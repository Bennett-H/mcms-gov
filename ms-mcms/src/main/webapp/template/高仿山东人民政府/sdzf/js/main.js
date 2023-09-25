/**
 * 分页渲染（需要ajax更新数据后需要重新渲染）
 * @param pageNo
 * @param pageSize
 * @param totalSize
 */
function initPage(pageNo, pageSize, totalSize) {
    layui.laypage.render({
        elem: 'page'
        , theme: "#2f6fb9"
        , curr: pageNo
        , limit: pageSize
        // , limits: [10, 20, 50, 100]
        , count: totalSize
        , layout: ['prev', 'page', 'next']
        , jump: function (obj, first) {
            $("#pageNo").val(obj.curr);
            $("#pageSize").val(obj.limit);
            if (!first) {
                submitSearchForm();
            }
        }
    });
}

/**
 * 取cookie
 * @param name
 * @returns {*}
 */
function getDecodeCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg)) return decodeURIComponent(arr[2]); else return null;
}

/**
 * 存cookie
 * @param cname
 * @param cvalue
 * @param exdays
 */
function setDecodeCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + exdays * 24 * 60 * 60 * 1e3);
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

/**
 * 刷新搜索记录cookie
 */
function flushSearchHistory() {
    var ck = getDecodeCookie("searched");
    if (ck) {
        $("#searchHistoryList").html("");
        var searchHistories = ck.split("#");
        for (var i = 0; i < searchHistories.length; i++) {
            var searchHistory = searchHistories[i];
            if (searchHistory.indexOf("@") > 0) {
                var array = searchHistories[i].split("@");
                var text = array[0];
                var category2 = array.length > 1 ? array[1] : "";
                $("#searchHistoryList").append("<a class='r-g-link-cell' title='" + text + "' href='?text=" + text + "&category2=" + category2 + "' target='_blank'>" + text + "</a>");
            }
        }
        $("#searchHistoryList").append("<a class=\"r-g-clear\" href=\"javascript:void(0);\" onclick=\"removeSearchHistory()\">清除所有搜索记录</a>");
        $("#searchHistoryGroup").show();
    }
}

/**
 * 清除搜索记录cookie
 */
function removeSearchHistory() {
    setDecodeCookie("searched", "", -1);
    $("#searchHistoryList").empty()
}

/**
 * 自动完成
 */
function bindAutoComplete() {
    $("#text").on("keyup click", function (event) {
        var keyword = $(this).val();
        if (keyword == "") {
            $("#word").hide();
            return;
        }
        var enReg = /^[A-Za-z\s]*$/;
        if (enReg.test(keyword) && keyword.length < 4) {
            return;
        }
        $.ajax({
            url: ctx + "suggest",
            type: "POST",
            data: $("#dataForm").serialize(),
            success: function (data) {
                if (data.length > 0) {
                    $("#word").empty().show();
                    $.each(data, function () {
                        var title = this;
                        var clickWorkHtml = '<div class="click_work">' + title + '</div>';
                        $("#word").append(clickWorkHtml);
                    });
                } else {
                    $("#word").empty().hide();
                }
            },
            error: function () {
                $("#word").empty().show();
            }
        });
    });
    $(document).on("click", function (e) {
        var target = $(".click_work");
        if (target.is(e.target)) {
            var word = $(e.target).text();
            $("#text").val(word);
            $("#searchBtn").trigger("click");
        } else {
            $("#word").empty().hide();
        }
    });
}

/**
 * 模糊搜索
 */
function searchSubmit() {
    var state = {
        title: "",
        url: window.location.href.split("?")[0]
    };
    if (history.pushState) {
        history.pushState(state, "", "?" + $("#dataForm").serialize());
    }
    $("#pageNo").val("1");
    $("#accurateMode").val("");
    submitSearchForm();
    if ($("#text").val() != "" && typeof wondersLog != "undefined") {
        wondersLog.track_event('智能检索', {targetName: $("#text").val()});
    }
}

