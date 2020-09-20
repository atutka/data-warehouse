package com.adverity.warehouse.search.query.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDate
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = DatasourceFilterDTO::class, name = "DATASOURCE"),
        JsonSubTypes.Type(value = CampaignFilterDTO::class, name = "CAMPAIGN"),
        JsonSubTypes.Type(value = DateFilterDTO::class, name = "DATE")
)
internal open class FilterDTO

internal data class DatasourceFilterDTO(val condition: DatasourceConditionDTO) : FilterDTO() {

    internal data class DatasourceConditionDTO(
            @field:NotBlank(message = "Datasource name cannot be blank")
            val name: String
    )
}

internal data class CampaignFilterDTO(val condition: CampaignConditionDTO) : FilterDTO() {

    internal data class CampaignConditionDTO(
            @field:NotBlank(message = "Campaign name cannot be blank")
            val name: String
    )
}

internal data class DateFilterDTO(val condition: DateConditionDTO) : FilterDTO() {

    internal data class DateConditionDTO(
            val date: LocalDate? = null,
            val range: RangeDTO? = null,
            @field:PositiveOrZero(message = "Year cannot be less than zero")
            val year: Int? = null,
            @field:Min(1, message = "Month cannot be less than one")
            @field:Max(12, message = "Month cannot be greater than twelve")
            val month: Short? = null,
            @field:Min(1, message = "Day of month cannot be less than one")
            @field:Max(31, message = "Day of month cannot be greater than thirty one")
            val dayOfMonth: Short? = null,
            @field:Min(1, message = "Day of year cannot be less than one")
            @field:Max(366, message = "Day of year cannot be greater than three hundred sixty six")
            val dayOfYear: Short? = null,
            @field:Min(1, message = "Week of year cannot be less than one")
            @field:Max(53, message = "Week of year cannot be greater than fifty three")
            val weekOfYear: Short? = null,
            @field:Min(1, message = "Quarter cannot be less than one")
            @field:Max(4, message = "Quarter cannot be greater than four")
            val quarter: Short? = null
    )
}