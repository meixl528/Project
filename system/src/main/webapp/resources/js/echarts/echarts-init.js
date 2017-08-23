require.config({
	paths : {
		//echarts : './js/echarts'
		echarts : './resources/js/echarts'
	}
});

/**
 * meixl
 * @param doc
 * @param option
 */
var Echarts={};
var E= {};

//单个
Echarts.load = E.load = function load(doc,option){
	require([ 'echarts', 'echarts/chart/bar', 'echarts/chart/line','echarts/chart/map' ], function(ec) {
		// --- 折柱 ---
		ec.init(doc).setOption(option);
	})
}

//多个
Echarts.loads = E.loads = function loads(docs){
	require([ 'echarts', 'echarts/chart/bar', 'echarts/chart/line','echarts/chart/map' ], function(ec) {
		// --- 折柱 ---
		for(var i=0;i<docs.length;i++){
			ec.init(docs[i].doc).setOption(docs[i].option);
		}
	})
}
                                                                                                                                                                                                                                                                                                              
// 请求地址url, 参数param, 回调函数 callback
E.ajax = function ajax(url,param,callback){
	$.ajax({
		url:url,
		data:param,
		dataType:"json",
		type:"post",
		cache:false,
		success:function(data){
			if(data){
				callback(data);
			}else{
				alert("data is null")
			}
		},
		error:function(er){
			alert("error")
		}
	})
}
//Echarts = {"load":load,"loads":loads};
//E = {"load":load,"loads":loads};

