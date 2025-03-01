import {mapping} from "./main";

import {fromLonLat, transform} from 'ol/proj';
import 'ol/ol.css';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import axios from "axios";



// 선택된 상태 추적
const selectedData = {
    category: null,
    options: new Set(),
    query: null,
    swLatlng : null,
    neLatlng :null,
};

var vectorSource;
var vectorLayer;


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

    selectedData.center = transform(mapping.getView().getCenter(), 'EPSG:3857', 'EPSG:4326');

    // 현재 화면의 Extent(경계 박스) 가져오기
    const extent = mapping.getView().calculateExtent(mapping.getSize());

    // 좌하단(left bottom)과 우상단(right top) 좌표 변환
    selectedData.swLatlng = transform([extent[0], extent[1]], 'EPSG:3857', 'EPSG:4326');
    selectedData.neLatlng = transform([extent[2], extent[3]], 'EPSG:3857', 'EPSG:4326');

    const data = await getData();
    markSearchData(data);
});


function getData() {
    const path = import.meta.env.VITE_API_URL + "/map/search";
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
            console.log(response.data)
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

function markSearchData(data) {
    // 기존 레이어 제거
    mapping.getLayers().forEach(layer => {
        if (layer === vectorLayer) {
            mapping.removeLayer(layer);
        }
    });

    // 마커 생성 및 스타일 적용
    const features = data.mapPositionDTOList.map(position => {
        const marker = new Feature({
            geometry: new Point(fromLonLat([position.longitude, position.latitude])),
            map_id: position.map_id,
            category3: position.category3,
            parkingAvailable: position.parkingAvailable
        });

        marker.setStyle(new Style({
            image: new Icon({
                src: './img/marker.png',
                scale: 1,
            })
        }));

        return marker;
    });

    // 벡터 소스 및 레이어 생성
    vectorSource = new VectorSource({
        features: features
    });

    vectorLayer = new VectorLayer({
        source: vectorSource
    });

    // 지도에 레이어 추가
    mapping.addLayer(vectorLayer);
}

window.toggleSidebar = toggleSidebar;
window.selectCategory = selectCategory;