package com.adverity.warehouse.search.query.dto

import java.time.LocalDate
import javax.validation.constraints.NotNull

internal data class RangeDTO(
        @field:NotNull(message = "Range from date cannot be null")
        val from: LocalDate,
        @field:NotNull(message = "Range to date cannot be null")
        val to: LocalDate
)