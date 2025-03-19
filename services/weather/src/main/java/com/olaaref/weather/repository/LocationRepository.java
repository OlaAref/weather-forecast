package com.olaaref.weather.repository;

import com.olaaref.weather.commonlib.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {
    @Query("SELECT l FROM Location l WHERE l.trashed = false")
    List<Location> findUntrashed();
    Optional<Location> findByCodeAndTrashed(String code, boolean trashed);
    @Modifying
    @Query("UPDATE Location l SET l.trashed = true WHERE l.code = :code")
    void trashLocation(String code);
}
