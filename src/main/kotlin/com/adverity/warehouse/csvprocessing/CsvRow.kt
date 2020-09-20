package com.adverity.warehouse.csvprocessing

internal data class CsvRow(
        var datasource: String? = null,
        var campaign: String? = null,
        var daily: String? = null,
        var clicks: Long? = null,
        var impressions: Long? = null
) {
    companion object {
        const val DATASOURCE_NAME = "datasource"
        const val CAMPAIGN_NAME = "campaign"
        const val DAILY_NAME = "daily"
        const val CLICKS_NAME = "clicks"
        const val IMPRESSIONS_NAME = "impressions"
    }
}