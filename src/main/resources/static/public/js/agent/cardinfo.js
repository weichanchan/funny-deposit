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
            {label: '密码/激活码', name: 'password', index: 'password', width: 80},
            {label: '关联商品id', name: 'wareNo', index: 'ware_no', width: 80},
            {label: '关联订单id', name: 'agentOrderNo', index: 'agent_order_no', width: 80},
            {label: '状态', name: 'status', index: 'status', width: 80,
                formatter: function (value, options, row) {
                    return value === 1 ? '未售出' : '已售出';
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
        wareNo:""
    },
    methods: {
        query: function () {
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
        getInfo: function (id) {
            $.get("../cardinfo/info/" + id, function (r) {
                vm.cardInfo = r.cardInfo;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        },
        btnback: function () {
            window.location.href="wareinfo.html";
        }
    }
});