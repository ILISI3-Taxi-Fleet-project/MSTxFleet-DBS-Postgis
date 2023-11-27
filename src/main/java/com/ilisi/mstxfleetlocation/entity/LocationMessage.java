package com.ilisi.mstxfleetlocation.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationMessage {
    private String userId;
    private double latitude;
    private double longitude;
    private String userType;
    private String timestamp;
}
