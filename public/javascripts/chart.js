$(function() {
	
	Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
	
	// See source code from the JSONP handler at https://github.com/highslide-software/highcharts.com/blob/master/samples/data/from-sql.php
	$.getJSON('chart?callback=?', function(data) {
				
		// create the chart
		$('#container').highcharts('StockChart', {
			chart : {
				zoomType: 'x'
			},

			navigator : {
				adaptToUpdatedData: false,
				series : {
					data : data
				}
			},

			scrollbar: {
				liveRedraw: false
			},
			
			title: {
				text: 'My EDF consumption'
			},
			
			subtitle: {
				text: 'Displaying 1.7 million data points in Highcharts Stock by async server loading'
			},
			
			rangeSelector : {
				buttons: [{
					type: 'hour',
					count: 1,
					text: '1h'
				}, {
					type: 'day',
					count: 1,
					text: '1d'
				}, {
					type: 'month',
					count: 1,
					text: '1m'
				}, {
					type: 'year',
					count: 1,
					text: '1y'
				}, {
					type: 'all',
					text: 'All'
				}],
				inputEnabled: true, 
				selected : 4 // all
			},
			
			xAxis : {
				events : {
					afterSetExtremes : afterSetExtremes
				},
				minRange: 3600 * 1000 // one hour
			},

			series : [{
				data : data,
				dataGrouping: {
					enabled: false
				}
			}]
		});
	});
});


/**
 * Load new data depending on the selected min and max
 */
function afterSetExtremes(e) {

	var currentExtremes = this.getExtremes(),
		range = e.max - e.min,
		chart = $('#container').highcharts();
		
	chart.showLoading('Loading data from server...');
	$.getJSON('chart?start='+ Math.round(e.min) +
			'&end='+ Math.round(e.max) +'&callback=?', function(data) {
		
		chart.series[0].setData(data);
		chart.hideLoading();
	});
}