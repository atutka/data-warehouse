package com.adverity.warehouse.search.query.dto

import com.adverity.warehouse.mapper.Mapper
import com.adverity.warehouse.search.query.CampaignFilter
import com.adverity.warehouse.search.query.CampaignGrouper
import com.adverity.warehouse.search.query.DatasourceFilter
import com.adverity.warehouse.search.query.DatasourceGrouper
import com.adverity.warehouse.search.query.DateFilter
import com.adverity.warehouse.search.query.DateGrouper
import com.adverity.warehouse.search.query.Filter
import com.adverity.warehouse.search.query.Grouper
import com.adverity.warehouse.search.query.WarehouseQuery
import org.springframework.data.domain.Range
import org.springframework.stereotype.Component

@Component
internal class WarehouseQueryMapper : Mapper<WarehouseQueryDTO, WarehouseQuery> {

    override fun map(source: WarehouseQueryDTO): WarehouseQuery {
        val filters = source.filters.map { mapFilter(it) }.toSet()
        val groupers = source.groupers.map { mapGrouper(it) }.toSet()
        return WarehouseQuery(
                metrics = source.metrics,
                filters = filters,
                groupers = groupers
        )
    }

    private fun mapFilter(filter: FilterDTO): Filter =
            when (filter) {
                is DatasourceFilterDTO -> DatasourceFilter(name = filter.condition.name)
                is CampaignFilterDTO -> CampaignFilter(name = filter.condition.name)
                is DateFilterDTO -> {
                    val dateCondition = filter.condition
                    DateFilter(
                            date = dateCondition.date,
                            range = dateCondition.range?.let { Range.closed(it.from, it.to) },
                            year = dateCondition.year,
                            month = dateCondition.month,
                            dayOfMonth = dateCondition.dayOfMonth,
                            dayOfYear = dateCondition.dayOfYear,
                            weekOfYear = dateCondition.weekOfYear,
                            quarter = dateCondition.quarter
                    )
                }
                else -> throw IllegalArgumentException("Filter is unknown type")
            }

    private fun mapGrouper(grouper: GrouperDTO): Grouper =
            when (grouper) {
                is DatasourceGrouperDTO -> DatasourceGrouper.valueOf(grouper.attribute.name)
                is CampaignGrouperDTO -> CampaignGrouper.valueOf(grouper.attribute.name)
                is DateGrouperDTO -> DateGrouper.valueOf(grouper.attribute.name)
                else -> throw IllegalArgumentException("Grouper is unknown type")
            }
}