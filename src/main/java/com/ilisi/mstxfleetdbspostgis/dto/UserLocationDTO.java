package com.ilisi.mstxfleetdbspostgis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLocationDTO {
    private String userId;
    private String location;
    private String userType;
    private boolean isOnline;
    private String creationAt;
    private String updatedAt;
}
