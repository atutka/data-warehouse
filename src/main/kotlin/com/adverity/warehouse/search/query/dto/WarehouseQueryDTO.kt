package com.adverity.warehouse.search.query.dto

import com.adverity.warehouse.search.query.Metric
import javax.validation.constraints.Max
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive

internal data class WarehouseQueryDTO(
        @field:NotEmpty(message = "Metrics cannot be empty")
        val metrics: Set<Metric>,
        val filters: Set<FilterDTO> = emptySet(),
        val groupers: Set<GrouperDTO> = emptySet(),
        @field:Positive(message = "Page cannot be less than one")
        val page: Int,
        @field:Max(100, message = "Size cannot be greater than one hundred")
        @field:Positive(message = "Size cannot be less than one")
        val size: Int
)