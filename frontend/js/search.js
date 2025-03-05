import {fromLonLat, transform} from 'ol/proj';
import 'ol/ol.css';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import axios from "axios";
import {Select} from "ol/interaction";
import {pointerMove} from "ol/events/condition";
import { showWindow} from "./openwindow";


const map = window.appMap; // main.js에서 설정한 전역 map 사용

// 선택된 상태 추적
const selectedData = {
    category: null,
    options: new Set(),
    query: null,
    swLatlng : null,
    neLatlng :null,
};

let vectorSource;
let markerLayer;
var enterOverlayId = "marker-enter-overlay";	// 마커 마우스 오버시 사용할 팝업


function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('collapsed');
}

function selectCategory(button) {
    const activeButton = document.querySelector('.category-button.active');

    // 현재 클릭한 버튼이 이미 활성화된 상태라면 비활성화
    if (activeButton === button) {
        button.classList.remove('active');
        selectedData.category = null;
    } else {
        // 그렇지 않다면 모든 버튼 비활성화 후 클릭한 버튼 활성화
        document.querySelectorAll('.category-button').forEach(btn => {
            btn.classList.remove('active');
        });
        button.classList.add('active');
        selectedData.category = button.querySelector('.category-icon').nextSibling.textContent.trim();
    }


}
document.querySelectorAll('.checkbox-input').forEach(checkbox => {
    checkbox.addEventListener('change', () => {
        if (checkbox.checked) {
            selectedData.options.add(checkbox.value);
        } else {
            selectedData.options.delete(checkbox.value);
        }
    });
});
// 검색 버튼 클릭 시 로그 출력
document.querySelector('.search-button').addEventListener('click', async () => {
    const searchInput = document.querySelector('.search-input');
    selectedData.query = searchInput.value.trim(); // 입력된 검색어 가져오기

    // 현재 화면의 Extent(경계 박스) 가져오기
    const extent = map.getView().calculateExtent(map.getSize());

    // 좌하단(left bottom)과 우상단(right top) 좌표 변환
    selectedData.swLatlng = transform([extent[0], extent[1]], 'EPSG:3857', 'EPSG:4326');
    selectedData.neLatlng = transform([extent[2], extent[3]], 'EPSG:3857', 'EPSG:4326');

    //서버로부터 데이터 가져오기
    const data = await getData();
    markSearchData(data);

    document.getElementById('showResultsBtn').style.display = 'flex';
});


function getData() {
    const path = import.meta.env.VITE_WEB_URL + "/map/search";
    // console.log('🔍 선택된 데이터:');
    // console.log('카테고리:', selectedData.category || '없음');
    // console.log('검색어:', selectedData.query || '없음');
    // console.log('옵션:', Array.from(selectedData.options).join(', ') || '없음');
    // console.log('남서쪽 좌표 (swLatlng):', selectedData.swLatlng ? selectedData.swLatlng.join(', ') : '없음');
    // console.log('북동쪽 좌표 (neLatlng):', selectedData.neLatlng ? selectedData.neLatlng.join(', ') : '없음');

    const params = new URLSearchParams();
    if (selectedData.category) params.append('category', selectedData.category);
    if (selectedData.options.size > 0) params.append('options', Array.from(selectedData.options).join(','));
    if (selectedData.query) params.append('keyword', selectedData.query);
    if (selectedData.swLatlng) {params.append('swLatlng.longitude', selectedData.swLatlng[0]);
        params.append('swLatlng.latitude', selectedData.swLatlng[1]);}
    if (selectedData.neLatlng){
        params.append('neLatlng.longitude', selectedData.neLatlng[0]);
        params.append('neLatlng.latitude', selectedData.neLatlng[1]);
    }

    // console.log('📡 요청 URL:', `${path}?${params.toString()}`);

    return axios.get(`${path}`, {
        params: Object.fromEntries(params) // URLSearchParams → 객체 변환
    })
        .then(response => {
            return response.data;
        })
        .catch(error => {
            if (error.response) {
                console.error('🚨 서버 오류! 상태 코드:', error.response.status);
            } else if (error.request) {
                console.error('🚨 요청이 전송되지 않음:', error.request);
            } else {
                console.error('🚨 요청 설정 중 오류:', error.message);
            }
            return null;
        });
}

export function markSearchData(data) {
    // 기존 레이어 제거

    // map.getLayers().forEach(layer => {
    //     if (layer === markerLayer) {
    //         map.removeLayer(layer);
    //     }
    // });

    map.getLayers().getArray().forEach(layer => {
        if(layer instanceof VectorLayer){
            map.removeLayer(layer);
        }
    })
    // map.removeLayer(markerLayer); // 레이어 자체 삭제

    // 마커 생성 및 스타일 적용
    const features = data.mapPositionDTOList.map(position => {
        const marker = new Feature({
            geometry: new Point(fromLonLat([position.longitude, position.latitude])),
            map_id: position.map_id
        });

        marker.setStyle(new Style({
            image: new Icon({
                src: '../img/marker.png',
                scale: 1,
            }),
        }));

        return marker;
    });

    // 벡터 소스 및 레이어 생성
    vectorSource = new VectorSource({
        features: features
    });

    markerLayer = new VectorLayer({
        id: 'markerLayer',  // 고유 ID 설정
        source: vectorSource,
        zIndex: 2

    });

    // 마우스 커서 변경을 위한 포인터 상호작용 추가
    const pointerInteraction = new Select({
        condition: pointerMove,
        style: null
    });

    map.addInteraction(pointerInteraction);

    // 마커 클릭 이벤트 처리
    map.on('click', function(evt) {
        const popup = map.getOverlayById(enterOverlayId);
        const feature = map.forEachFeatureAtPixel(evt.pixel, function(feature) {
            return feature;
        });
        if (feature) {

            showWindow(feature);
        }

        if (popup) {
            popup.setPosition(undefined); // 팝업 숨기기
            document.getElementById("popup_over").style.display = "none";
        }
    });

    // 마우스 커서 변경
    map.on('pointermove', function(evt) {
        const pixel = map.getEventPixel(evt.originalEvent);
        const hit = map.hasFeatureAtPixel(pixel);
        map.getTargetElement().style.cursor = hit ? 'pointer' : '';
    });

    // 지도에 레이어 추가
    map.addLayer(markerLayer);
}
// 전역 변수로 벡터 레이어 선언

// 결과 보기 버튼 클릭 이벤트
document.getElementById('showResultsBtn').addEventListener('click', function() {
    const resultContainer = document.getElementById('result-container');
    resultContainer.classList.add('visible');

    // 부드러운 스크롤
    resultContainer.scrollIntoView({ behavior: 'smooth' });

    // 버튼 숨기기
    this.style.display = 'none';
});

// Enter 키로도 검색 가능하도록
document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter' && this.value.trim()) {
        document.getElementById('showResultsBtn').style.display = 'flex';
    }
});




window.toggleSidebar = toggleSidebar;
window.selectCategory = selectCategory;