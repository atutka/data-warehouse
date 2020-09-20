package com.adverity.warehouse.search.query.response.dto

import com.adverity.warehouse.mapper.Mapper
import com.adverity.warehouse.search.query.SearchResult
import org.springframework.stereotype.Component

@Component
internal class SearchResultDTOMapper : Mapper<SearchResult, SearchResultDTO> {

    override fun map(source: SearchResult): SearchResultDTO =
            SearchResultDTO(
                    clicks = source.clicks,
                    impressions = source.impressions,
                    clickThroughRate = source.clickThroughRate,
                    sumClicks = source.sumClicks,
                    sumImpressions = source.sumImpressions,
                    averageClicks = source.averageClicks,
                    averageImpressions = source.averageImpressions,
                    datasourceName = source.datasourceName,
                    campaignName = source.campaignName,
                    date = source.date,
                    year = source.year,
                    month = source.month,
                    dayOfMonth = source.dayOfMonth,
                    dayOfYear = source.dayOfYear,
                    weekOfYear = source.weekOfYear,
                    quarter = source.quarter

            )

}