/**
 * 精确搜索
 */
function accurateSearchSubmit() {
    $("#pageNo").val("1");
    $("#accurateMode").val("1");
    submitSearchForm();
}

/**
 * 提交搜索表单
 */
function submitSearchForm() {
    formula.query({
        formId: "dataForm",
        before: function () {
            $("#word").empty().hide();
            $("#searchBtn").focus();
        },
        after: function () {
            $("#word").empty().hide();
            $("#searchBtn").focus();
            $("#filterTextSpan").text($("#districtLocate").text());

            if ($("#totalSize").val()) {
                $("#result-count").show();
            } else {
                $("#result-count").hide();
            }

            $("#totalSizeSpan").text($("#totalSize").val());
            $("#QTimeSpan").text(parseInt($("#QTime").val()) / 1000);
            $("#accurateText").text($("#accurateWord").val());
            $("#sourceText").text($("#sourceWord").val());
            if ($("#accurateMode").val() != "1" && $("#accurateText").text() != ""
                && $("#accurateText").text() != $("#sourceText").text()) {
                $("#accurateDiv").show();
            } else {
                $("#accurateDiv").hide();
            }
            if ($("#sortMode").val() == "1") {
                $("#drop2 span").text("时间排序");
            }
            highlight();
            flushSearchHistory();
        }
    });
}

/**
 * 推荐
 */
function getReferral() {
    $.ajax({
        url: ctx + "referral",
        type: "post",
        data: "text=" + $("#text").val(),
        cache: false,
        async: true,
        success: function (data) {
            if (data['data'] && data['data'].length > 0) {
                var html = [];
                for (var i in data['data']) {
                    html.push("<li><a href='javascript:void(0);' onclick='toSearch(this)'>" + data['data'][i] + "</li>");
                }
                $("#tagbox ul").html(html.join(""));
                $("#tagbox").show();
                sphere.initSphere();
            }
        }
    });
}

/**
 * 搜索跳转
 * @param obj
 */
function toSearch(obj) {
    location.href = ctx + "?text=" + encodeURIComponent($(obj).data("text"));
}

/**
 * 高亮
 */
function highlight() {
    var keyword = $("#text").val();
    $(".r-i-til>a,.r-i-con>p").each(function () {
        var html = $(this).html();
        if (html && html.indexOf("</span>") == -1) {
            var array = html.split(keyword);
            if (array.length > 1) {
                html = array.join("<span class='hl'>" + keyword + "</span>");
                $(this).html(html);
            }
        }
    });
}

// 右侧悬浮链接收起/展开
$('.rmenu1').on('click', '.floatnav-control', function () {
    if ($('.foldbox').is('.fold')) {
        $('.foldbox>dl').show();
        $('.foldbox').removeClass('fold').animate({
            height: '365px'
        }, 300, function () {
            $('.floatnav-control').text('收起');
        });
    } else {
        $('.foldbox').addClass('fold').animate({
            height: '68px'
        }, 300, function () {
            $('.foldbox.fold>dl').hide();
            $('.floatnav-control').text('展开');
        });
    }
});

// 返回顶部按钮
$(window).scroll(function () {
    if ($(document).scrollTop() > 500) {
        $('.link-floatnav-top').addClass('active');
    } else {
        $('.link-floatnav-top').removeClass('active');
    }
});

/**
 * 搜索初始化
 */
