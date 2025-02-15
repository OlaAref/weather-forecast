CREATE DATABASE IF NOT EXISTS weather_db;
USE weather_db;
CREATE USER 'weatherApp'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON weather_db.* TO 'weatherApp'@'%';
FLUSH PRIVILEGES;