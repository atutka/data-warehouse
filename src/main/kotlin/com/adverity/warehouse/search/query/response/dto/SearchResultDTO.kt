package com.adverity.warehouse.search.query.response.dto

import java.time.LocalDate

internal data class SearchResultDTO(
        val clicks: Long? = null,
        val impressions: Long? = null,
        val clickThroughRate: Double? = null,
        val sumClicks: Long? = null,
        val sumImpressions: Long? = null,
        val averageClicks: Double? = null,
        val averageImpressions: Double? = null,
        val datasourceName: String? = null,
        val campaignName: String? = null,
        val date: LocalDate? = null,
        val year: Int? = null,
        val month: Int? = null,
        val dayOfMonth: Int? = null,
        val dayOfYear: Int? = null,
        val weekOfYear: Int? = null,
        val quarter: Int? = null
)