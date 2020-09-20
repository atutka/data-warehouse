CREATE TABLE data.date_dimension (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    day_of_month INTEGER NOT NULL,
    day_of_year INTEGER NOT NULL,
    quarter INTEGER NOT NULL,
    week_of_year INTEGER NOT NULL
);

CREATE INDEX ON data.date_dimension (date);
CREATE INDEX ON data.date_dimension (year);
CREATE INDEX ON data.date_dimension (month);
CREATE INDEX ON data.date_dimension (day_of_month);