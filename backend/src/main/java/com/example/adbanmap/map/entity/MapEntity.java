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
@Table(name="map")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String facilityName;            // 시설명
    private String category1;               // 카테고리1
    private String category2;               // 카테고리2
    private String category3;               // 카테고리3
    private String provinceName;            // 시도 명칭
    private String cityName;                // 시군구 명칭
    private String townName;                // 법정읍면동명칭
    private String villageName;             // 리 명칭
    private String lotNumber;               // 번지
    private String roadName;                // 도로명 이름
    private String buildingNumber;          // 건물 번호
    private Double latitude;                // 위도
    private Double longitude;               // 경도
    private Integer zipCode;                // 우편번호
    private String roadAddress;             // 도로명주소
    private String lotAddress;              // 지번주소
    private String phoneNumber;             // 전화번호

    @Column(length = 500)
    private String homepage; // 홈페이지

    private String closedDays;              // 휴무일
    private String operatingHours;          // 운영시간
    private String parkingAvailable;        // 주차 가능여부
    private String admissionFee;            // 입장(이용료)가격 정보
    private String petFriendlyInfo;         // 반려동물 동반 가능정보
    private String petExclusiveInfo;        // 반려동물 전용 정보
    private String allowedPetSize;          // 입장 가능 동물 크기
    private String petRestrictions;         // 반려동물 제한사항
    private String indoorAvailable;         // 장소(실내) 여부
    private String outdoorAvailable;        // 장소(실외)여부
    private String placeDescription;        // 기본 정보_장소설명
    private String additionalPetFee;        // 애견 동반 추가 요금
    private String lastUpdated;             // 최종작성일

    public void update(MapEntity entity) {
        this.facilityName = entity.getFacilityName();
        this.category1 = entity.getCategory1();
        this.category2 = entity.getCategory2();
        this.category3 = entity.getCategory3();
        this.provinceName = entity.getProvinceName();
        this.cityName = entity.getCityName();
        this.townName = entity.getTownName();
        this.villageName = entity.getVillageName();
        this.lotNumber = entity.getLotNumber();
        this.roadName = entity.getRoadName();
        this.buildingNumber = entity.getBuildingNumber();
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
        this.zipCode = entity.getZipCode();
        this.roadAddress = entity.getRoadAddress();
        this.lotAddress = entity.getLotAddress();
        this.phoneNumber = entity.getPhoneNumber();
        this.homepage = entity.getHomepage();
        this.closedDays = entity.getClosedDays();
        this.operatingHours = entity.getOperatingHours();
        this.parkingAvailable = entity.getParkingAvailable();
        this.admissionFee = entity.getAdmissionFee();
        this.petFriendlyInfo = entity.getPetFriendlyInfo();
        this.petExclusiveInfo = entity.getPetExclusiveInfo();
        this.allowedPetSize = entity.getAllowedPetSize();
        this.petRestrictions = entity.getPetRestrictions();
        this.indoorAvailable = entity.getIndoorAvailable();
        this.outdoorAvailable = entity.getOutdoorAvailable();
        this.placeDescription = entity.getPlaceDescription();
        this.additionalPetFee = entity.getAdditionalPetFee();
        this.lastUpdated = entity.getLastUpdated();
    }

}