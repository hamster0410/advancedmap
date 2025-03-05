// marker-interactions.js

// axios import 추가
import axios from 'axios';
import {transform} from "ol/proj";
import {addWaypoint, moveWaypoint,clearWaypoints,removeWaypoint} from "./route";

const map = window.appMap; // main.js에서 설정한 전역 map 사용

var enterOverlayId = "marker-enter-overlay";	// 마커 마우스 오버시 사용할 팝업
var detail = null;

// showModal 함수 수정
export async function showWindow(feature,pixel) {

    const map_id = feature.get('map_id');
    try {
        // 서버에 상세 정보 요청
        const response = await axios.get(import.meta.env.VITE_API_URL + `/map/detail?id=${map_id}`);
        detail = response.data;
        // console.log('=== 상세 정보 로그 ===');
        // console.log('시설명:', detail.facilityName);
        // console.log('카테고리:', detail.category2);
        // console.log('주소 (도로명):', detail.roadAddress);
        // console.log('주소 (지번):', detail.lotAddress);
        // console.log('연락처:', detail.phoneNumber);
        // console.log('운영시간:', detail.operatingHours);
        // console.log('휴무일:', detail.closedDays);
        // console.log('반려동물 크기제한:', detail.allowedPetSize);
        // console.log('반려동물 제한사항:', detail.petRestrictions);
        // console.log('반려동물 추가요금:', detail.additionalPetFee);
        // console.log('홈페이지:', detail.homepage !== "정보없음" ? detail.homepage : '정보없음');
        // console.log('설명:', detail.placeDescription);
        // console.log('마지막 업데이트:', new Date(detail.lastUpdated).toLocaleDateString());
        // console.log('=========================');

        // 상세 정보 표시
        var point = feature.getGeometry().getCoordinates();
        var content = `
    <div class='popup-content'>
    <h3 class='popup-title'>상세 정보</h3>
    <div class='popup-section'>
        <div class='popup-item'><strong>시설명:</strong> ${detail.facilityName}</div>
        <div class='popup-item'><strong>카테고리:</strong> ${detail.category2}</div>
    </div>
    <hr class='popup-divider'>
    <div class='popup-section'>
        <div class='popup-item'><strong>주소 (도로명):</strong> ${detail.roadAddress}</div>
        <div class='popup-item'><strong>주소 (지번):</strong> ${detail.lotAddress}</div>
    </div>
    <hr class='popup-divider'>
    <div class='popup-section'>
        <div class='popup-item'><strong>연락처:</strong> ${detail.phoneNumber}</div>
        <div class='popup-item'><strong>운영시간:</strong> ${detail.operatingHours}</div>
        <div class='popup-item'><strong>휴무일:</strong> ${detail.closedDays}</div>
    </div>
    <hr class='popup-divider'>
    <div class='popup-section'>
        <div class='popup-item'><strong>반려동물 크기제한:</strong> ${detail.allowedPetSize}</div>
        <div class='popup-item'><strong>반려동물 제한사항:</strong> ${detail.petRestrictions}</div>
        <div class='popup-item'><strong>반려동물 추가요금:</strong> ${detail.additionalPetFee}</div>
    </div>
    <hr class='popup-divider'>
    <div class='popup-section'>
        <div class='popup-item'><strong>홈페이지:</strong> ${detail.homepage !== "정보없음" ? `<a href="${detail.homepage}" target="_blank">바로가기</a>` : '정보없음'}</div>
        <div class='popup-item'><strong>설명:</strong> ${detail.placeDescription}</div>
    </div>
    <hr class='popup-divider'>
    <div class='popup-footer'>
    <div><strong>마지막 업데이트:</strong> ${new Date(detail.lastUpdated).toLocaleDateString()}</div>
    <div class='route-add'><button>경유지 추가</button></div>
</div>

</div>
`;
        markerInfoWindow(content,point,pixel);

    } catch (error) {
        console.error('Error fetching details:', error);
    }


}

// 재시도 함수
async function retryLoad(mapId) {
    const feature = vectorSource.getFeatures().find(f =>
        f.get('properties').map_id === mapId
    );
    if (feature) {
        await showModal(feature.get('properties'));
    }
}


//인포윈도우 표시
function markerInfoWindow(content,point,pixel){
    document.getElementById("popup_over").style.display = "block";
    const popup = map.getOverlayById(enterOverlayId);
    popup.className = 'map-popup arrow-top';

    const obj = popup.getElement();

// 마커 위에 마우스 오버 시 정보 표시
    if (content !== "" && point !== undefined) {
        obj.innerHTML = content;
        popup.setOffset([75, -40]); // 기본값 설정
        popup.setPositioning('bottom-center'); // 기본값 설정
        popup.setPosition(point);

    } else {
        popup.setPosition(undefined);
    }
}


// 지도 마우스 오버 모서리에 팝업 표시 시 오버레이 위치 변경
const getOverlayOffsets = (mapInstance, overlay, px) => {
    const overlayRect = overlay.getElement().getBoundingClientRect();
    const mapRect = mapInstance.getTargetElement().getBoundingClientRect();
    const margin = 20;

    const offsetLeft = overlayRect.left - mapRect.left;
    const offsetRight = mapRect.right - overlayRect.right;
    const offsetTop = overlayRect.top - mapRect.top;
    const offsetBottom = mapRect.bottom - overlayRect.bottom;

    console.log('offsets', offsetLeft, offsetRight, offsetTop, offsetBottom);
    console.log('px', px[0], px[1]);

    // 기본 오프셋 값
    const offset = [75, 10];

    let tbChange = false;
    let lrChange = false;

    // 지도 크기 가져오기
    const mapElement = document.getElementById('map');
    const mapWidth = parseInt(getComputedStyle(mapElement).width);
    const mapHeight = parseInt(getComputedStyle(mapElement).height);

    if (offsetLeft < 0) {
        offset[0] = margin - offsetLeft + px[0];
        lrChange = true;
    } else if (offsetRight < 0) {
        offset[0] = -(Math.abs(offsetRight) + margin + mapWidth - px[0]) + 60;
        lrChange = true;
    }
    if (offsetTop < 0) {
        offset[1] = margin - offsetTop + px[1];
        tbChange = true;
    } else if (offsetBottom < 0) {
        offset[1] = -(Math.abs(offsetBottom) + margin + mapHeight - px[1]);
        tbChange = true;
    }

    if (lrChange && !tbChange) {
        offset[1] = 0;
    }

    return offset;
};


document.addEventListener('click', function(event) {
    // 클릭된 요소가 'route-add' 클래스를 가진 버튼인지 확인
    if (event.target.closest('.route-add')) {
        addWaypoint(detail);
    }
});

document.addEventListener('click', (e) => {
    const target = e.target.closest('.waypoint-action');
    if (!target) return;

    const index = Number(target.dataset.index);
    if (target.classList.contains('remove')) {
        removeWaypoint(index);
    } else if (target.classList.contains('clear')) {
        clearWaypoints();
    } else if (target.classList.contains('up')) {
        moveWaypoint(index, 'up');
    } else if (target.classList.contains('down')) {
        moveWaypoint(index, 'down');
    }
});

