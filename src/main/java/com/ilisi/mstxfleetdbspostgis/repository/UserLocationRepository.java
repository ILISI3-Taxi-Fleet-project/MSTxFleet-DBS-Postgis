package com.ilisi.mstxfleetdbspostgis.repository;

import com.ilisi.mstxfleetdbspostgis.model.UserLocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "userLocations")
public interface UserLocationRepository extends CrudRepository<UserLocation, String> {
    @Query(value = """
            -- find he nearest vertex to the start longitude/latitude
            WITH start AS (
              SELECT topo.source --could also be topo.target
              FROM media_2po_4pgr as topo
              ORDER BY topo.geom_way <-> ST_SetSRID(
                ST_MakePoint(:startLongitude, :startLatitude),
              4326)
              LIMIT 1
            ),
            -- find the nearest vertex to the destination longitude/latitude
            destination AS (
              SELECT topo.source --could also be topo.target
              FROM media_2po_4pgr as topo
              ORDER BY topo.geom_way <-> ST_SetSRID(
                ST_MakePoint(:endLongitude, :endLatitude),
              4326)
              LIMIT 1
            )
            -- use Dijsktra and join with the geometries
            SELECT ST_AsText(ST_Union(geom_way))
            FROM pgr_dijkstra('
                SELECT id,
                     source,
                     target,
                     ST_Length(ST_Transform(geom_way, 3857)) AS cost
                    FROM media_2po_4pgr',
                array(SELECT source FROM start),
                array(SELECT source FROM destination),
                directed := false) AS di
            JOIN   media_2po_4pgr AS pt
              ON   di.edge = pt.id;
            """,
            nativeQuery = true)
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
                AND ul.location != :userLocation
                AND upper(ul.userType) = 'PASSENGER'
            """)
    List<UserLocation> findNearbyOnlineUsersByLocation (
            @Param("userLocation") String userLocation,
            @Param("radiusInMeters") double radiusInMeters
    );


    @Query(value = """
            SELECT ul
            FROM UserLocation ul
            WHERE ST_DWithin(
                ST_SetSRID(ST_GeomFromText(ul.location), 4326),
                (SELECT ST_SetSRID(ST_GeomFromText(ul1.location), 4326)
                        FROM UserLocation ul1
                        WHERE ul1.userId = :userId),
                :radiusInMeters, true) = true
                AND ul.isOnline = true
                AND ul.userId != :userId
                AND upper(ul.userType) = 'PASSENGER'
            """)
    List<UserLocation> findNearbyOnlineUsersByUserId (
            @Param("userId") String userId,
            @Param("radiusInMeters") double radiusInMeters
    );


}
