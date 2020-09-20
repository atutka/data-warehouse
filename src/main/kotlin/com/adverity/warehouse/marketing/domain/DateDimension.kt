package com.adverity.warehouse.marketing.domain

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(schema = "data", name = "date_dimension")
internal data class DateDimension(
        @Id
        @GeneratedValue(strategy = IDENTITY)
        @SequenceGenerator(name = "data.date_dimension_id_seq")
        val id: Long? = null,
        @Column(name = "date", nullable = false)
        val date: LocalDate,
        @Column(name = "year", nullable = false)
        val year: Int,
        @Column(name = "month", nullable = false)
        val month: Int,
        @Column(name = "day_of_month", nullable = false)
        val dayOfMonth: Int,
        @Column(name = "day_of_year", nullable = false)
        val dayOfYear: Int,
        @Column(name = "quarter", nullable = false)
        val quarter: Int,
        @Column(name = "week_of_year", nullable = false)
        val weekOfYear: Int
) {
    companion object {
        const val PROPERTY_DATE = "date"
        const val PROPERTY_YEAR = "year"
        const val PROPERTY_MONTH = "month"
        const val PROPERTY_DAY_OF_MONTH = "dayOfMonth"
        const val PROPERTY_DAY_OF_YEAR = "dayOfYear"
        const val PROPERTY_QUARTER = "quarter"
        const val PROPERTY_WEEK_OF_YEAR = "weekOfYear"
    }
}