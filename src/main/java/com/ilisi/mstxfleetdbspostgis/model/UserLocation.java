package com.ilisi.mstxfleetdbspostgis.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_location")
@ToString
public class UserLocation {

        @Id
        private String userId;
        private String location;

        private String userType;
        @Column(columnDefinition = "boolean default false")
        private boolean isOnline;
        @CreationTimestamp
        private String createdAt;
        @UpdateTimestamp
        private String updatedAt;
}
