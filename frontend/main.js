import './style.css';
import { Map, View } from 'ol';
import TileLayer from 'ol/layer/Tile';
import { fromLonLat } from 'ol/proj';
import XYZ from 'ol/source/XYZ.js';
import Geolocation from 'ol/Geolocation.js';
import OSM from 'ol/source/OSM.js';

const view = new View({
  center: fromLonLat([0,0]),
  zoom: 16,       // 페이지 시작시 지도 레벨 설정(필수)
  minZoom: 5,     // 축소 지도 레벨 설정(필수)
  maxZoom: 18,     // 확대 지도 레벨 설정(필수),
  projection: 'EPSG:3857'	// 좌표계
})
const map = new Map({
  target: 'map',
  layers: [
    new TileLayer({
      //지센 소프트 지도로 할 경우
      // source: new XYZ({
      //   url: '//xy.gcen.co.kr/reverse/mapset/tile/{z}/{x}/{y}?mapset=vt_maplabel',
      // })
      source: new OSM(),
    })
  ],
  view: view,
});

const geolocation = new Geolocation({
  // enableHighAccuracy must be set to true to have the heading value.
  trackingOptions: {
    enableHighAccuracy: false,
  },
  projection: view.getProjection(),
});

// // 페이지 로드 시 1회 위치 추적
window.addEventListener('load', () => {
  geolocation.once('change', function() {
    const position = geolocation.getPosition();

    // 지도 중심 이동 및 확대
    map.getView().setCenter(position);

  });
  geolocation.setTracking(true); // 위치 추적 1회 실행
});

export const mapping = map;