function initSearch() {
    // $('[data-toggle="popover"]').popover();
    $('.s-f-btn').on('click', function () {
        if ($(this).is('.dislike')) {
            $('.s-f-form').fadeIn();
        }
    });
    $('.s-f-form .close,.s-f-form .btn-default').on('click', function () {
        $('.s-f-form').fadeOut();
        // $('.s-f-form form')[0].reset();
    });

    $('.s-c-nums-filter .dropdown-menu').on('click', 'a', function () {
        var _condition = $(this).text();
        $(this).parents('.dropdown').find('.dropdown-toggle').html("<span>" + _condition + '</span><i class="layui-icon layui-icon-down">');
        var filterId = $(this).data("filter-id");
        var filterValue = $(this).data("filter-value");
        $("#" + filterId).val(filterValue);
        searchSubmit();
    });

    $('.r-g-clear').on('click', function () {
        $(this).siblings().remove();
    });

    $('.showmap').on('click', function () {
        $('.filterModal').modal('show');
    });

    $('.executeLevel').on('click', function () {
        var category1 = $("#category1").val();
        var executeLevel = $(this).data("execute-level");
        if (category1 == executeLevel) {
            $("#category1").val("");
        } else {
            $("#category1").val(executeLevel);
        }
        searchSubmit();
    });

    $('.channel-tab').on('click', function () {
        var channel2 = $("#channel2").val();
        var tabValue = $(this).data("channel");
        $(this).parent().addClass("active");
        $(this).parent().siblings().removeClass("active");
        if (channel2 != tabValue) {
            $("#channel1").val("");
            $("#channel2").val(tabValue);
            initChannelSelector();
            searchSubmit();
        }
    });
}

if (typeof wondersLog !== "undefined") {
    wondersLog.init("sdywtb", {
        loaded: function (sdk) {
        }
    });
}

var layerPageIndex;

/**
 * 打开区域选择
 */
function selectDivision() {
    layer.open({
        type: 1,
        title: "选择您所在的区域",
        offset: "100px",
        area: ['60%', '380px'],
        // btn: ['确定'],
        content: $("#select-divisions").html(),
        success: function (layero, index) {
            $("#select-divisions").empty();
            layerPageIndex = index;
        },
        cancel: function (index, layero) {
            $("#select-divisions").html($(".divisions"))
        }
    });
}

function initChannelSelector() {
    var channel2 = $("#channel2").val();
    if (channel2 == "" || channel2 == "一网通办") {
        $("#use-level-selector").show();
        $("#item-type-selector").show();
        $("#news-channel1-selector").hide();
        $("#news-channel1-selector").html("");
    } else {
        $("#use-level-selector").hide();
        $("#item-type-selector").hide();
        $("#use-level-selector").find('.dropdown-toggle').html("<span>行使层级</span><i class=\"layui-icon layui-icon-down\">");
        $("#item-type-selector").find('.dropdown-toggle').html("<span>事项类型</span><i class=\"layui-icon layui-icon-down\">");
        $("#category1").val("");
        $("#category3").val("");
        var selectorHtml = "<a id=\"drop5\" target=\"_blank\" href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">\n" +
            "<span>主题类型</span><i class=\"layui-icon layui-icon-down\"></i>\n" +
            "</a>\n" +
            "<ul class=\"dropdown-menu dropdown-menus\" aria-labelledby=\"drop5\">\n" +
            "<li class=\"qbsx\">\n" +
            "<a href=\"javascript:void(0);\" data-filter-id=\"channel1\" data-filter-value=\"\">所有类型</a>\n" +
            "</li>\n";
        var subChannels = eval('(' + $("#subChannels").val() + ')');
        var channel1List = subChannels[channel2];
        for (var i = 0; i < channel1List.length; i++) {
            selectorHtml += "<li><a href=\"javascript:void(0);\" data-filter-id=\"channel1\" data-filter-value=\"" + channel1List[i] + "\">" + channel1List[i] + "</a></li>";
        }
        selectorHtml += "</ul>";
        $("#news-channel1-selector").html(selectorHtml);
        initSearch();
        $("#news-channel1-selector").show();
    }
}

function renderFilter(index) {
    $("#filter-wrap .filter").each(function () {
        var indexes = $(this).data("index");
        var array = indexes.split(",");
        if (array.indexOf(index.toString()) > -1) {
            $(this).show();
        } else {
            $(this).hide();
            $(this).find("select").val("");
        }
    })
}

