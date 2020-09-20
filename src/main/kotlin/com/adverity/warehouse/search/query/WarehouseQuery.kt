package com.adverity.warehouse.search.query

internal data class WarehouseQuery(
        val metrics: Set<Metric>,
        val filters: Set<Filter> = emptySet(),
        val groupers: Set<Grouper> = emptySet()
)