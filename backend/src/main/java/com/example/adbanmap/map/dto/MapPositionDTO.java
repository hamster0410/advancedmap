package com.example.adbanmap.map.dto;

import com.example.adbanmap.map.entity.MapPosition;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class MapPositionDTO {
    private long map_id;
    private String facilityName;            // 시설명
    private String category3;               // 카테고리3
    private Double latitude;                // 위도
    private Double longitude;               // 경도
    private String parkingAvailable;        // 주차 가능여부
    private String indoorAvailable;         // 장소(실내) 여부
    private String outdoorAvailable;        // 장소(실외)여부


    public MapPositionDTO(MapPosition mapEntity){
        this.latitude = mapEntity.getLatitude();
        this.facilityName = mapEntity.getFacilityName();
        this.longitude = mapEntity.getLongitude();
        this.map_id = mapEntity.getId();
        this.category3 = mapEntity.getCategory3();
        this.parkingAvailable = mapEntity.getParkingAvailable();
        this.indoorAvailable = mapEntity.getIndoorAvailable();
        this.outdoorAvailable = mapEntity.getOutdoorAvailable();
    }

}