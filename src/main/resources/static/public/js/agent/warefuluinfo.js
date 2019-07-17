$(function () {
    $("#jqGrid").jqGrid({
        url: '../warefuluinfo/list?type=' + $.getUrlParam('type'),
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '商品编号', name: 'outerSkuId', index: 'outer_sku_id', width: 80 },
			{ label: '商品名', name: 'wareName', index: 'ware_name', width: 80 }, 			
			{ label: '数量', name: 'num', index: 'num', width: 80 },
			{ label: '对接平台商品编号', name: 'productId', index: 'product_id', width: 80 },
			{ label: '对接平台商品编号（批量）', name: 'productHugeId', index: 'product_huge_id', width: 80 },
			{ label: '充值账号提取标识', name: 'mark', index: 'mark', width: 80 },
            { label: '充值平台', name: 'rechargeChannel', index: 'recharge_channel', width: 80 ,
                formatter: function (value, options, row) {
                    if(value == 1){
                        return'<font color="green">旧卡门平台</font>'
                    }
                    if(value == 2){
                        return'<font color="#ff8c00">新福禄平台</font>'
                    }
                    if(value == 3){
                        return'<font color="#gray">A平台</font>'
                    }
                    if(value == 4){
                        return'<font color="#gray">超人平台</font>'
                    }
                    return '未知状态';
                }},
            { label: '批量渠道', name: 'type', index: 'type', width: 80 ,
                formatter: function (value, options, row) {
                    if(value == 1){
                        return'<font color="green">卡密</font>'
                    }
                    if(value == 2){
                        return'<font color="#ff8c00">非卡密</font>'
                    }
                    return '未知状态';
                }},
			{ label: '处理角色', name: 'roleName', index: 'role_name', width: 80 },
			{ label: '创建时间', name: 'createTime', index: 'create_time', width: 80 }
        ],
		viewrecords: true,
        height: 650,
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
        roleList: [],
		wareFuluInfo: {roleList:[]},
		q: {
			no: ""
		}

	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.wareFuluInfo = {roleList:[]};
			//获取角色信息
			this.getRoleList();
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
			//获取角色信息
			this.getRoleList();
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
		check: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}

			confirm('确定要执行查询吗？', function(){
				$.ajax({
					type: "POST",
				    url: "../warefuluinfo/check?id=" + id,
				    contentType: "application/json",
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
                page:page,
				postData: {
					"no": vm.q.no
				}
            }).trigger("reloadGrid");
		},
		getRoleList: function(){
			$.get("../sys/role/select", function(r){
				vm.roleList = r.list;
			});
		}
	}
});

(function($){
$.getUrlParam = function(name)
{
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r!=null) return unescape(r[2]); return null;
}
})(jQuery);