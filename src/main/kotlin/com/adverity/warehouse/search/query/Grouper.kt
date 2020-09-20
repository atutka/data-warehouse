package com.adverity.warehouse.search.query

internal interface Grouper

internal enum class DatasourceGrouper : Grouper {
    NAME
}

internal enum class CampaignGrouper : Grouper {
    NAME
}

internal enum class DateGrouper : Grouper {
    DATE, YEAR, MONTH, DAY_OF_MONTH, DAY_OF_YEAR, WEEK_OF_YEAR, QUARTER
}