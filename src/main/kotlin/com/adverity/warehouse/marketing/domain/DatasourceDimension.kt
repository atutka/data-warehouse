package com.adverity.warehouse.marketing.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(schema = "data", name = "datasource_dimension")
internal data class DatasourceDimension(
        @Id
        @GeneratedValue(strategy = IDENTITY)
        @SequenceGenerator(name = "data.datasource_dimension_id_seq")
        val id: Long? = null,
        @Column(name = "name", nullable = false, unique = true)
        val name: String
) {
    companion object {
        const val PROPERTY_NAME = "name"
    }
}