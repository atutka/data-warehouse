package com.adverity.warehouse.marketing.repository

import com.adverity.warehouse.marketing.domain.DatasourceDimension
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface DatasourceDimensionRepository: CrudRepository<DatasourceDimension, Long> {

    fun findByName(name: String): DatasourceDimension?

}