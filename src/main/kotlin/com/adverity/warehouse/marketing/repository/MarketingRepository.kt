package com.adverity.warehouse.marketing.repository

import com.adverity.warehouse.marketing.domain.Marketing
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface MarketingRepository: CrudRepository<Marketing, Long>, MarketingCustomRepository