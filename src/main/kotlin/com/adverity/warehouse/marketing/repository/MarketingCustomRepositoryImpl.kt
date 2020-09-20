package com.adverity.warehouse.marketing.repository

import com.adverity.warehouse.marketing.domain.CampaignDimension
import com.adverity.warehouse.marketing.domain.DatasourceDimension
import com.adverity.warehouse.marketing.domain.DateDimension
import com.adverity.warehouse.marketing.domain.DateDimension.Companion.PROPERTY_DATE
import com.adverity.warehouse.marketing.domain.DateDimension.Companion.PROPERTY_DAY_OF_MONTH
import com.adverity.warehouse.marketing.domain.DateDimension.Companion.PROPERTY_DAY_OF_YEAR
import com.adverity.warehouse.marketing.domain.DateDimension.Companion.PROPERTY_MONTH
import com.adverity.warehouse.marketing.domain.DateDimension.Companion.PROPERTY_QUARTER
import com.adverity.warehouse.marketing.domain.DateDimension.Companion.PROPERTY_WEEK_OF_YEAR
import com.adverity.warehouse.marketing.domain.DateDimension.Companion.PROPERTY_YEAR
import com.adverity.warehouse.marketing.domain.Marketing
import com.adverity.warehouse.marketing.domain.Marketing.Companion.PROPERTY_CLICKS_AMOUNT
import com.adverity.warehouse.marketing.domain.Marketing.Companion.PROPERTY_IMPRESSIONS_AMOUNT
import com.adverity.warehouse.search.query.CampaignFilter
import com.adverity.warehouse.search.query.CampaignGrouper
import com.adverity.warehouse.search.query.DatasourceFilter
import com.adverity.warehouse.search.query.DatasourceGrouper
import com.adverity.warehouse.search.query.DateFilter
import com.adverity.warehouse.search.query.DateGrouper
import com.adverity.warehouse.search.query.DateGrouper.DATE
import com.adverity.warehouse.search.query.DateGrouper.DAY_OF_MONTH
import com.adverity.warehouse.search.query.DateGrouper.DAY_OF_YEAR
import com.adverity.warehouse.search.query.DateGrouper.MONTH
import com.adverity.warehouse.search.query.DateGrouper.QUARTER
import com.adverity.warehouse.search.query.DateGrouper.WEEK_OF_YEAR
import com.adverity.warehouse.search.query.DateGrouper.YEAR
import com.adverity.warehouse.search.query.Filter
import com.adverity.warehouse.search.query.Grouper
import com.adverity.warehouse.search.query.Metric
import com.adverity.warehouse.search.query.Metric.AVERAGE_CLICKS
import com.adverity.warehouse.search.query.Metric.AVERAGE_IMPRESSIONS
import com.adverity.warehouse.search.query.Metric.CLICKS
import com.adverity.warehouse.search.query.Metric.CLICK_THROUGH_RATE
import com.adverity.warehouse.search.query.Metric.IMPRESSIONS
import com.adverity.warehouse.search.query.Metric.SUM_CLICKS
import com.adverity.warehouse.search.query.Metric.SUM_IMPRESSIONS
import com.adverity.warehouse.search.query.SearchResult
import com.adverity.warehouse.search.query.WarehouseQuery
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.persistence.criteria.Selection

private const val ATTRIBUTE_NAME_DATASOURCE_DIMENSION = "datasourceDimension"
private const val ATTRIBUTE_NAME_CAMPAIGN_DIMENSION = "campaignDimension"
private const val ATTRIBUTE_NAME_DATE_DIMENSION = "dateDimension"

