package com.adverity.warehouse.search.query.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.constraints.NotNull

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = DatasourceGrouperDTO::class, name = "DATASOURCE"),
        JsonSubTypes.Type(value = CampaignGrouperDTO::class, name = "CAMPAIGN"),
        JsonSubTypes.Type(value = DateGrouperDTO::class, name = "DATE")
)
internal open class GrouperDTO

internal data class DatasourceGrouperDTO(@field:NotNull val attribute: DatasourceAttribute) : GrouperDTO() {

    internal enum class DatasourceAttribute {
        NAME
    }

}

internal data class CampaignGrouperDTO(@field:NotNull val attribute: CampaignAttribute) : GrouperDTO() {

    internal enum class CampaignAttribute {
        NAME
    }
}

internal data class DateGrouperDTO(@field:NotNull val attribute: DateAttribute) : GrouperDTO() {

    internal enum class DateAttribute {
        DATE, YEAR, MONTH, DAY_OF_MONTH, DAY_OF_YEAR, WEEK_OF_YEAR, QUARTER
    }
}