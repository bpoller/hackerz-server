var map;
var ajaxRequest;

function getXmlHttpObject() {
	if (window.XMLHttpRequest) {
		return new XMLHttpRequest();
	}
	if (window.ActiveXObject) {
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
	return null;
}

function initMap() {
	setupAjax();
	loadPosition();
}

function loadPosition() {
	var msg = 'position';
	ajaxRequest.onreadystatechange = onPositionLoaded;
	ajaxRequest.open('GET', msg, true);
	ajaxRequest.send(null);
}

function onPositionLoaded() {
	if (ajaxRequest.readyState == 4 && ajaxRequest.status == 200) {
		position = eval("(" + ajaxRequest.responseText + ")");
		console.log(position);
		setupMap(position);		
	}
}

function setupMap(position){
	// set up the map
	map = new L.Map('map');

	// create the tile layer with correct attribution
	var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib = 'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
	var osm = new L.TileLayer(osmUrl, {
		minZoom : 10,
		maxZoom : 18,
		attribution : osmAttrib
	});

	map.setView(new L.LatLng(position.latitude, position.longitude), 15);
	map.addLayer(osm);
	L.marker([ position.latitude, position.longitude ]).addTo(map);
}

function setupAjax() {
	ajaxRequest = getXmlHttpObject()
	if (ajaxRequest == null) {
		alert("This browser does not support HTTP Request");
		return;
	}
}