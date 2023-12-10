package com.ilisi.mstxfleetdbspostgis.repository;

import com.ilisi.mstxfleetdbspostgis.model.UserLocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

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
            SELECT COALESCE(
                       ST_AsText(ST_Union(pt.geom_way)),
                       ST_AsText(ST_MakePoint(0, 1)) -- Default value when no route is found
                   ) AS path
            FROM pgr_dijkstra('
                SELECT id,
                     source,
                     target,
                     ST_Length(ST_Transform(geom_way, 3857)) AS cost
                    FROM media_2po_4pgr',
                array(SELECT source FROM start),
                array(SELECT source FROM destination),
                directed \\:= false) AS di
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
            SELECT COALESCE(
                    jsonb_agg(
                      jsonb_build_object(
                       'userId', ul.userId,
                       'location', ul.location,
                       'userType', ul.userType,
                       'createdAt', ul.createdAt,
                       'updatedAt', ul.updatedAt,
                       'isOnline', ul.isOnline,
                       'linearDistanceInMeters', ST_Distance(
                           ST_SetSRID(ST_GeomFromText(ul.location), 4326),
                           ST_SetSRID(ST_GeomFromText(:userLocation), 4326)
                       )
                    )
                   ),
                   '[]'
                ) as nearby_users
            FROM UserLocation ul
            WHERE ST_DWithin(
                ST_SetSRID(ST_GeomFromText(ul.location), 4326),
                ST_SetSRID(ST_GeomFromText(:userLocation), 4326),
                :radiusInMeters, true) = true
                AND ul.isOnline = true
                AND ul.location != :userLocation
                AND upper(ul.userType) = 'PASSENGER'
            """)
    String findNearbyOnlineUsersByLocation (
            @Param("userLocation") String userLocation,
            @Param("radiusInMeters") double radiusInMeters
    );


    @Query(value = """
            SELECT COALESCE(
                       jsonb_agg(
                           jsonb_build_object(
                               'userId', ul.userId,
                               'location', ul.location,
                               'userType', ul.userType,
                               'createdAt', ul.createdAt,
                               'updatedAt', ul.updatedAt,
                               'isOnline', ul.isOnline
                           )
                       ),
                       '[]'
                   ) as nearby_users
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
    String findNearbyOnlineUsersByUserId (
            @Param("userId") String userId,
            @Param("radiusInMeters") double radiusInMeters
    );

    @Query(value = """
            -- find the nearest vertex to the start longitude/latitude
                WITH start AS (
                    SELECT topo.source
                    FROM media_2po_4pgr as topo
                    ORDER BY topo.geom_way <-> ST_SetSRID(ST_GeomFromText(:startLocation), 4326)
                    LIMIT 1
                ),
                -- find the nearest vertex to the destination longitude/latitude
                destination AS (
                    SELECT topo.source
                    FROM media_2po_4pgr as topo
                    ORDER BY topo.geom_way <-> ST_SetSRID(ST_GeomFromText(:endLocation), 4326)
                    LIMIT 1
                ),
                -- use Dijsktra and join with the geometries
                dijkstra AS (
                    SELECT geom_way
                    FROM pgr_dijkstra(
                        'SELECT id, source, target, ST_Length(ST_Transform(geom_way, 3857)) AS cost FROM media_2po_4pgr',
                        array(SELECT source FROM start),
                        array(SELECT source FROM destination),
                        false
                    ) AS di
                    JOIN media_2po_4pgr AS pt ON di.edge = pt.id
                )
                -- Select all users that are online and within the radius of the path
                SELECT  jsonb_agg(
                          jsonb_build_object(
                           'userId', ul.user_id,
                           'location', ul.location,
                           'userType', ul.user_type,
                           'createdAt', ul.created_at,
                           'updatedAt', ul.updated_at,
                           'isOnline', ul.is_online
                       )
                    ) as nearby_users
                FROM user_location AS ul, dijkstra As pt
                WHERE ST_DWithin(ST_SetSRID(ST_GeomFromText(ul.location), 4326), pt.geom_way, :radiusInMeters,true)
                AND ul.is_online
                AND upper(ul.user_type) = 'PASSENGER'
                AND ul.location != :startLocation
    """, nativeQuery = true)
    String findNearbyOnlineUsersToPath(
            @Param("startLocation") String startLocation,
            @Param("endLocation") String endLocation,
            @Param("radiusInMeters") double radiusInMeters
    );

    Optional<UserLocation> findByUserId(String userId);
}
