$(function () {
    $("#jqGrid").jqGrid({
        url: '../wareinfo/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, key: true},
            {label: '商品编号', name: 'wareNo', index: 'ware_no', width: 80},
            {label: '代理商价格', name: 'agentPrice', index: 'agent_price', width: 80},
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
            }
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
    $("#accountPwd").click(function () {
        var s = $("#accountPwd").is(":checked");
        if (s) {
            showCardInfo = true;
        }
    })
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        showCardInfo: true,
        title: null,
        wareInfo: {},
        cardInfo: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.wareInfo = {};

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
        del: function (event) {
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: "../wareinfo/delete",
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
            $.get("../wareinfo/info/" + id, function (r) {
                vm.wareInfo = r.wareInfo;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
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
        accountPwdSelected: function () {
            document.getElementById("addCardInfoLable").style.display = "block"
            alert("a")
        }
    }
});