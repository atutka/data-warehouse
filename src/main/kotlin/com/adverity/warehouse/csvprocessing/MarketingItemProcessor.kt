package com.adverity.warehouse.csvprocessing

import com.adverity.warehouse.marketing.domain.CampaignDimension
import com.adverity.warehouse.marketing.domain.DatasourceDimension
import com.adverity.warehouse.marketing.domain.DateDimension
import com.adverity.warehouse.marketing.domain.Marketing
import com.adverity.warehouse.marketing.repository.CampaignDimensionRepository
import com.adverity.warehouse.marketing.repository.DatasourceDimensionRepository
import com.adverity.warehouse.marketing.repository.DateDimensionRepository
import org.springframework.batch.item.ItemProcessor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

private const val DATE_TIME_PATTERN = "MM/dd/yy"

internal class MarketingItemProcessor(
        private val campaignDimensionRepository: CampaignDimensionRepository,
        private val datasourceDimensionRepository: DatasourceDimensionRepository,
        private val dateDimensionRepository: DateDimensionRepository
) : ItemProcessor<CsvRow, Marketing> {

    override fun process(item: CsvRow): Marketing? {
        val campaignDimension = campaignDimensionRepository.findByName(item.campaign!!)
                ?: campaignDimensionRepository.save(CampaignDimension(name = item.campaign.toString()))
        val datasourceDimension = datasourceDimensionRepository.findByName(item.datasource!!)
                ?: datasourceDimensionRepository.save(DatasourceDimension(name = item.datasource.toString()))
        val date = LocalDate.parse(item.daily!!, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
        val dateDimension = dateDimensionRepository.findByDate(date)
                ?: dateDimensionRepository.save(date.toDateDimension())

        return Marketing(
                clicksAmount = item.clicks!!,
                impressionsAmount = item.impressions!!,
                datasourceDimension = datasourceDimension,
                campaignDimension = campaignDimension,
                dateDimension = dateDimension
        )
    }

    private fun LocalDate.toDateDimension() =
            DateDimension(
                    date = this,
                    year = year,
                    month = monthValue,
                    dayOfMonth = dayOfMonth,
                    dayOfYear = dayOfYear,
                    quarter = when {
                        monthValue < 4 -> 1
                        monthValue < 7 -> 2
                        monthValue < 10 -> 3
                        else -> 4
                    },
                    weekOfYear = this.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
            )
}