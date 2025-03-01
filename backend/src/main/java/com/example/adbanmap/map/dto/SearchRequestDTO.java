package com.example.adbanmap.map.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchRequestDTO {
    private String keyword;           // 검색 키워드
    private String category;
    private List<String> options;
    private LatLng swLatlng;          // 남서 좌표
    private LatLng neLatlng;          // 북동 좌표



    @Data
    public static class LatLng {
        private Double longitude;             // 경도(Longitude)
        private Double latitude;             // 위도(Latitude)
    }
}