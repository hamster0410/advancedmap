import {mapping} from "./main";

import { transform } from 'ol/proj';
import 'ol/ol.css';
import { fromLonLat } from 'ol/proj';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import axios from 'axios';

// 마커 생성
const marker = new Feature({
    geometry: new Point(fromLonLat([127.152921, 37.26968181])),
});
// 마커 스타일 설정 (아이콘 or 기본 스타일)
marker.setStyle(
    new Style({
        image: new Icon({
            src: './img/marker.png',
            scale: 1,
        }),
    })
);

// 선택된 상태 추적
const selectedData = {
    category: null,
    options: new Set(),
    query: null,
};

// 벡터 소스와 레이어 추가
const vectorSource = new VectorSource({
    features: [marker],
});

const vectorLayer = new VectorLayer({
    source: vectorSource,
});

mapping.addLayer(vectorLayer);

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

    document.querySelectorAll('.checkbox-input').forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            if (checkbox.checked) {
                selectedData.options.add(checkbox.value);
            } else {
                selectedData.options.delete(checkbox.value);
            }
        });
    });

}

// 검색 버튼 클릭 시 로그 출력
document.querySelector('.search-button').addEventListener('click', () => {
    const searchInput = document.querySelector('.search-input');
    selectedData.query = searchInput.value.trim(); // 입력된 검색어 가져오기
    logSelectedData();
});




//데이터 변경 로그 출력
function logSelectedData(){
    console.log('선택된 카테고리:', selectedData.category||'없음');
    console.log('선택된 옵션:',Array.from(selectedData.options).join(', ')||'없음');
    console.log('작성한 검색어:',selectedData.query)
    console.log(selectedData.category +" "+ Array.from(selectedData.options).join(', ') + " " + selectedData.query + " " + mapping.getView().getCenter());

    const transformed = transform(mapping.getView().getCenter(), 'EPSG:3857', 'EPSG:4326');
    marker.getGeometry().setCoordinates(mapping.getView().getCenter());
    console.log('변환된 좌표:', transformed); // [127.152921, 37.26968181]

    const path = import.meta.env.API_BASE_URL + "/map/search";
    console.log()
    console.log(path);
    //
    // axios.get(path, {
    //     params: selectedData
    // })
    //     .then(response => {
    //         console.log('응답 데이터:', response.data);
    //     })
    //     .catch(error => {
    //         console.error('데이터 전송 실패:', error);
    //     });

}

window.toggleSidebar = toggleSidebar;
window.selectCategory = selectCategory;