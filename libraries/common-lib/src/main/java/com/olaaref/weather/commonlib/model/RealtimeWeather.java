package com.olaaref.weather.commonlib.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "REALTIME_WEATHER")
public class RealtimeWeather {
    @Id
    @Column(name = "LOCATION_CODE")
    private String locationCode;
    @Column(name = "TEMPERATURE")
    private double temperature;
    @Column(name = "HUMIDITY")
    private double humidity;
    @Column(name = "PRECIPITATION")
    private double precipitation;
    @Column(name = "WIND_SPEED")
    private double windSpeed;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;

    /**
     * @JoinColumn: This annotation is used to specify the column name in the database table that is used for the join between the owning entity and the associated entity.
     * It is used to establish the physical join between the two entities in the database.
     * @MapsId: This annotation is used to specify that the primary key of the associated entity (Location)
     * should be mapped to the primary key of the owning entity (RealtimeWeather).
     * It ensures that the primary key value of the associated entity is the same as the primary key value of the owning entity.
     */
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "LOCATION_CODE", referencedColumnName = "CODE")
    @MapsId
    private Location location;
}
