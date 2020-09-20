package com.adverity.warehouse.service

import com.adverity.warehouse.csvprocessing.ResourceJobParameter
import com.adverity.warehouse.exception.EntityNotFoundException
import com.adverity.warehouse.import.ImportStatus
import com.adverity.warehouse.import.ImportStatus.COMPLETED
import com.adverity.warehouse.import.ImportStatus.FAILED
import com.adverity.warehouse.import.ImportStatus.IN_PROGRESS
import com.adverity.warehouse.marketing.repository.MarketingRepository
import com.adverity.warehouse.search.query.SearchResult
import com.adverity.warehouse.search.query.WarehouseQuery
import mu.KotlinLogging
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.BatchStatus.ABANDONED
import org.springframework.batch.core.BatchStatus.STARTED
import org.springframework.batch.core.BatchStatus.STARTING
import org.springframework.batch.core.BatchStatus.STOPPED
import org.springframework.batch.core.BatchStatus.STOPPING
import org.springframework.batch.core.BatchStatus.UNKNOWN
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.Date

private val logger = KotlinLogging.logger {}

private const val RESOURCE_PARAMETER_NAME = "resource"
private const val DATE_PARAMETER_NAME = "date"

@Service
internal class WarehouseServiceImpl(
        private val marketingRepository: MarketingRepository,
        private val jobLauncher: JobLauncher,
        private val jobExplorer: JobExplorer,
        private val importMarketingJob: Job
) : WarehouseService {

    override fun import(file: ByteArray): Long {
        logger.debug { "Importing file" }
        val jobParametersBuilder = JobParametersBuilder(jobExplorer)
        jobParametersBuilder.getNextJobParameters(importMarketingJob)
        jobParametersBuilder.addParameter(RESOURCE_PARAMETER_NAME, ResourceJobParameter(ByteArrayResource(file)))
        jobParametersBuilder.addDate(DATE_PARAMETER_NAME, Date.from(LocalDateTime.now().toInstant(UTC)))
        val jobExecution = jobLauncher.run(importMarketingJob, jobParametersBuilder.toJobParameters())
        return jobExecution.id
    }

    @Transactional
    override fun getImportStatus(id: Long): ImportStatus {
        val jobExecution = jobExplorer.getJobExecution(id) ?: throw EntityNotFoundException("Import not found")
        return when (jobExecution.status) {
            STARTING -> ImportStatus.INIT
            STARTED, STOPPED, STOPPING -> IN_PROGRESS
            BatchStatus.FAILED, ABANDONED, UNKNOWN -> FAILED
            BatchStatus.COMPLETED -> COMPLETED
        }
    }

    @Transactional
    override fun search(query: WarehouseQuery, pageable: Pageable): List<SearchResult> {
        logger.debug { "Searching marketing data for $query" }
        return marketingRepository.findByQuery(query, pageable)
    }

}