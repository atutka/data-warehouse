CREATE TABLE data.campaign_dimension (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE INDEX ON data.campaign_dimension (name);