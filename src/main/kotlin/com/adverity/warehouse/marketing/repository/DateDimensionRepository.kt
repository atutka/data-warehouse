package com.adverity.warehouse.marketing.repository

import com.adverity.warehouse.marketing.domain.DateDimension
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
internal interface DateDimensionRepository: CrudRepository<DateDimension, Long> {

    fun findByDate(date: LocalDate): DateDimension?

}