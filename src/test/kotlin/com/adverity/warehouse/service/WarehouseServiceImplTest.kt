package com.adverity.warehouse.service

import com.adverity.warehouse.exception.EntityNotFoundException
import com.adverity.warehouse.import.ImportStatus
import com.adverity.warehouse.marketing.repository.MarketingRepository
import com.adverity.warehouse.search.query.Metric.CLICKS
import com.adverity.warehouse.search.query.SearchResult
import com.adverity.warehouse.search.query.WarehouseQuery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.BatchStatus.COMPLETED
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.data.domain.Pageable

@ExtendWith(MockKExtension::class)
internal class WarehouseServiceImplTest {

    @MockK
    private lateinit var marketingRepository: MarketingRepository

    @MockK
    private lateinit var jobLauncher: JobLauncher

    @MockK
    private lateinit var jobExplorer: JobExplorer

    @MockK
    private lateinit var importMarketingJob: Job

    @InjectMockKs
    private lateinit var warehouseService: WarehouseServiceImpl

    @Test
    fun `should search`() {
        // given
        every { marketingRepository.findByQuery(any(), any()) } returns listOf(searchResult)

        // when
        val results = warehouseService.search(clicksQuery, Pageable.unpaged())

        // then
        verify { marketingRepository.findByQuery(clicksQuery, Pageable.unpaged()) }
        assertThat(results).containsExactly(searchResult)
    }

    @Test
    fun `should get import status`() {
        // given
        every { jobExplorer.getJobExecution(any()) } returns jobExecution

        // when
        val result = warehouseService.getImportStatus(importId)

        // then
        verify { jobExplorer.getJobExecution(importId) }
        assertThat(result).isEqualTo(ImportStatus.COMPLETED)
    }

    @Test
    fun `should throw error on getting import status`() {
        // given
        every { jobExplorer.getJobExecution(any()) } returns null

        // when
        assertThatThrownBy { warehouseService.getImportStatus(importId) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Import not found")

        // then
        verify { jobExplorer.getJobExecution(importId) }
    }

    @Test
    fun `should import`() {
        // given
        every { jobLauncher.run(any(), any()) } returns jobExecution
        every { importMarketingJob.jobParametersIncrementer } returns jobParametersIncrementer
        every { importMarketingJob.name } returns jobName
        every { jobExplorer.getLastJobInstance(any()) } returns null

        // when
        val result = warehouseService.import(byteArray)

        // then
        verify { jobLauncher.run(eq(importMarketingJob), any()) }
        verify { importMarketingJob.jobParametersIncrementer }
        verify { importMarketingJob.name }
        verify { jobExplorer.getLastJobInstance(jobName) }
        assertThat(result).isEqualTo(importId)
    }

    private companion object Fixtures {
        const val importId = -100L
        const val jobName = "jobName"
        val byteArray = byteArrayOf(0x2E, 0x38)
        val clicksQuery = WarehouseQuery(metrics = setOf(CLICKS))
        val searchResult = SearchResult(
                clicks = 100
        )
        val jobExecution = createJobExecution()
        val jobParametersIncrementer = RunIdIncrementer()

        fun createJobExecution(): JobExecution {
            val jobExecution = JobExecution(importId)
            jobExecution.status = COMPLETED
            return jobExecution
        }
    }
}