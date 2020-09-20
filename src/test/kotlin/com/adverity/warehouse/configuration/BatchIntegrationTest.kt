package com.adverity.warehouse.configuration

import com.adverity.warehouse.csvprocessing.ResourceJobParameter
import com.adverity.warehouse.marketing.repository.MarketingRepository
import io.mockk.junit5.MockKExtension
import org.awaitility.Awaitility.await
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDate
import java.util.concurrent.TimeUnit.SECONDS


@ExtendWith(MockKExtension::class)
@SpringBatchTest
@SpringBootTest(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
internal class BatchIntegrationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var jobRepositoryTestUtils: JobRepositoryTestUtils

    @Autowired
    private lateinit var marketinRepository: MarketingRepository

    @AfterEach
    fun cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    private fun defaultJobParameters(): JobParameters? {
        val paramsBuilder = JobParametersBuilder()
        val resourceJobParameter = ResourceJobParameter(ClassPathResource("input/test-data.csv"))
        paramsBuilder.addParameter("resource", resourceJobParameter)
        return paramsBuilder.toJobParameters()
    }

    @Test
    @Throws(Exception::class)
    fun `should execute job with success`() {
        // when
        val jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters()!!)
        val actualJobInstance = jobExecution.jobInstance

        // then
        assertThat(actualJobInstance.jobName, `is`("importMarketingJob"))
        await().atMost(10, SECONDS).until({ jobExecution.exitStatus.exitCode }, `is`("COMPLETED"))

        val marketings = marketinRepository.findAll().toList()
        assertThat(marketings, hasSize(2))
        assertThat(marketings[0], hasProperty("clicksAmount", equalTo(7L)))
        assertThat(marketings[0], hasProperty("impressionsAmount", equalTo(22425L)))
        assertThat(marketings[0].datasourceDimension, hasProperty("name", equalTo("Google Ads")))
        assertThat(marketings[0].campaignDimension, hasProperty("name", equalTo("Adventmarkt Touristik")))
        assertThat(marketings[0].dateDimension, hasProperty("date", equalTo(LocalDate.of(2019, 11, 12))))
        assertThat(marketings[0].dateDimension, hasProperty("year", equalTo(2019)))
        assertThat(marketings[0].dateDimension, hasProperty("month", equalTo(11)))
        assertThat(marketings[0].dateDimension, hasProperty("dayOfMonth", equalTo(12)))
        assertThat(marketings[0].dateDimension, hasProperty("dayOfYear", equalTo(316)))
        assertThat(marketings[0].dateDimension, hasProperty("weekOfYear", equalTo(46)))
        assertThat(marketings[0].dateDimension, hasProperty("quarter", equalTo(4)))

        assertThat(marketings[1], hasProperty("clicksAmount", equalTo(16L)))
        assertThat(marketings[1], hasProperty("impressionsAmount", equalTo(45452L)))
        assertThat(marketings[1].datasourceDimension, hasProperty("name", equalTo("Google Ads2")))
        assertThat(marketings[1].campaignDimension, hasProperty("name", equalTo("Adventmarkt Touristik2")))
        assertThat(marketings[1].dateDimension, hasProperty("date", equalTo(LocalDate.of(2019, 11, 13))))
        assertThat(marketings[1].dateDimension, hasProperty("year", equalTo(2019)))
        assertThat(marketings[1].dateDimension, hasProperty("month", equalTo(11)))
        assertThat(marketings[1].dateDimension, hasProperty("dayOfMonth", equalTo(13)))
        assertThat(marketings[1].dateDimension, hasProperty("dayOfYear", equalTo(317)))
        assertThat(marketings[1].dateDimension, hasProperty("weekOfYear", equalTo(46)))
        assertThat(marketings[1].dateDimension, hasProperty("quarter", equalTo(4)))
    }

}