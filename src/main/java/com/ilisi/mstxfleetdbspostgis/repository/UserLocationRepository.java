package com.ilisi.mstxfleetdbspostgis.repository;

import com.ilisi.mstxfleetdbspostgis.model.UserLocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "userLocations")
public interface UserLocationRepository extends CrudRepository<UserLocation, String> {
    @Query(value = "SELECT find_path_2pt_dijkstra(:startLongitude, :startLatitude, :endLongitude, :endLatitude)", nativeQuery = true)
    String findPath(
            @Param("startLongitude") double startLongitude,
            @Param("startLatitude") double startLatitude,
            @Param("endLongitude") double endLongitude,
            @Param("endLatitude") double endLatitude
    );
}