function moreSubItem(obj) {
    $(obj).remove();
    $(".sub-items").css("display", "inline-block");
}

function changeSubItem(obj) {
    if (!$(obj).hasClass("active")) {
        $(obj).siblings().removeClass("active");
        $(obj).addClass("active");
        $(obj).closest(".result-op").find(".sub-items-title, .sub-items-content, .sub-items-detail").removeClass("active");
        $("#sub-item-title-" + $(obj).data("id")).addClass("active");
        $("#sub-item-content-" + $(obj).data("id")).addClass("active");
        $("#sub-item-detail-" + $(obj).data("id")).addClass("active");
    }
}

function changeDivision(obj) {
    $("#districtLocate").text($(obj).text());
    $("#category2").val($(obj).data("code"));
    searchSubmit();
}

function changeChannel(obj) {
    $(obj).closest("li").addClass("active").siblings().removeClass("active");
    renderFilter($(obj).data("index"));
    var channel2 = $(obj).data("channel");
    var wrap = $("#channel1-wrap");
    wrap.hide();
    $("#channel1").val("");
    $("#channel2").val(channel2);
    $.ajax({
        url: "loadNewsLastChannels",
        type: "get",
        data: "channel=" + channel2,
        success: function (data) {
            var html = [];
            html.push("<option value=''>主题类型</option>");
            if (data.length) {
                for (var i in data) {
                    html.push("<option value='" + data[i].value + "'>" + data[i].value + "</option>");
                }
                wrap.show();
            }
            $("#channel1").html(html.join(""));
            form.render();
            searchSubmit();
        }
    });
}

