package com.adverity.warehouse.controller

import com.adverity.warehouse.import.response.dto.ImportStatusResponseDTO
import com.adverity.warehouse.search.query.dto.WarehouseQueryDTO
import com.adverity.warehouse.search.query.dto.WarehouseQueryMapper
import com.adverity.warehouse.search.query.response.dto.SearchResponseDTO
import com.adverity.warehouse.search.query.response.dto.SearchResultDTOMapper
import com.adverity.warehouse.service.WarehouseService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import javax.validation.Valid

@RestController
internal class WarehouseController(
        private val warehouseService: WarehouseService,
        private val warehouseQueryMapper: WarehouseQueryMapper,
        private val searchResultDTOMapper: SearchResultDTOMapper
) {

    @PostMapping("/api/warehouse/import")
    fun import(@RequestParam("file") file: MultipartFile): ResponseEntity<Void> {
        val importId = warehouseService.import(file.bytes)
        return created(URI.create(importId.toString())).build()
    }

    @GetMapping("/api/warehouse/import/{id}/status")
    fun getImportStatus(@PathVariable("id") importId: Long): ImportStatusResponseDTO {
        val status = warehouseService.getImportStatus(importId)
        return ImportStatusResponseDTO(status)
    }

    @PostMapping("/api/warehouse/search")
    fun search(@Valid @RequestBody queryDTO: WarehouseQueryDTO): SearchResponseDTO {
        val query = warehouseQueryMapper.map(queryDTO)
        val searchResults = warehouseService.search(query, PageRequest.of(queryDTO.page - 1, queryDTO.size))
        return SearchResponseDTO(searchResults.map { searchResultDTOMapper.map(it) })
    }

}