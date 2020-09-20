package com.adverity.warehouse.controller

import com.adverity.warehouse.import.ImportStatus.COMPLETED
import com.adverity.warehouse.marketing.repository.MarketingRepository
import com.adverity.warehouse.search.query.CampaignFilter
import com.adverity.warehouse.search.query.CampaignGrouper
import com.adverity.warehouse.search.query.DatasourceFilter
import com.adverity.warehouse.search.query.DatasourceGrouper
import com.adverity.warehouse.search.query.DateFilter
import com.adverity.warehouse.search.query.DateGrouper
import com.adverity.warehouse.search.query.Metric.AVERAGE_CLICKS
import com.adverity.warehouse.search.query.Metric.AVERAGE_IMPRESSIONS
import com.adverity.warehouse.search.query.Metric.CLICKS
import com.adverity.warehouse.search.query.Metric.CLICK_THROUGH_RATE
import com.adverity.warehouse.search.query.Metric.IMPRESSIONS
import com.adverity.warehouse.search.query.Metric.SUM_CLICKS
import com.adverity.warehouse.search.query.Metric.SUM_IMPRESSIONS
import com.adverity.warehouse.search.query.SearchResult
import com.adverity.warehouse.search.query.WarehouseQuery
import com.adverity.warehouse.search.query.dto.CampaignFilterDTO
import com.adverity.warehouse.search.query.dto.CampaignFilterDTO.CampaignConditionDTO
import com.adverity.warehouse.search.query.dto.CampaignGrouperDTO
import com.adverity.warehouse.search.query.dto.CampaignGrouperDTO.CampaignAttribute
import com.adverity.warehouse.search.query.dto.DatasourceFilterDTO
import com.adverity.warehouse.search.query.dto.DatasourceFilterDTO.DatasourceConditionDTO
import com.adverity.warehouse.search.query.dto.DatasourceGrouperDTO
import com.adverity.warehouse.search.query.dto.DatasourceGrouperDTO.DatasourceAttribute
import com.adverity.warehouse.search.query.dto.DateFilterDTO
import com.adverity.warehouse.search.query.dto.DateFilterDTO.DateConditionDTO
import com.adverity.warehouse.search.query.dto.DateGrouperDTO
import com.adverity.warehouse.search.query.dto.DateGrouperDTO.DateAttribute.DATE
import com.adverity.warehouse.search.query.dto.DateGrouperDTO.DateAttribute.DAY_OF_MONTH
import com.adverity.warehouse.search.query.dto.DateGrouperDTO.DateAttribute.DAY_OF_YEAR
import com.adverity.warehouse.search.query.dto.DateGrouperDTO.DateAttribute.MONTH
import com.adverity.warehouse.search.query.dto.DateGrouperDTO.DateAttribute.QUARTER
import com.adverity.warehouse.search.query.dto.DateGrouperDTO.DateAttribute.WEEK_OF_YEAR
import com.adverity.warehouse.search.query.dto.DateGrouperDTO.DateAttribute.YEAR
import com.adverity.warehouse.search.query.dto.RangeDTO
import com.adverity.warehouse.search.query.dto.WarehouseQueryDTO
import com.adverity.warehouse.search.query.response.dto.SearchResponseDTO
import com.adverity.warehouse.search.query.response.dto.SearchResultDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Range
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate


