package com.adverity.warehouse.marketing.repository

import com.adverity.warehouse.search.query.WarehouseQuery
import com.adverity.warehouse.search.query.SearchResult
import org.springframework.data.domain.Pageable

internal interface MarketingCustomRepository {

    fun findByQuery(warehouseQuery: WarehouseQuery, pageable: Pageable): List<SearchResult>

}