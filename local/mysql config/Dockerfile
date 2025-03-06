FROM mysql:8.0

# Set environment variables (change as needed)
ENV MYSQL_ALLOW_EMPTY_PASSWORD=yes
ENV MYSQL_DATABASE=weather_db

# Copy custom MySQL configuration (if any)
# COPY my.cnf /etc/mysql/my.cnf

# Copy initialization SQL script
COPY init.sql /docker-entrypoint-initdb.d/

# Expose MySQL port
EXPOSE 3306
# Define entrypoint (default is fine for MySQL)
CMD ["mysqld"]
