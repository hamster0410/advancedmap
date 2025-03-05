import './style.css';
import './result.css'
import './modal.css'
import './route.css'
import {Map, Overlay, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
import { fromLonLat } from 'ol/proj';
import XYZ from 'ol/source/XYZ.js';
import Geolocation from 'ol/Geolocation.js';
import OSM from 'ol/source/OSM.js';


var enterOverlayId = "marker-enter-overlay";	// 마커 마우스 오버시 사용할 팝업


const view = new View({
  center: fromLonLat([0,0]),
  zoom: 16,       // 페이지 시작시 지도 레벨 설정(필수)
  minZoom: 5,     // 축소 지도 레벨 설정(필수)
  maxZoom: 18,     // 확대 지도 레벨 설정(필수),
  projection: 'EPSG:3857'	// 좌표계
})

// main.js
if (!window.appMap) { // 중복 초기화 방지
  window.appMap = new Map({
    target: 'map',
    layers: [
      new TileLayer({
        source : new XYZ({
          url: '//xy.gcen.co.kr/reverse/mapset/tile/{z}/{x}/{y}?mapset=vt_maplabel',
        })
        // source: new OSM(),
      }),
    ],
    view: new View({
      center: fromLonLat([0, 0]),
      zoom: 16,
    }),
  });
}

export const map = window.appMap;

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


// 마커 마우스 오버 오버레이 설정
function setOvrlay() {
  console.log("setOverlay")
  // var mouseOverPopup = $("#popup_over").get(0);
  var mouseOverPopup = document.createElement("div");

  mouseOverPopup.id = "popup_over";

  mouseOverPopup.className = "ol-popup-left";

  var overPopup = new Overlay({
    id: enterOverlayId,
    element: mouseOverPopup,
    positioning: 'bottom-center',
    offset: [0, -50],
    autoPan: true,
    autoPanAnimation: {
      duration: 150
    }
  });
  map.addOverlay(overPopup);
}

setOvrlay();