!function () {
    window.sphere = function () {
        var radius = 100;
        var dtr = Math.PI / 180;
        var d = 200;
        var mcList = [];
        var active = false;
        var lasta = 1;
        var lastb = 1;
        var distr = true;
        var tspeed = 5;
        var size = 250;
        var mouseX = 0;
        var mouseY = 0;
        var howElliptical = 1;
        var aA = null;
        var oDiv = null;
        var initSphere = function () {
            var i = 0;
            var oTag = null;
            oDiv = document.getElementById("tagbox");
            aA = oDiv.getElementsByTagName("a");
            for (i = 0; i < aA.length; i++) {
                oTag = {};
                oTag.offsetWidth = aA[i].offsetWidth;
                oTag.offsetHeight = aA[i].offsetHeight;
                mcList.push(oTag);
            }
            sineCosine(0, 0, 0);
            positionAll();
            oDiv.onmouseover = function () {
                active = false;
            };
            oDiv.onmouseout = function () {
                active = true;
            };
            oDiv.onmousemove = function (ev) {
                var oEvent = window.event || ev;
                mouseX = oEvent.clientX - (oDiv.offsetLeft + oDiv.offsetWidth / 2);
                mouseY = oEvent.clientY - (oDiv.offsetTop + oDiv.offsetHeight / 2);
                mouseX /= 5;
                mouseY /= 5;
            };
            setInterval(update, 30);
        };
        var update = function () {
            var a;
            var b;
            if (active) {
                a = -Math.min(Math.max(-mouseY, -size), size) / radius * tspeed;
                b = Math.min(Math.max(-mouseX, -size), size) / radius * tspeed;
            } else {
                a = lasta * .98;
                b = lastb * .98;
            }
            lasta = a;
            lastb = b;
            if (Math.abs(a) <= .01 && Math.abs(b) <= .01) {
                return;
            }
            var c = 0;
            sineCosine(a, b, c);
            for (var j = 0; j < mcList.length; j++) {
                var rx1 = mcList[j].cx;
                var ry1 = mcList[j].cy * ca + mcList[j].cz * -sa;
                var rz1 = mcList[j].cy * sa + mcList[j].cz * ca;
                var rx2 = rx1 * cb + rz1 * sb;
                var ry2 = ry1;
                var rz2 = rx1 * -sb + rz1 * cb;
                var rx3 = rx2 * cc + ry2 * -sc;
                var ry3 = rx2 * sc + ry2 * cc;
                var rz3 = rz2;
                mcList[j].cx = rx3;
                mcList[j].cy = ry3;
                mcList[j].cz = rz3;
                per = d / (d + rz3);
                mcList[j].x = howElliptical * rx3 * per - howElliptical * 2;
                mcList[j].y = ry3 * per;
                mcList[j].scale = per;
                mcList[j].alpha = per;
                mcList[j].alpha = (mcList[j].alpha - .6) * (10 / 6);
            }
            doPosition();
            depthSort();
        };
        var depthSort = function () {
            var i = 0;
            var aTmp = [];
            for (i = 0; i < aA.length; i++) {
                aTmp.push(aA[i]);
            }
            aTmp.sort(function (vItem1, vItem2) {
                if (vItem1.cz > vItem2.cz) {
                    return -1;
                } else if (vItem1.cz < vItem2.cz) {
                    return 1;
                } else {
                    return 0;
                }
            });
            for (i = 0; i < aTmp.length; i++) {
                aTmp[i].style.zIndex = i;
            }
        };
        var positionAll = function () {
            var phi = 0;
            var theta = 0;
            var max = mcList.length;
            var i = 0;
            var aTmp = [];
            var oFragment = document.createDocumentFragment();
            //随机排序
            for (i = 0; i < aA.length; i++) {
                aTmp.push(aA[i]);
            }
            aTmp.sort(function () {
                return Math.random() < .5 ? 1 : -1;
            });
            for (i = 0; i < aTmp.length; i++) {
                oFragment.appendChild(aTmp[i]);
            }
            oDiv.appendChild(oFragment);
            for (var i = 1; i < max + 1; i++) {
                if (distr) {
                    phi = Math.acos(-1 + (2 * i - 1) / max);
                    theta = Math.sqrt(max * Math.PI) * phi;
                } else {
                    phi = Math.random() * Math.PI;
                    theta = Math.random() * 2 * Math.PI;
                }
                //坐标变换
                mcList[i - 1].cx = radius * Math.cos(theta) * Math.sin(phi);
                mcList[i - 1].cy = radius * Math.sin(theta) * Math.sin(phi);
                mcList[i - 1].cz = radius * Math.cos(phi);
                aA[i - 1].style.left = mcList[i - 1].cx + oDiv.offsetWidth / 2 - mcList[i - 1].offsetWidth / 2 + "px";
                aA[i - 1].style.top = mcList[i - 1].cy + oDiv.offsetHeight / 2 - mcList[i - 1].offsetHeight / 2 + "px";
            }
        };
        var doPosition = function () {
            var l = oDiv.offsetWidth / 2;
            var t = oDiv.offsetHeight / 2;
            for (var i = 0; i < mcList.length; i++) {
                aA[i].style.left = mcList[i].cx + l - mcList[i].offsetWidth / 2 + "px";
                aA[i].style.top = mcList[i].cy + t - mcList[i].offsetHeight / 2 + "px";
                aA[i].style.fontSize = Math.ceil(12 * mcList[i].scale / 2) + 4 + "px";
                aA[i].style.filter = "alpha(opacity=" + 100 * mcList[i].alpha + ")";
                aA[i].style.opacity = mcList[i].alpha;
            }
        };
        var sineCosine = function (a, b, c) {
            sa = Math.sin(a * dtr);
            ca = Math.cos(a * dtr);
            sb = Math.sin(b * dtr);
            cb = Math.cos(b * dtr);
            sc = Math.sin(c * dtr);
            cc = Math.cos(c * dtr);
        };
        return {
            initSphere: initSphere
        };
    }();
}();