@ExtendWith(MockKExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
internal class WarehouseControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var jobExplorer: JobExplorer

    @MockkBean
    private lateinit var jobLauncher: JobLauncher

    @MockkBean
    private lateinit var importMarketingJob: Job

    @MockkBean
    private lateinit var marketingRepository: MarketingRepository

    @Test
    fun `should get import status`() {
        // given
        every { jobExplorer.getJobExecution(any()) } returns jobExecution

        // when
        mvc.perform(get("/api/warehouse/import/{id}/status", importId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status", `is`(COMPLETED.name)))

        // then
        verify { jobExplorer.getJobExecution(importId) }
    }

    @Test
    fun `should import file`() {
        // given
        val file = MockMultipartFile("file", "filename.csv", "text/csv", "some text".toByteArray())
        every { jobLauncher.run(any(), any()) } returns jobExecution
        every { importMarketingJob.jobParametersIncrementer } returns jobParametersIncrementer
        every { importMarketingJob.name } returns jobName
        every { jobExplorer.getLastJobInstance(any()) } returns null

        // when
        mvc.perform(multipart("/api/warehouse/import")
                .file(file)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated)
                .andExpect(header().string("location", importId.toString()))

        // then
        verify { jobLauncher.run(eq(importMarketingJob), any()) }
        verify { importMarketingJob.jobParametersIncrementer }
        verify { importMarketingJob.name }
        verify { jobExplorer.getLastJobInstance(any()) }
    }

    @Test
    fun `should search`() {
        // given
        every { marketingRepository.findByQuery(any(), any()) } returns listOf(searchResult)

        // when
        val responseAsString = mvc.perform(post("/api/warehouse/search")
                .content(asJsonString(warehouseQueryDTO))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString

        // then
        verify { marketingRepository.findByQuery(warehouseQuery, PageRequest.of(page - 1, size)) }

        assertThat(responseAsString).isEqualTo(asJsonString(SearchResponseDTO(listOf(searchResultDTO))))
    }

    @Test
    fun `should return bad request when searching with page set to zero`() {
        // when
        mvc.perform(post("/api/warehouse/search")
                .content(asJsonString(warehouseQueryDTO.copy(page = 0)))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.messages[0]", `is`("Page cannot be less than one")))
    }

    @Test
    fun `should return bad request when searching with size set to zero`() {
        // when
        mvc.perform(post("/api/warehouse/search")
                .content(asJsonString(warehouseQueryDTO.copy(size = 0)))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.messages[0]", `is`("Size cannot be less than one")))
    }

    @Test
    fun `should return bad request when searching with size set to one hundred one`() {
        // when
        mvc.perform(post("/api/warehouse/search")
                .content(asJsonString(warehouseQueryDTO.copy(size = 101)))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.messages[0]", `is`("Size cannot be greater than one hundred")))
    }

    @Test
    fun `should return bad request when searching with empty metrics`() {
        // when
        mvc.perform(post("/api/warehouse/search")
                .content(asJsonString(warehouseQueryDTO.copy(metrics = emptySet())))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.messages[0]", `is`("Metrics cannot be empty")))
    }

    fun asJsonString(obj: Any): String = objectMapper.writeValueAsString(obj)

    private companion object Fixtures {
        const val importId = -100L
        const val jobName = "jobName"
        const val page = 1
        const val size = 10
        val warehouseQueryDTO = WarehouseQueryDTO(
                metrics = setOf(CLICK_THROUGH_RATE, SUM_CLICKS, SUM_IMPRESSIONS, AVERAGE_IMPRESSIONS, AVERAGE_CLICKS, CLICKS, IMPRESSIONS),
                filters = setOf(DatasourceFilterDTO(DatasourceConditionDTO(name = "name")),
                        CampaignFilterDTO(CampaignConditionDTO(name = "name")),
                        DateFilterDTO(DateConditionDTO(
                                date = LocalDate.of(2020, 9, 15),
                                year = 2020,
                                month = 9,
                                dayOfMonth = 15,
                                dayOfYear = 100,
                                range = RangeDTO(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1)),
                                weekOfYear = 99,
                                quarter = 3
                        ))),
                groupers = setOf(DatasourceGrouperDTO(DatasourceAttribute.NAME),
                        CampaignGrouperDTO(CampaignAttribute.NAME),
                        DateGrouperDTO(DATE), DateGrouperDTO(YEAR), DateGrouperDTO(MONTH), DateGrouperDTO(DAY_OF_MONTH),
                        DateGrouperDTO(DAY_OF_YEAR), DateGrouperDTO(WEEK_OF_YEAR), DateGrouperDTO(QUARTER)),
                page = page,
                size = size
        )
        val warehouseQuery = WarehouseQuery(
                metrics = setOf(CLICK_THROUGH_RATE, SUM_CLICKS, SUM_IMPRESSIONS, AVERAGE_CLICKS, AVERAGE_IMPRESSIONS, CLICKS, IMPRESSIONS),
                filters = setOf(DatasourceFilter(name = "name"),
                        CampaignFilter(name = "name"),
                        DateFilter(
                                date = LocalDate.of(2020, 9, 15),
                                year = 2020,
                                month = 9,
                                dayOfMonth = 15,
                                dayOfYear = 100,
                                range = Range.closed(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1)),
                                weekOfYear = 99,
                                quarter = 3
                        )),
                groupers = setOf(DatasourceGrouper.NAME,
                        CampaignGrouper.NAME,
                        DateGrouper.DATE, DateGrouper.YEAR, DateGrouper.MONTH, DateGrouper.DAY_OF_MONTH,
                        DateGrouper.DAY_OF_YEAR, DateGrouper.WEEK_OF_YEAR, DateGrouper.QUARTER)
        )
        val searchResult = SearchResult(
                clicks = 1,
                impressions = 1,
                clickThroughRate = 1.1,
                sumClicks = 1,
                sumImpressions = 1,
                averageImpressions = 2.0,
                averageClicks = 3.0,
                datasourceName = "datasourceName",
                campaignName = "campaignName",
                date = LocalDate.of(2020, 9, 10),
                year = 2019,
                month = 9,
                dayOfMonth = 10,
                dayOfYear = 100,
                weekOfYear = 99,
                quarter = 3
        )
        val searchResultDTO = SearchResultDTO(
                clicks = 1,
                impressions = 1,
                clickThroughRate = 1.1,
                sumClicks = 1,
                sumImpressions = 1,
                averageImpressions = 2.0,
                averageClicks = 3.0,
                datasourceName = "datasourceName",
                campaignName = "campaignName",
                date = LocalDate.of(2020, 9, 10),
                year = 2019,
                month = 9,
                dayOfMonth = 10,
                dayOfYear = 100,
                weekOfYear = 99,
                quarter = 3
        )

        val jobExecution = createJobExecution()
        val jobParametersIncrementer = RunIdIncrementer()

        fun createJobExecution(): JobExecution {
            val jobExecution = JobExecution(importId)
            jobExecution.status = BatchStatus.COMPLETED
            return jobExecution
        }
    }
}