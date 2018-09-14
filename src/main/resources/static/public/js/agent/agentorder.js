$(function () {
    $("#jqGrid").jqGrid({
        url: '../agentorder/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, key: true, hidden:true},
            {label: '代理商订单号', name: 'agentOrderNo', index: 'agent_order_no', width: 80},
            {label: '京东订单号', name: 'jdOrderNo', index: 'jd_order_no', width: 80},
            {
                label: '订单类型', name: 'type', index: 'type', width: 70,
                formatter: function (value, options, row) {
                    return value === 1 ? '普通' : '其他';
                }
            },
            // {label: '清算时间', name: 'finTime', index: 'fin_time', width: 80},
            // {label: '回调通知地址', name: 'notifyUrl', index: 'notify_url', width: 120},
            {label: '充值号码', name: 'rechargeNum', index: 'recharge_num', width: 80},
            {label: '数量', name: 'quantity', index: 'quantity', width: 50},
            {label: '商品编码', name: 'wareNo', index: 'ware_no', width: 80},
            {
                label: '成本价(元)', name: 'costPrice', index: 'cost_price', width: 80,
                formatter: function (value, options, row) {
                    return (value / 100).toFixed(2);
                }
            },
            {
                label: '订单状态', name: 'status', index: 'status', width: 80,
                formatter: function (value, options, row) {
                    if (value === 0) {
                        return '新创建';
                    } else if (value === 1) {
                        return '<font color="green">处理成功</font>'
                    } else if (value===2){
                        return '<font color="red">处理失败</font>';
                    } else {
                        return '<font color="gray">处理中</font>';
                    }
                }
            },
            {
                label: '充值状态', name: 'rechargeStatus', index: 'recharge_status', width: 80,
                formatter: function (value, options, row) {
                    if (value === 0) {
                        return '未充值';
                    } else if (value === 1) {
                        return '<font color="green">充值成功</font>';
                    } else if (value === 2){
                        return '<font color="red">充值失败</font>'
                    } else {
                        return '<font color="gray">充值中</font>';
                    }
                }
            },
            // {label: '特殊属性', name: 'features', index: 'features', width: 80},
            // { label: '订单创建时间', name: 'createTime', index: 'create_time', width: 80 }
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
    // 7秒一次去查询是否有新的订单
    window.setInterval("vm.newOrderWarning()",7000);
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        agentOrder: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.agentOrder = {};
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
            var url = vm.agentOrder.id == null ? "../agentorder/save" : "../agentorder/update";
            $.ajax({
                type: "POST",
                url: url,
                contentType: "application/json",
                data: JSON.stringify(vm.agentOrder),
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
                    url: "../agentorder/delete",
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
            $.get("../agentorder/info/" + id, function (r) {
                vm.agentOrder = r.agentOrder;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        },
        newOrderWarning:function(){
            $.get("../agentorder/infoNew", function (r) {
                if(r.code == 0){
                    vm.reload();
                    var n = new Audio("../public/media/2018-09-12_12_31_38.mp3");
                    n.play();
                    // alert("您有新的订单请及时处理");
                }
            });
        },
        startHandle: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var rowData = $("#jqGrid").jqGrid("getRowData", id);
            confirm('确定要处理选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: "../agentorder/handleAgentOrder/" + id,
                    contentType: "application/json",
                    // dataType:json ,
                    success: function (r) {
                        if (r.code == 0) {
                            alert('正在处理... ...', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        handleSuccess: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var rowData = $("#jqGrid").jqGrid("getRowData", id);
            var status = rowData.status;
            if(status=="<font color=\"gray\">处理中</font>"){
                alert("订单正在处理，请稍后~");
                return;
            }
            if(status=="<font color=\"green\">处理成功</font>" || status=="<font color=\"red\">处理失败</font>"){
                alert("订单已处理，请不要重复操作！");
                return;
            }
            confirm('确定要处理选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: "../agentorder/handleSuccess/" + id,
                    contentType: "application/json",
                    // dataType:json ,
                    success: function (r) {
                        if (r.code == 0) {
                            alert('处理成功！', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        handleFailed: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var rowData = $("#jqGrid").jqGrid("getRowData", id);
            var status = rowData.status;
            if(status=="<font color=\"gray\">处理中</font>"){
                alert("订单正在处理，请稍后~");
                return;
            }
            if(status=="<font color=\"green\">处理成功</font>" || status=="新创建"){
                alert("请选择处理失败的订单");
                return;
            }
            confirm('确定要处理选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: "../agentorder/handleFailed/" + id,
                    contentType: "application/json",
                    // dataType:json ,
                    success: function (r) {
                        if (r.code == 0) {
                            alert('发送通知成功！', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        }
    }
});