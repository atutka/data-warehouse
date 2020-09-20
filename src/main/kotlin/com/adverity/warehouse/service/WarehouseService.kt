package com.adverity.warehouse.service

import com.adverity.warehouse.import.ImportStatus
import com.adverity.warehouse.search.query.WarehouseQuery
import com.adverity.warehouse.search.query.SearchResult
import org.springframework.data.domain.Pageable

internal interface WarehouseService {

    fun import(file: ByteArray): Long

    fun getImportStatus(id: Long): ImportStatus

    fun search(query: WarehouseQuery, pageable: Pageable): List<SearchResult>
}