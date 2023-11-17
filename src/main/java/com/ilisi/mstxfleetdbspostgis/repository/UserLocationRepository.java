package com.ilisi.mstxfleetdbspostgis.repository;

import com.ilisi.mstxfleetdbspostgis.model.UserLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "userLocations")
public interface UserLocationRepository extends CrudRepository<UserLocation, String> {

}
