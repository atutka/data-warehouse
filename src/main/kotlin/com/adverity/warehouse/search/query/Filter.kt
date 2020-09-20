package com.adverity.warehouse.search.query

import org.springframework.data.domain.Range
import java.time.LocalDate

internal open class Filter

internal data class DatasourceFilter(
        val name: String
) : Filter()

internal data class CampaignFilter(
        val name: String
) : Filter()

internal data class DateFilter(
        val date: LocalDate? = null,
        val range: Range<LocalDate>? = null,
        val year: Int? = null,
        val month: Short? = null,
        val dayOfMonth: Short? = null,
        val dayOfYear: Short? = null,
        val weekOfYear: Short? = null,
        val quarter: Short? = null
) : Filter()