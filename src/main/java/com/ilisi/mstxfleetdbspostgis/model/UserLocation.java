package com.ilisi.mstxfleetdbspostgis.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLocation {

        @Id
        private String userId;
        @Column(columnDefinition = "geometry(Point,4326)")
        private Point location;

        private String userType;

        @CreationTimestamp
        private String creationAt;
        private String updatedAt;
}
