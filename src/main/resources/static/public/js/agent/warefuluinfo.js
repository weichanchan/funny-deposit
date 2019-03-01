$(function () {
    $("#jqGrid").jqGrid({
        url: '../warefuluinfo/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '商品编号', name: 'outerSkuId', index: 'outer_sku_id', width: 80 },
			{ label: '商品名', name: 'wareName', index: 'ware_name', width: 80 }, 			
			{ label: '数量', name: 'num', index: 'num', width: 80 },
			{ label: '福禄商品编号', name: 'productId', index: 'product_id', width: 80 },
			{ label: '福禄商品编号（批量）', name: 'productHugeId', index: 'product_huge_id', width: 80 },
			{ label: '充值账号提取标识', name: 'mark', index: 'mark', width: 80 },
            { label: '批量渠道', name: 'type', index: 'type', width: 80 ,
                formatter: function (value, options, row) {
                    if(value == 1){
                        return'<font color="green">不区分</font>'
                    }
                    if(value == 2){
                        return'<font color="#ff8c00">区分</font>'
                    }
                    return '未知状态';
                }},
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80 }			
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
		wareFuluInfo: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.wareFuluInfo = {};
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
			var url = vm.wareFuluInfo.id == null ? "../warefuluinfo/save" : "../warefuluinfo/update";
			$.ajax({
				type: "POST",
			    url: url,
			    contentType: "application/json",
			    data: JSON.stringify(vm.wareFuluInfo),
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
				    url: "../warefuluinfo/delete",
				    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								window.location.reload();
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(id){
			$.get("../warefuluinfo/info/"+id, function(r){
                vm.wareFuluInfo = r.wareFuluInfo;
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