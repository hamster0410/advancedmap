package com.example.adbanmap.map.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="map_position")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String facilityName;            // 시설명
    private String category3;               // 카테고리3
    private Double latitude;                // 위도
    private Double longitude;               // 경도
    private String parkingAvailable;        // 주차 가능여부
    private String indoorAvailable;         // 장소(실내) 여부
    private String outdoorAvailable;        // 장소(실외)여부

    @OneToOne(mappedBy = "mapPosition", cascade = CascadeType.ALL)
    private MapDescription mapDescription;
}