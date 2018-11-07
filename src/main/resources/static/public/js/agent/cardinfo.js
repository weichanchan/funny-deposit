$(function () {
    //商品id
    var wareId = T.p('wareId');
    var wareNo = T.p('wareNo');
    var param = "";
    if(wareNo != undefined){
        param = "?wareNo=" + wareNo;
    }
    $("#jqGrid").jqGrid({
        url: '../cardinfo/list'+param,
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, key: true},
            {label: '账号', name: 'accountNo', index: 'account_no', width: 80},
            {label: '密码/激活码', name: 'password', index: 'password', width: 80,
                formatter: function (value, options, row) {
                    var s = vm.q.password;
                    var reg = new RegExp("(" + s + ")", "g");
                    return  value.replace(reg, "<font color=red>$1</font>");
                }
             },
            {label: '关联商品id', name: 'wareNo', index: 'ware_no', width: 80},
            {label: '关联订单id', name: 'agentOrderNo', index: 'agent_order_no', width: 80},
            {label: '状态', name: 'status', index: 'status', width: 80,
                formatter: function (value, options, row) {
                    return value == 1 ? '<font color="green">未售出</font>' : '已售出';
                }
            },
            {label: '有效期', name: 'expiryDate', index: 'expiry_date', width: 80},
            {label: '售出时间', name: 'rechargeTime', index: 'recharge_time', width: 80}
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
        wareNo:"",
        q:{
            password:"",
            status:""
        },
        statusSelect:[
            {id:"",name:"全部状态"},
            {id:"1",name:"未售出"},
            {id:"2",name:"已售出"}
        ],
        btn : null
    },
    methods: {
        query: function () {
            vm.btn = event.target.id;
            vm.reload();
        },
        reset: function () {
            $("#searchKey").val("");
            vm.q.password = "";
            vm.q.status = "";
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.cardInfo = {};
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            cardInfo.wareNo = vm.wareNo;
            var url = vm.cardInfo.id == null ? "../cardinfo/save" : "../cardinfo/update";
            $.ajax({
                type: "POST",
                url: url,
                contentType: "application/json",
                data: JSON.stringify(vm.cardInfo),
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
        del: function (event) {
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }
            var data;
            var status;
            for(var i=0;i<ids.length;i++){
                data = $("#jqGrid").jqGrid("getRowData", ids[i]);
                status = data.status;
                if(status=='已售出'){
                    alert("已售出卡密不能删除，请重新选择！");
                    return;
                }
            }
            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: "../cardinfo/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('操作成功', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        exportExcel: function() {
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }
            location.href = "../cardinfo/exportExcel?ids=" + JSON.stringify(ids);
        },
        getInfo: function (id) {
            $.get("../cardinfo/info/" + id, function (r) {
                vm.cardInfo = r.cardInfo;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = (vm.btn == "query")?1:$("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {
                    'password': vm.q.password,
                    "status":vm.q.status
                },
                page: page
            }).trigger("reloadGrid");
        },
        btnback: function () {
            window.location.href="wareinfo.html";
        }
    }
});