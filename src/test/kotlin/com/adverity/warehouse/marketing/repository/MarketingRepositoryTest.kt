package com.adverity.warehouse.marketing.repository

import com.adverity.warehouse.search.query.CampaignFilter
import com.adverity.warehouse.search.query.CampaignGrouper
import com.adverity.warehouse.search.query.DatasourceFilter
import com.adverity.warehouse.search.query.DatasourceGrouper
import com.adverity.warehouse.search.query.DateFilter
import com.adverity.warehouse.search.query.DateGrouper.DATE
import com.adverity.warehouse.search.query.DateGrouper.DAY_OF_MONTH
import com.adverity.warehouse.search.query.DateGrouper.DAY_OF_YEAR
import com.adverity.warehouse.search.query.DateGrouper.MONTH
import com.adverity.warehouse.search.query.DateGrouper.QUARTER
import com.adverity.warehouse.search.query.DateGrouper.WEEK_OF_YEAR
import com.adverity.warehouse.search.query.DateGrouper.YEAR
import com.adverity.warehouse.search.query.Metric.AVERAGE_CLICKS
import com.adverity.warehouse.search.query.Metric.AVERAGE_IMPRESSIONS
import com.adverity.warehouse.search.query.Metric.CLICKS
import com.adverity.warehouse.search.query.Metric.CLICK_THROUGH_RATE
import com.adverity.warehouse.search.query.Metric.IMPRESSIONS
import com.adverity.warehouse.search.query.Metric.SUM_CLICKS
import com.adverity.warehouse.search.query.Metric.SUM_IMPRESSIONS
import com.adverity.warehouse.search.query.SearchResult
import com.adverity.warehouse.search.query.WarehouseQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable.unpaged
import org.springframework.data.domain.Range
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@DataJpaTest(properties = [
    "spring.test.database.replace=none",
    "spring.liquibase.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;;DATABASE_TO_LOWER=TRUE;INIT=CREATE SCHEMA IF NOT EXISTS DATA",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_LOWER=TRUE;INIT=CREATE SCHEMA IF NOT EXISTS DATA"])
@Sql("data.sql")
internal class MarketingRepositoryTest {

    @Autowired
    private lateinit var marketingRepository: MarketingRepository

    @Test
    fun `should find data for click metric`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 7L),
                SearchResult(clicks = 147L),
                SearchResult(clicks = 16L),
                SearchResult(clicks = 131L)
        )
    }

    @Test
    fun `should find data for impression metric`() {
        // given
        val query = WarehouseQuery(metrics = setOf(IMPRESSIONS))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(impressions = 22425L),
                SearchResult(impressions = 80351L),
                SearchResult(impressions = 45452L),
                SearchResult(impressions = 81906L)
        )
    }

    @Test
    fun `should find data for sum click metric`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 301L)
        )
    }

    @Test
    fun `should find data for sum impressions metric`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_IMPRESSIONS))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumImpressions = 230134L)
        )
    }

    @Test
    fun `should find data for click through rate metric`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICK_THROUGH_RATE))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clickThroughRate = 0.0013079336386626922)
        )
    }

    @Test
    fun `should find data for click metric for datasource`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(DatasourceFilter(name = "Google Ads")))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 7L),
                SearchResult(clicks = 147L)
        )
    }

    @Test
    fun `should find data for click metric for campaign`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(CampaignFilter(name = "Firmen Mitgliedschaft")))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 16L),
                SearchResult(clicks = 131L)
        )
    }

    @Test
    fun `should find data for click metric for date`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(DateFilter(date = LocalDate.of(2019, 11, 14))))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 147L)
        )
    }

    @Test
    fun `should find data for click metric for date year`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(DateFilter(year = 2019)))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactlyInAnyOrder(
                SearchResult(clicks = 16L),
                SearchResult(clicks = 147L),
                SearchResult(clicks = 131L)
        )
    }

    @Test
    fun `should find data for click metric for date month`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(DateFilter(month = 10)))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 131L)
        )
    }

    @Test
    fun `should find data for click metric for date day of year`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(DateFilter(dayOfYear = 319)))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 131L)
        )
    }

    @Test
    fun `should find data for click metric for date week of year`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(DateFilter(weekOfYear = 10)))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 16L)
        )
    }

    @Test
    fun `should find data for click metric for date quarter`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(DateFilter(quarter = 1)))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 16L)
        )
    }

    @Test
    fun `should find data for click metric for date range`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters =
        setOf(DateFilter(range = Range.closed(LocalDate.of(2019, 3, 10), LocalDate.of(2019, 10, 18)))))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 16L),
                SearchResult(clicks = 131L)
        )
    }

    @Test
    fun `should find data for click metric for date year and month`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS), filters = setOf(DateFilter(year = 2019, month = 3)))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 16L)
        )
    }

    @Test
    fun `should find data for sum of click metric and group by datasource`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(DatasourceGrouper.NAME))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 154L, datasourceName = "Google Ads"),
                SearchResult(sumClicks = 147L, datasourceName = "Twitter Ads")
        )
    }

    @Test
    fun `should find data for sum of click metric and group by campaign`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(CampaignGrouper.NAME))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 154L, campaignName = "Adventmarkt Touristik"),
                SearchResult(sumClicks = 147L, campaignName = "Firmen Mitgliedschaft")
        )
    }

    @Test
    fun `should find data for sum of click metric and group by date`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(DATE))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 7L, date = LocalDate.of(2018, 11, 12)),
                SearchResult(sumClicks = 16L, date = LocalDate.of(2019, 3, 13)),
                SearchResult(sumClicks = 131L, date = LocalDate.of(2019, 10, 15)),
                SearchResult(sumClicks = 147L, date = LocalDate.of(2019, 11, 14))
        )
    }

    @Test
    fun `should find data for sum of click metric and group by date year`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(YEAR))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 7L, year = 2018),
                SearchResult(sumClicks = 294L, year = 2019)
        )
    }

    @Test
    fun `should find data for sum of click metric and group by date month`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(MONTH))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 16L, month = 3),
                SearchResult(sumClicks = 131L, month = 10),
                SearchResult(sumClicks = 154L, month = 11)
        )
    }

    @Test
    fun `should find data for sum of click metric and group by day of month`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(DAY_OF_MONTH))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 7L, dayOfMonth = 12),
                SearchResult(sumClicks = 16L, dayOfMonth = 13),
                SearchResult(sumClicks = 147L, dayOfMonth = 14),
                SearchResult(sumClicks = 131L, dayOfMonth = 15)
        )
    }

    @Test
    fun `should find data for sum of click metric and group by day of year`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(DAY_OF_YEAR))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 16L, dayOfYear = 72),
                SearchResult(sumClicks = 7L, dayOfYear = 316),
                SearchResult(sumClicks = 147L, dayOfYear = 318),
                SearchResult(sumClicks = 131L, dayOfYear = 319)
        )
    }

    @Test
    fun `should find data for sum of click metric and group by week of year`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(WEEK_OF_YEAR))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 16L, weekOfYear = 10),
                SearchResult(sumClicks = 285L, weekOfYear = 46)
        )
    }

    @Test
    fun `should find data for sum of click metric and group by quarter`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS), groupers = setOf(QUARTER))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 16L, quarter = 1),
                SearchResult(sumClicks = 285L, quarter = 4)
        )
    }

    @Test
    fun `should find data for sum of click, sum of impressions metric filter by datasource and group by year and month`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS, SUM_IMPRESSIONS),
                filters = setOf(DatasourceFilter(name = "Google Ads")),
                groupers = setOf(YEAR, MONTH))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 7L, sumImpressions = 22425L, year = 2018, month = 11),
                SearchResult(sumClicks = 147L, sumImpressions = 80351L, year = 2019, month = 11)
        )
    }

    @Test
    fun `should find data for sum of click, sum of impressions metric filter by datasource and group by month and week of year`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS, SUM_IMPRESSIONS),
                filters = setOf(DatasourceFilter(name = "Google Ads")),
                groupers = setOf(MONTH, WEEK_OF_YEAR))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 154L, sumImpressions = 102776L, month = 11, weekOfYear = 46)
        )
    }

    @Test
    fun `should find data for sum of click metric filter by datasource and date range`() {
        // given
        val query = WarehouseQuery(metrics = setOf(SUM_CLICKS),
                filters = setOf(DatasourceFilter(name = "Google Ads"),
                        DateFilter(range = Range.closed(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 12, 30)))))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(sumClicks = 7L)
        )
    }

    @Test
    fun `should find data for click through rate metric grouped by datasource and campaign`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICK_THROUGH_RATE),
                groupers = setOf(DatasourceGrouper.NAME, CampaignGrouper.NAME))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(clickThroughRate = 0.0014984042967229703, datasourceName = "Google Ads", campaignName = "Adventmarkt Touristik"),
                SearchResult(clickThroughRate = 0.0011542266681323513, datasourceName = "Twitter Ads", campaignName = "Firmen Mitgliedschaft")
        )
    }

    @Test
    fun `should find data for impressions metric grouped by date`() {
        // given
        val query = WarehouseQuery(metrics = setOf(IMPRESSIONS), groupers = setOf(DATE))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(impressions = 22425L, date = LocalDate.of(2018, 11, 12)),
                SearchResult(impressions = 45452L, date = LocalDate.of(2019, 3, 13)),
                SearchResult(impressions = 81906L, date = LocalDate.of(2019, 10, 15)),
                SearchResult(impressions = 80351L, date = LocalDate.of(2019, 11, 14))
        )
    }

    @Test
    fun `should find data for click metric for first page`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS))

        // when
        val results = marketingRepository.findByQuery(query, PageRequest.of(0, 1))

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 7L)
        )
    }

    @Test
    fun `should find data for click metric for last page`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS))

        // when
        val results = marketingRepository.findByQuery(query, PageRequest.of(3, 1))

        // then
        assertThat(results).containsExactly(
                SearchResult(clicks = 131L)
        )
    }

    @Test
    fun `should find data for click metric that extends last page`() {
        // given
        val query = WarehouseQuery(metrics = setOf(CLICKS))

        // when
        val results = marketingRepository.findByQuery(query, PageRequest.of(4, 1))

        // then
        assertThat(results).isEmpty()
    }

    @Test
    fun `should find data for average click metric`() {
        // given
        val query = WarehouseQuery(metrics = setOf(AVERAGE_CLICKS))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(averageClicks = 75.25)
        )
    }

    @Test
    fun `should find data for average click metric filtered by datasource name`() {
        // given
        val query = WarehouseQuery(metrics = setOf(AVERAGE_CLICKS), filters = setOf(DatasourceFilter(name = "Google Ads")))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(averageClicks = 77.0)
        )
    }

    @Test
    fun `should find data for average impressions metric`() {
        // given
        val query = WarehouseQuery(metrics = setOf(AVERAGE_IMPRESSIONS))

        // when
        val results = marketingRepository.findByQuery(query, unpaged())

        // then
        assertThat(results).containsExactly(
                SearchResult(averageClicks = 57533.5)
        )
    }

}