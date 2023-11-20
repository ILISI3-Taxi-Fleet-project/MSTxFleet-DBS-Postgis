package com.ilisi.mstxfleetdbspostgis.repository;

import com.ilisi.mstxfleetdbspostgis.model.UserLocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "userLocations")
public interface UserLocationRepository extends CrudRepository<UserLocation, String> {
    @Query(value = "SELECT find_path_2pt_dijkstra(:startLongitude, :startLatitude, :endLongitude, :endLatitude)", nativeQuery = true)
    String findPath(
            @Param("startLongitude") double startLongitude,
            @Param("startLatitude") double startLatitude,
            @Param("endLongitude") double endLongitude,
            @Param("endLatitude") double endLatitude
    );
    @Query(value = """
            SELECT ul
            FROM UserLocation ul
            WHERE ST_DWithin(
                ST_SetSRID(ST_GeomFromText(ul.location), 4326),
                ST_SetSRID(ST_GeomFromText(:userLocation), 4326),
                :radiusInMeters, true) = true
            AND ul.isOnline = true
            """
)
    List<UserLocation> findNearbyOnlineUsers(
            @Param("userLocation") String userLocation,
            @Param("radiusInMeters") double radiusInMeters
    );
}
