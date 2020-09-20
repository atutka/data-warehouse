package com.adverity.warehouse.marketing.domain

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(schema = "data", name = "marketing")
internal data class Marketing(
        @Id
        @GeneratedValue(strategy = IDENTITY)
        @SequenceGenerator(name = "data.marketing_id_seq")
        val id: Long? = null,
        @Column(name = "clicks_amount", nullable = false)
        val clicksAmount: Long,
        @Column(name = "impressions_amount", nullable = false)
        val impressionsAmount: Long,
        @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        @JoinColumn(name = "date_dimension_id", nullable = false, foreignKey = ForeignKey(name = "fk_marketing_date_dimension_id"))
        val dateDimension: DateDimension,
        @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        @JoinColumn(name = "datasource_dimension_id", nullable = false, foreignKey = ForeignKey(name = "fk_marketing_datasource_dimension_id"))
        val datasourceDimension: DatasourceDimension,
        @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        @JoinColumn(name = "campaign_dimension_id", nullable = false, foreignKey = ForeignKey(name = "fk_marketing_campaign_dimension_id"))
        val campaignDimension: CampaignDimension
) {
        companion object {
                const val PROPERTY_CLICKS_AMOUNT = "clicksAmount"
                const val PROPERTY_IMPRESSIONS_AMOUNT = "impressionsAmount"
        }
}