$(function () {
    $("#jqGrid").jqGrid({
        url: '../wareinfo/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, key: true},
            {label: '商品编号', name: 'wareNo', index: 'ware_no', width: 80,
                formatter: function (value, options, row) {
                    var s = vm.q.wareNo;
                    var reg = new RegExp("(" + s + ")", "g");
                    return  value.replace(reg, "<font color=red>$1</font>");
                }
            },
            {label: '商品名', name: 'wareName', index: 'ware_name', width: 80},
            {label: '代理商id', name: 'agentId', index: 'agent_id', width: 80},
            {label: '代理商价格(元)', name: 'agentPrice', index: 'agent_price', width: 80,
                formatter: function (value, options, row) {
                    return (value / 100).toFixed(2);
                }
            },
            {
                label: '充值类型', name: 'type', index: 'type', width: 80,
                formatter: function (value, options, row) {
                    return value === 1 ? '直充类型' : '卡密类型';
                }
            },
            {
                label: '商品状态', name: 'status', index: 'status', width: 80,
                formatter: function (value, options, row) {
                    return value === 1 ?
                        '<font color="green">可售</font>' :
                        '<font color="red">不可售</font>';
                }
            },
            {label: '商品总数', name: 'cardInfoTotal', index: 'cardInfoTotal', width: 80},
            {label: '商品库存', name: 'available', index: 'available', width: 80}
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        cardInfo: {},
        roleList: [],
        wareInfo:{
            status:1,
            roleIdList:[]
        },
        q:{
            wareNo:""
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reset: function () {
            vm.q.wareNo = "";
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.wareInfo = {status:1,roleIdList:[]};

            //获取角色信息
            this.getRoleList();
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id)

            //获取角色信息
            this.getRoleList();
        },
        saveOrUpdate: function (event) {
            var url = vm.wareInfo.id == null ? "../wareinfo/save" : "../wareinfo/update";
            $.ajax({
                type: "POST",
                url: url,
                contentType: "application/json",
                data: JSON.stringify(vm.wareInfo),
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        getInfo: function (id) {
            $.get("../wareinfo/info/" + id, function (r) {
                vm.wareInfo = r.wareInfo;
            });
        },
        offShelves: function(){
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }
            var data;
            var status;
            for(var i=0;i<ids.length;i++){
                data = $("#jqGrid").jqGrid("getRowData", ids[i]);
                status = data.status;
                if(status=='<font color="red">不可售</font>'){
                    alert("存在已下架商品，请重新选择！");
                    return;
                }
            }
            shelves(ids, 2);
        },
        onShelves: function(){
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }
            var data;
            var available;
            var status;
            var type;
            for(var i=0;i<ids.length;i++){
                data = $("#jqGrid").jqGrid("getRowData", ids[i]);
                available = data.available;
                type = data.type;
                if(type=="卡密类型"&&(available==0||available==null)){
                    alert("请选择库存不为0的卡密类商品！");
                    return;
                }
                status = data.status;
                if(status=='<font color="green">可售</font>'){
                    alert("存在已上架商品，请重新选择！");
                    return;
                }
            }
            shelves(ids, 1);
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData:{
                    "wareNo": vm.q.wareNo
                }
            }).trigger("reloadGrid");
        },
        addCardInfo: function () {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var data = $("#jqGrid").jqGrid("getRowData", id);
            var type = data.type;
            if (type == "直充类型") {
                alert("直充类型商品不能添加卡密信息！")
                return;
            }
            showCardInfo = true;
            var y = (window.screen.availHeight - 200);
            var x = (window.screen.availWidth - 200);
            var mywindow = window.open("addCardInfo.html?wareId=" + id, "_blank", "height=" + 400 + ",width=" + 600);
            mywindow.moveTo(x / 2, y / 2);
        },
        cardinfo: function () {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var data = $("#jqGrid").jqGrid("getRowData", id);
            var type = data.type;
            if (type == "直充类型") {
                alert("请选择卡密类型商品！")
                return;
            }
            var url = "cardinfo.html?wareId=" + id + "&wareNo=" + data.wareNo;
            // encodeURI 编码
            window.location.assign(encodeURI(url));
        },
        getRoleList: function(){
            $.get("../sys/role/select", function(r){
                vm.roleList = r.list;
            });
        }
    }
});

//只能输入两位小数
function checkDecimal(data) {
    var re = /([0-9]+\.[0-9]{2})[0-9]*/;
    var num = data.replace(/\D/g,'');
    return num;
}

//商品上下架
function shelves(ids, status){
    $.ajax({
        type: "POST",
        url: "../wareinfo/shelves",
        contentType:"application/json",
        data: JSON.stringify({
            "ids" : ids,
            "status" : status
        }),
        success: function (r) {
            if (r.code === 0) {
                alert('操作成功', function (index) {
                    vm.reload();
                });
            } else {
                alert(r.msg);
            }
        }
    });
}