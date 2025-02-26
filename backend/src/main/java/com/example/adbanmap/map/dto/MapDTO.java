package com.example.adbanmap.map.dto;


import com.example.adbanmap.map.entity.MapDescription;
import com.example.adbanmap.map.entity.MapPosition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapDTO {
    @JsonProperty("시설명")
    private String facilityName;            // 시설명

    @JsonProperty("카테고리1")
    private String category1;               // 카테고리1

    @JsonProperty("카테고리2")
    private String category2;               // 카테고리2

    @JsonProperty("카테고리3")
    private String category3;               // 카테고리3

    @JsonProperty("시도 명칭")
    private String provinceName;            // 시도 명칭

    @JsonProperty("시군구 명칭")
    private String cityName;                // 시군구 명칭

    @JsonProperty("법정읍면동명칭")
    private String townName;                // 법정읍면동명칭

    @JsonProperty("리 명칭")
    private String villageName;             // 리 명칭

    @JsonProperty("번지")
    private String lotNumber;               // 번지

    @JsonProperty("도로명 이름")
    private String roadName;                // 도로명 이름


    @JsonProperty("건물 번호")
    private String buildingNumber;          // 건물 번호
    @JsonProperty("위도")
    private Double latitude;                 // 위도

    @JsonProperty("경도")
    private Double longitude;               // 경도

    @JsonProperty("우편번호")
    private Integer zipCode;                // 우편번호

    @JsonProperty("도로명주소")
    private String roadAddress;             // 도로명주소

    @JsonProperty("지번주소")
    private String lotAddress;              // 지번주소

    @JsonProperty("전화번호")
    private String phoneNumber;             // 전화번호
    @JsonProperty("홈페이지")
    private String homepage;                // 홈페이지

    @JsonProperty("휴무일")
    private String closedDays;              // 휴무일

    @JsonProperty("운영시간")
    private String operatingHours;          // 운영시간

    @JsonProperty("주차 가능여부")
    private String parkingAvailable;        // 주차 가능여부

    @JsonProperty("입장(이용료)가격 정보")
    private String admissionFee;            // 입장(이용료)가격 정보

    @JsonProperty("반려동물 동반 가능정보")
    private String petFriendlyInfo;         // 반려동물 동반 가능정보

    @JsonProperty("반려동물 전용 정보")
    private String petExclusiveInfo;        // 반려동물 전용 정보

    @JsonProperty("입장 가능 동물 크기")
    private String allowedPetSize;          // 입장 가능 동물 크기

    @JsonProperty("반려동물 제한사항")
    private String petRestrictions;         // 반려동물 제한사항

    @JsonProperty("장소(실내) 여부")
    private String indoorAvailable;         // 장소(실내) 여부

    @JsonProperty("장소(실외)여부")
    private String outdoorAvailable;        // 장소(실외)여부

    @JsonProperty("기본 정보_장소설명")
    private String placeDescription;        // 기본 정보_장소설명

    @JsonProperty("애견 동반 추가 요금")
    private String additionalPetFee;        // 애견 동반 추가 요금

    @JsonProperty("최종작성일")
    private String lastUpdated;             // 최종작성일

    private long commentCount;
    private long totalRating;


    public MapPosition toMapPosition() {
        MapPosition mapPosition = new MapPosition();
        mapPosition.setCategory3(this.getCategory3());
        mapPosition.setLatitude(this.getLatitude());
        mapPosition.setLongitude(this.getLongitude());
        mapPosition.setParkingAvailable(this.getParkingAvailable());
        mapPosition.setIndoorAvailable(this.getIndoorAvailable());
        mapPosition.setOutdoorAvailable(this.getOutdoorAvailable());

        return mapPosition;
    }

    public MapDescription toMapDescription(MapPosition mapPosition) {
        MapDescription mapDescription = new MapDescription();
        mapDescription.setFacilityName(this.getFacilityName());
        mapDescription.setCategory1(this.getCategory1());
        mapDescription.setCategory2(this.getCategory2());
        mapDescription.setProvinceName(this.getProvinceName());
        mapDescription.setCityName(this.getCityName());
        mapDescription.setTownName(this.getTownName());
        mapDescription.setVillageName(this.getVillageName());
        mapDescription.setLotNumber(this.getLotNumber());
        mapDescription.setRoadName(this.getRoadName());
        mapDescription.setZipCode(this.getZipCode());
        mapDescription.setRoadAddress(this.getRoadAddress());
        mapDescription.setLotAddress(this.getLotAddress());
        mapDescription.setPhoneNumber(this.getPhoneNumber());
        mapDescription.setHomepage(this.getHomepage());
        mapDescription.setClosedDays(this.getClosedDays());
        mapDescription.setOperatingHours(this.getOperatingHours());
        mapDescription.setAdmissionFee(this.getAdmissionFee());
        mapDescription.setPetFriendlyInfo(this.getPetFriendlyInfo());
        mapDescription.setPetExclusiveInfo(this.getPetExclusiveInfo());
        mapDescription.setAllowedPetSize(this.getAllowedPetSize());
        mapDescription.setPetRestrictions(this.getPetRestrictions());
        mapDescription.setPlaceDescription(this.getPlaceDescription());
        mapDescription.setAdditionalPetFee(this.getAdditionalPetFee());
        mapDescription.setLastUpdated(this.getLastUpdated());
        mapDescription.setMapPosition(mapPosition);

        return mapDescription;

    }

}
