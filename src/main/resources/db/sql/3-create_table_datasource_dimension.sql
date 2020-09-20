CREATE TABLE data.datasource_dimension (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE INDEX ON data.datasource_dimension (name);