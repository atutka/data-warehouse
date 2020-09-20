package com.adverity.warehouse.configuration

import com.adverity.warehouse.csvprocessing.CsvRow
import com.adverity.warehouse.csvprocessing.CsvRow.Companion.CAMPAIGN_NAME
import com.adverity.warehouse.csvprocessing.CsvRow.Companion.CLICKS_NAME
import com.adverity.warehouse.csvprocessing.CsvRow.Companion.DAILY_NAME
import com.adverity.warehouse.csvprocessing.CsvRow.Companion.DATASOURCE_NAME
import com.adverity.warehouse.csvprocessing.CsvRow.Companion.IMPRESSIONS_NAME
import com.adverity.warehouse.csvprocessing.MarketingItemProcessor
import com.adverity.warehouse.marketing.domain.Marketing
import com.adverity.warehouse.marketing.repository.CampaignDimensionRepository
import com.adverity.warehouse.marketing.repository.DatasourceDimensionRepository
import com.adverity.warehouse.marketing.repository.DateDimensionRepository
import com.adverity.warehouse.marketing.repository.MarketingRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.item.data.RepositoryItemWriter
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.task.SimpleAsyncTaskExecutor

@Configuration
@EnableBatchProcessing
internal class BatchConfiguration {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Bean
    @StepScope
    fun reader(@Value("#{jobParameters['resource']}") resource: Resource): FlatFileItemReader<CsvRow> {
        return FlatFileItemReaderBuilder<CsvRow>()
                .name("csvRowItemReader")
                .resource(resource)
                .delimited()
                .names(DATASOURCE_NAME, CAMPAIGN_NAME, DAILY_NAME, CLICKS_NAME, IMPRESSIONS_NAME)
                .fieldSetMapper(object : BeanWrapperFieldSetMapper<CsvRow>() {
                    init {
                        setTargetType(CsvRow::class.java)
                    }
                })
                .linesToSkip(1)
                .build()
    }

    @Bean
    fun processor(campaignDimensionRepository: CampaignDimensionRepository,
                  datasourceDimensionRepository: DatasourceDimensionRepository,
                  dateDimensionRepository: DateDimensionRepository): MarketingItemProcessor {
        return MarketingItemProcessor(campaignDimensionRepository, datasourceDimensionRepository, dateDimensionRepository)
    }

    @Bean
    fun writer(marketingRepository: MarketingRepository): RepositoryItemWriter<Marketing> {
        return RepositoryItemWriterBuilder<Marketing>()
                .repository(marketingRepository)
                .methodName("save")
                .build()
    }

    @Bean
    fun importMarketingJob(step: Step): Job {
        return jobBuilderFactory["importMarketingJob"]
                .incrementer(RunIdIncrementer())
                .flow(step)
                .end()
                .build()
    }

    @Bean
    fun step(writer: RepositoryItemWriter<Marketing>,
             processor: MarketingItemProcessor,
             reader: FlatFileItemReader<CsvRow>): Step {
        return stepBuilderFactory["transformCsvRowToMarketing"]
                .chunk<CsvRow, Marketing>(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build()
    }

    @Bean
    fun jobLauncher(jobRepository: JobRepository): JobLauncher {
        val jobLauncher = SimpleJobLauncher()
        jobLauncher.setJobRepository(jobRepository)
        jobLauncher.setTaskExecutor(SimpleAsyncTaskExecutor())
        jobLauncher.afterPropertiesSet()
        return jobLauncher
    }

}