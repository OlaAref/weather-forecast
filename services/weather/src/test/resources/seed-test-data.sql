INSERT INTO locations (code, country_code, country_name, region_name, city_name, latitude, longitude, zip_code, time_zone, enabled, trashed)
VALUES
('US-CA-LA', 'US', 'United States', 'California', 'Los Angeles', 34.052235, -118.243683, '90001', 'America/Los_Angeles', true, false),
('US-NY-NY', 'US', 'United States', 'New York', 'New York City', 40.712776, -74.005974, '10001', 'America/New_York', true, false),
('GB-ENG-LDN', 'GB', 'United Kingdom', 'England', 'London', 51.507351, -0.127758, 'SW1A 1AA', 'Europe/London', true, false);

-- Insert test data for RealtimeWeather
INSERT INTO REALTIME_WEATHER (LOCATION_CODE, TEMPERATURE, HUMIDITY, PRECIPITATION, WIND_SPEED, STATUS, LAST_UPDATED)
VALUES
('US-CA-LA', 25.5, 65.0, 0.0, 10.2, 'Sunny', '2023-06-15 12:00:00'),
('US-NY-NY', 18.2, 72.5, 2.5, 15.7, 'Rainy', '2023-06-15 12:00:00'),
('GB-ENG-LDN', 15.8, 80.0, 1.2, 8.5, 'Cloudy', '2023-06-15 12:00:00');