@Component
internal class MarketingCustomRepositoryImpl(
        @PersistenceContext
        private val entityManager: EntityManager
) : MarketingCustomRepository {

    override fun findByQuery(warehouseQuery: WarehouseQuery, pageable: Pageable): List<SearchResult> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val query = criteriaBuilder.createQuery()
        val marketing = query.from(Marketing::class.java)
        val metricExpressions: List<Selection<out Any>> = warehouseQuery.metrics.map {
            mapMetricToSelection(it, marketing, criteriaBuilder)
        }
        val predicates: List<Predicate> = warehouseQuery.filters.map {
            mapFilterToPredicate(it, marketing, criteriaBuilder)
        }
        val groupBy = warehouseQuery.groupers.map {
            mapGrouperToPath(it, marketing)
        }
        val metricsGroupers = if (groupBy.isNotEmpty())
            warehouseQuery.metrics
                    .filter { it == CLICKS || it == IMPRESSIONS }
                    .map {
                        if (it == CLICKS)
                            marketing.get<Long>(PROPERTY_CLICKS_AMOUNT)
                        else
                            marketing.get<Long>(PROPERTY_IMPRESSIONS_AMOUNT)
                    }
        else
            emptyList()
        val typedQuery = entityManager.createQuery(query.multiselect(metricExpressions + groupBy)
                .where(criteriaBuilder.and(*predicates.toTypedArray()))
                .groupBy(groupBy + metricsGroupers))
        if (pageable != Pageable.unpaged()) {
            typedQuery
                    .setFirstResult(pageable.offset.toInt())
                    .setMaxResults(pageable.pageSize)
        }
        val results = typedQuery.resultList
        val numberOfMetrics = warehouseQuery.metrics.size
        val numberOfGroups = warehouseQuery.groupers.size
        return results.map {
            mapResult(it, numberOfMetrics, numberOfGroups, warehouseQuery)
        }
    }

    private fun mapMetricToSelection(metric: Metric, marketing: Root<Marketing>, criteriaBuilder: CriteriaBuilder): Selection<out Any> =
            when (metric) {
                CLICKS -> marketing.get<Long>(PROPERTY_CLICKS_AMOUNT)
                IMPRESSIONS -> marketing.get<Long>(PROPERTY_IMPRESSIONS_AMOUNT)
                SUM_CLICKS -> criteriaBuilder.sum(marketing.get<Long>(PROPERTY_CLICKS_AMOUNT))
                SUM_IMPRESSIONS -> criteriaBuilder.sum(marketing.get<Long>(PROPERTY_IMPRESSIONS_AMOUNT))
                CLICK_THROUGH_RATE -> criteriaBuilder.quot(
                        criteriaBuilder.prod(
                                criteriaBuilder.sum(marketing.get<Long>(PROPERTY_CLICKS_AMOUNT)),
                                criteriaBuilder.literal(1.0)),
                        criteriaBuilder.sum(marketing.get<Long>(PROPERTY_IMPRESSIONS_AMOUNT))
                )
                AVERAGE_CLICKS -> criteriaBuilder.quot(
                        criteriaBuilder.prod(
                                criteriaBuilder.sum(marketing.get<Long>(PROPERTY_CLICKS_AMOUNT)),
                                criteriaBuilder.literal(1.0)),
                        criteriaBuilder.count(marketing.get<Long>(PROPERTY_CLICKS_AMOUNT))
                )
                AVERAGE_IMPRESSIONS -> criteriaBuilder.quot(
                        criteriaBuilder.prod(
                                criteriaBuilder.sum(marketing.get<Long>(PROPERTY_IMPRESSIONS_AMOUNT)),
                                criteriaBuilder.literal(1.0)),
                        criteriaBuilder.count(marketing.get<Long>(PROPERTY_IMPRESSIONS_AMOUNT))
                )
            }

    private fun mapFilterToPredicate(it: Filter, marketing: Root<Marketing>, criteriaBuilder: CriteriaBuilder): Predicate =
            when (it) {
                is DatasourceFilter -> {
                    val datasourceDimension = marketing.join<Marketing, DatasourceDimension>(ATTRIBUTE_NAME_DATASOURCE_DIMENSION)
                    criteriaBuilder.equal(datasourceDimension.get<String>(DatasourceDimension.PROPERTY_NAME), it.name)
                }
                is CampaignFilter -> {
                    val campaignDimension = marketing.join<Marketing, CampaignDimension>(ATTRIBUTE_NAME_CAMPAIGN_DIMENSION)
                    criteriaBuilder.equal(campaignDimension.get<String>(CampaignDimension.PROPERTY_NAME), it.name)
                }
                is DateFilter -> {
                    val dateDimension = marketing.join<Marketing, DateDimension>(ATTRIBUTE_NAME_DATE_DIMENSION)
                    val datePredicates = mutableListOf<Predicate>()
                    it.date?.let { date -> datePredicates.add(criteriaBuilder.equal(dateDimension.get<LocalDate>(PROPERTY_DATE), date)) }
                    it.year?.let { year -> datePredicates.add(criteriaBuilder.equal(dateDimension.get<Int>(PROPERTY_YEAR), year)) }
                    it.month?.let { month -> datePredicates.add(criteriaBuilder.equal(dateDimension.get<Int>(PROPERTY_MONTH), month)) }
                    it.dayOfMonth?.let { dayOfMonth -> datePredicates.add(criteriaBuilder.equal(dateDimension.get<Int>(PROPERTY_DAY_OF_MONTH), dayOfMonth)) }
                    it.dayOfYear?.let { dayOfYear -> datePredicates.add(criteriaBuilder.equal(dateDimension.get<Int>(PROPERTY_DAY_OF_YEAR), dayOfYear)) }
                    it.weekOfYear?.let { weekOfYear -> datePredicates.add(criteriaBuilder.equal(dateDimension.get<Int>(PROPERTY_WEEK_OF_YEAR), weekOfYear)) }
                    it.range?.let { range -> datePredicates.add(criteriaBuilder.between(dateDimension.get<LocalDate>(PROPERTY_DATE), range.lowerBound.value.get(), range.upperBound.value.get())) }
                    it.quarter?.let { quarter -> datePredicates.add(criteriaBuilder.equal(dateDimension.get<Int>(PROPERTY_QUARTER), quarter)) }
                    criteriaBuilder.and(*datePredicates.toTypedArray())
                }
                else -> throw IllegalArgumentException("Filter is unknown type")
            }

    private fun mapGrouperToPath(it: Grouper, marketing: Root<Marketing>): Path<out Any> =
            when (it) {
                is DatasourceGrouper -> {
                    val datasourceDimension = marketing.join<Marketing, DatasourceDimension>(ATTRIBUTE_NAME_DATASOURCE_DIMENSION)
                    datasourceDimension.get<String>(DatasourceDimension.PROPERTY_NAME)
                }
                is CampaignGrouper -> {
                    val campaignDimension = marketing.join<Marketing, CampaignDimension>(ATTRIBUTE_NAME_CAMPAIGN_DIMENSION)
                    campaignDimension.get<String>(CampaignDimension.PROPERTY_NAME)
                }
                is DateGrouper -> {
                    val dateDimension = marketing.join<Marketing, DateDimension>(ATTRIBUTE_NAME_DATE_DIMENSION)
                    when (it) {
                        DATE -> dateDimension.get<LocalDate>(PROPERTY_DATE)
                        YEAR -> dateDimension.get<Int>(PROPERTY_YEAR)
                        MONTH -> dateDimension.get<Int>(PROPERTY_MONTH)
                        DAY_OF_MONTH -> dateDimension.get<Int>(PROPERTY_DAY_OF_MONTH)
                        DAY_OF_YEAR -> dateDimension.get<Int>(PROPERTY_DAY_OF_YEAR)
                        WEEK_OF_YEAR -> dateDimension.get<Int>(PROPERTY_WEEK_OF_YEAR)
                        QUARTER -> dateDimension.get<Int>(PROPERTY_QUARTER)
                    }
                }
                else -> throw IllegalArgumentException("Grouper is unknown type")
            }

    private fun mapResult(it: Any, numberOfMetrics: Int, numberOfGroups: Int, warehouseQuery: WarehouseQuery): SearchResult {
        val metrics = warehouseQuery.metrics
        val groupers = warehouseQuery.groupers
        if (metrics.size == 1 && groupers.isEmpty()) {
            return when (metrics.elementAt(0)) {
                CLICKS -> SearchResult(clicks = it as Long?)
                IMPRESSIONS -> SearchResult(impressions = it as Long?)
                CLICK_THROUGH_RATE -> SearchResult(clickThroughRate = it as Double?)
                SUM_CLICKS -> SearchResult(sumClicks = it as Long?)
                SUM_IMPRESSIONS -> SearchResult(sumImpressions = it as Long?)
                AVERAGE_CLICKS -> SearchResult(averageClicks = it as Double?)
                AVERAGE_IMPRESSIONS -> SearchResult(averageClicks = it as Double?)
            }
        }
        val objects = it as Array<*>
        val metricsArray = objects.copyOfRange(0, numberOfMetrics)
        val groupsArray = objects.copyOfRange(numberOfMetrics, numberOfMetrics + numberOfGroups)
        return SearchResult(
                clicks = metricsArray.getOrNull(metrics.indexOf(CLICKS)) as? Long,
                impressions = metricsArray.getOrNull(metrics.indexOf(IMPRESSIONS)) as? Long,
                clickThroughRate = metricsArray.getOrNull(metrics.indexOf(CLICK_THROUGH_RATE)) as? Double,
                sumClicks = metricsArray.getOrNull(metrics.indexOf(SUM_CLICKS)) as? Long,
                sumImpressions = metricsArray.getOrNull(metrics.indexOf(SUM_IMPRESSIONS)) as? Long,
                averageClicks = metricsArray.getOrNull(metrics.indexOf(AVERAGE_CLICKS)) as? Double,
                averageImpressions = metricsArray.getOrNull(metrics.indexOf(AVERAGE_IMPRESSIONS)) as? Double,
                datasourceName = groupsArray.getOrNull(groupers.indexOf(DatasourceGrouper.NAME))?.toString(),
                campaignName = groupsArray.getOrNull(groupers.indexOf(CampaignGrouper.NAME))?.toString(),
                date = groupsArray.getOrNull(groupers.indexOf(DATE))?.let { it as LocalDate },
                year = groupsArray.getOrNull(groupers.indexOf(YEAR)) as? Int,
                month = groupsArray.getOrNull(groupers.indexOf(MONTH)) as? Int,
                dayOfMonth = groupsArray.getOrNull(groupers.indexOf(DAY_OF_MONTH)) as? Int,
                dayOfYear = groupsArray.getOrNull(groupers.indexOf(DAY_OF_YEAR)) as? Int,
                weekOfYear = groupsArray.getOrNull(groupers.indexOf(WEEK_OF_YEAR)) as? Int,
                quarter = groupsArray.getOrNull(groupers.indexOf(QUARTER)) as? Int
        )
    }

    private fun Array<*>.getOrNull(position: Int): Any? {
        if (position < 0) {
            return null
        }
        return this[position]
    }

}