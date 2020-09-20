INSERT INTO data.datasource_dimension(id, name) VALUES (1, 'Google Ads');
INSERT INTO data.datasource_dimension(id, name) VALUES (2, 'Twitter Ads');
INSERT INTO data.campaign_dimension(id, name) VALUES (1, 'Adventmarkt Touristik');
INSERT INTO data.campaign_dimension(id, name) VALUES (2, 'Firmen Mitgliedschaft');
INSERT INTO data.date_dimension(id, date, year, month, day_of_month, day_of_year, quarter, week_of_year) VALUES (1, '2018-11-12', 2018, 11, 12, 316, 4, 46);
INSERT INTO data.date_dimension(id, date, year, month, day_of_month, day_of_year, quarter, week_of_year) VALUES (2, '2019-03-13', 2019, 3, 13, 72, 1, 10);
INSERT INTO data.date_dimension(id, date, year, month, day_of_month, day_of_year, quarter, week_of_year) VALUES (3, '2019-11-14', 2019, 11, 14, 318, 4, 46);
INSERT INTO data.date_dimension(id, date, year, month, day_of_month, day_of_year, quarter, week_of_year) VALUES (4, '2019-10-15', 2019, 10, 15, 319, 4, 46);
INSERT INTO data.marketing(id, date_dimension_id, datasource_dimension_id, campaign_dimension_id, clicks_amount, impressions_amount)
    VALUES (1, 1, 1, 1, 7, 22425);
INSERT INTO data.marketing(id, date_dimension_id, datasource_dimension_id, campaign_dimension_id, clicks_amount, impressions_amount)
    VALUES (2, 3, 1, 1, 147, 80351);
INSERT INTO data.marketing(id, date_dimension_id, datasource_dimension_id, campaign_dimension_id, clicks_amount, impressions_amount)
    VALUES (3, 2, 2, 2, 16, 45452);
INSERT INTO data.marketing(id, date_dimension_id, datasource_dimension_id, campaign_dimension_id, clicks_amount, impressions_amount)
    VALUES (4, 4, 2, 2, 131, 81906);
