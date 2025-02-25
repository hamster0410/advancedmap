import './style.css';
import { Map, View } from 'ol';
import TileLayer from 'ol/layer/Tile';
import { fromLonLat } from 'ol/proj';
import XYZ from 'ol/source/XYZ.js';
const map = new Map({
  target: 'map',
  layers: [
    new TileLayer({
      source: new XYZ({
        url: '//xy.gcen.co.kr/reverse/mapset/tile/{z}/{x}/{y}?mapset=vt_maplabel',
      })
    })
  ],
  view: new View({
    center: fromLonLat([126.920757, 37.618855]),
    zoom: 16,       // 페이지 시작시 지도 레벨 설정(필수)
    minZoom: 5,     // 축소 지도 레벨 설정(필수)
    maxZoom: 18,     // 확대 지도 레벨 설정(필수),
    projection: 'EPSG:3857'	// 좌표계
  })
});
