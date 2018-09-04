$(function () {
    $("#jqGrid").jqGrid({
        url: '../agentorder/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '代理商订单号', name: 'agentOrderNo', index: 'agent_order_no', width: 80 }, 			
			{ label: '京东订单号', name: 'jdOrderNo', index: 'jd_order_no', width: 80 },
			{ label: '订单类型，1：普通', name: 'type', index: 'type', width: 80 }, 			
			{ label: '清算时间', name: 'fintime', index: 'finTime', width: 80 }, 			
			{ label: '回调通知地址', name: 'notifyurl', index: 'notifyUrl', width: 80 }, 			
			{ label: '充值号码', name: 'rechargeNum', index: 'recharge_num', width: 80 }, 			
			{ label: '数量', name: 'quantity', index: 'quantity', width: 80 }, 			
			{ label: '商品编码', name: 'wareno', index: 'wareNo', width: 80 }, 			
			{ label: '成本价', name: 'costprice', index: 'costPrice', width: 80 }, 			
			{ label: '特殊属性', name: 'features', index: 'features', width: 80 }, 			
			{ label: '订单状态，1：充值成功；2：充值失败；3：充值中', name: 'status', index: 'status', width: 80 }, 			
			{ label: '订单创建时间', name: 'createTime', index: 'create_time', width: 80 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		agentOrder: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.agentOrder = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			var url = vm.agentOrder.id == null ? "../agentorder/save" : "../agentorder/update";
			$.ajax({
				type: "POST",
			    url: url,
			    contentType: "application/json",
			    data: JSON.stringify(vm.agentOrder),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		test: function(){
            var url =  "../api/beginDistill1";
            $.ajax({
                type: "GET",
                url: url,
                contentType: "application/json",
                data: {
                    sign : "1f3870be274f6c49b3e31a0c6728957f",
                    signType : "MD5",
                    timestamp : "20090707122322",
                    version : "1.0",
                    jdOrderNo : "ail000011111",
                    type : "1",
                    finTime : "20090707122322",
                    notifyUrl : "http://aaa",
                    rechargeNum : "2233576890",
                    quantity : "10",
                    wareNo : "af123456",
                    costPrice : "10000",
                    features : ""
                },
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: "../agentorder/delete",
				    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(id){
			$.get("../agentorder/info/"+id, function(r){
                vm.agentOrder = r.agentOrder;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});