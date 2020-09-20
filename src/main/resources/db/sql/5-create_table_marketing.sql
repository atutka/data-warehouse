CREATE TABLE data.marketing (
    id BIGSERIAL PRIMARY KEY,
    date_dimension_id BIGINT NOT NULL,
    datasource_dimension_id BIGINT NOT NULL,
    campaign_dimension_id BIGINT NOT NULL,
    clicks_amount BIGINT NOT NULL,
    impressions_amount BIGINT NOT NULL,

    CONSTRAINT fk_marketing_date_dimension_id FOREIGN KEY (date_dimension_id) REFERENCES data.date_dimension (id),
    CONSTRAINT fk_marketing_datasource_dimension_id FOREIGN KEY (datasource_dimension_id) REFERENCES data.datasource_dimension (id),
    CONSTRAINT fk_marketing_campaign_dimension_id FOREIGN KEY (campaign_dimension_id) REFERENCES data.campaign_dimension (id)
);

CREATE INDEX ON data.marketing (date_dimension_id);
CREATE INDEX ON data.marketing (datasource_dimension_id);
CREATE INDEX ON data.marketing (campaign_dimension_id);