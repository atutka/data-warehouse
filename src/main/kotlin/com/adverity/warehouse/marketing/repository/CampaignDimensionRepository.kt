package com.adverity.warehouse.marketing.repository

import com.adverity.warehouse.marketing.domain.CampaignDimension
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface CampaignDimensionRepository : CrudRepository<CampaignDimension, Long> {

    fun findByName(name: String): CampaignDimension?

}