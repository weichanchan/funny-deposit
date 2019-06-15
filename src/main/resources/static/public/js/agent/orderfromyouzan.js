$(function () {
    $("#jqGrid").jqGrid({
        url: '../orderfromyouzan/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, key: true},
            {label: '有赞订单号', name: 'youzanOrderId', index: 'youzan_order_id', width: 100},
            {label: '对接平台下单号', name: 'orderNo', index: 'order_no', width: 60},
            {label: '订单金额', name: 'orderPrice', index: 'order_price', width: 40},
            {label: '商品编号', name: 'wareNo', index: 'ware_no', width: 40},
            {label: '商品规格', name: 'formatInfo', index: 'format_info', width: 80},
            {label: '充值用户信息', name: 'rechargeInfo', index: 'recharge_info', width: 80},
            {
                label: '状态',
                name: 'status',
                index: 'status',
                width: 80,
                formatter: function (value, options, row) {
                    if (value == 1) {
                        return '<font color="green">充值成功</font>'
                    }
                    if (value == 2) {
                        return '<font color="yellow">待充值</font>'
                    }
                    if (value == 3) {
                        return '<font color="#ff8c00">充值中</font>'
                    }
                    if (value == 4) {
                        return '<font color="#ff8c00">退款成功</font>'
                    }
                    if (value == 5) {
                        return '<font color="red">需要手工退款</font>'
                    }
                    if (value == -1) {
                        return '<font color="red">充值失败，待退款</font>'
                    }
                    if (value == -2) {
                        return '<font color="red">充值异常，重试中</font>'
                    }
                    if (value == -3) {
                        return '<font color="red">有赞退款异常</font>'
                    }
                    return '未知状态';
                }
            },
            {
                label: '异常', name: 'exception', index: 'exception', width: 80,
                formatter: function (value, options, row) {
                    if (value = 'null') {
                        return ''
                    }
                    return '<font color="red">' + value + '</font>'
                }
            },
            {label: '创建时间', name: 'createTime', index: 'create_time', width: 80},
            {
                label: '操作', name: 'cards', index: 'cards', width: 80,
                formatter: function (value, options, row) {
                    return '<font color="green">提取卡密</font>'
                }
            }
        ],
        viewrecords: true,
        height: 650,
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
        orderFromYouzan: {},
        q: {
            no: "",
            wareNo: ""
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.orderFromYouzan = {};
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
        getCards: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            $.ajax({
                type: "POST",
                url: "../orderfromyouzan/cards?id=" + id,
                contentType: "application/json",
                data: JSON.stringify(vm.orderFromYouzan),
                success: function (r) {
                    if (r.code === 0) {
                        alert(r.cards);
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
                    url: "../orderfromyouzan/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('操作成功', function (index) {
                                window.location.reload();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        getInfo: function (id) {
            $.get("../orderfromyouzan/info/" + id, function (r) {
                vm.orderFromYouzan = r.orderFromYouzan;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {
                    "no": vm.q.no,
                    "wareNo": vm.q.wareNo
                }
            }).trigger("reloadGrid");
        }
    }
});