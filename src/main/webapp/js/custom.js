// 共同初始脚本
$(document).ready(function() {
	try {
		// 公告
		$('#breakingnews').BreakingNews({
			title : '&gt; 报警信息',
			background : '#fff5f5',
			titlecolor : '#fff',
			titlebgcolor : '#ee615a',
			linkcolor : '#8d0903',
			linkhovercolor : 'red',
			border : '1px solid #ff8a84',
			timer : 4000,
			effect : 'slide'
		});
		$(".bn-close .fa-close").click(function() {
			$(".BreakingNewsControllerbox").hide();
		});
	} catch (e) {
	}
	
	// 处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外  
	function banBackSpace(e){     
	    var ev = e || window.event;//获取event对象     
	    var obj = ev.target || ev.srcElement;//获取事件源     
	    
	    var t = obj.type || obj.getAttribute('type');//获取事件源类型    
	      
	    //获取作为判断条件的事件类型  
	    var vReadOnly = obj.getAttribute('readonly');  
	    var vEnabled = obj.getAttribute('enabled');  
	    //处理null值情况  
	    vReadOnly = (vReadOnly == null) ? false : vReadOnly;  
	    vEnabled = (vEnabled == null) ? true : vEnabled;  
	      
	    //当敲Backspace键时，事件源类型为密码或单行、多行文本的，  
	    //并且readonly属性为true或enabled属性为false的，则退格键失效  
	    var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea")   
	                && (vReadOnly==true || vEnabled!=true))?true:false;  
	     
	    //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效  
	    var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")  
	                ?true:false;          
	      
	    //判断  
	    if(flag2){  
	        return false;  
	    }  
	    if(flag1){     
	        return false;     
	    }     
	}  
	
	// 禁止后退键 作用于Firefox、Opera  
	document.onkeypress=banBackSpace;  
	// 禁止后退键  作用于IE、Chrome  
	document.onkeydown=banBackSpace;
});

// 隐藏显示左侧树
function showTreeOrHide(obj) {

	if ($(obj).parent().hasClass("active")) {
		$(obj).parent().attr("class", "collapse-bg");
		$(".main-product-body").attr("style", "left:220");
		$(".sidebar-menu-second-collapse").removeAttr("style");
		$(".sidebar-menu-second-collapse").css("left", "220");
		$(".sidebar-menu-second").show();
		return;
	} else {
		$(obj).parent().attr("class", "collapse-bg active");
		$(".sidebar-menu-second-collapse").removeAttr("style");
		$(".sidebar-menu-second-collapse").css("left", "0");
		$(".main-product-body").attr("style", "left:0");
		$(".sidebar-menu-second").hide();
		return;
	}
}

// 树形Json整理
function transData(a, idStr, pidStr, chindrenStr) {
	var r = [], hash = {}, id = idStr, pid = pidStr, children = chindrenStr, i = 0, j = 0, len = a.length;
	for (; i < len; i++) {
		hash[a[i][id]] = a[i];
	}
	var pidLev = 0, hashLev = [], level = 0;
	for (; j < len; j++) {
		var aVal = a[j], hashVP = hash[aVal[pid]];
		if (hashVP) {
			if (pidLev != aVal.pid) {
				pidLev = aVal.pid;
				if (hashLev[aVal] == undefined) {
					level = hashVP.level;
					level++;
					hashLev.push(aVal);
				}
			}
			aVal["level"] = level;
			!hashVP[children] && (hashVP[children] = []);
			hashVP[children].push(aVal);
		} else {
			aVal["level"] = 0;
			r.push(aVal);
		}
	}
	return r;
}

// 得到Json同一级别的Json对象
function getNodesByParam(nodes, key, value) {
	if (!nodes || !key)
		return [];
	var childKey = "children", result = [];
	for ( var i = 0, l = nodes.length; i < l; i++) {
		if (nodes[i][key] == value) {
			result.push(nodes[i]);
		}
		result = result.concat(getNodesByParam(nodes[i][childKey], key, value));
	}
	return result;
}

// 树形菜单右击添加事件
function OnRightClick(event, treeId, treeNode) {
	if (!treeNode && event.target.tagName.toLowerCase() != "button"
			&& $(event.target).parents("a").length == 0) {
		zTree.cancelSelectedNode();
		showRMenu("root", event.clientX, event.clientY);
	} else if (treeNode && !treeNode.noR) {
		zTree.selectNode(treeNode);
		showRMenu("node", event.clientX, event.clientY);
	}
}

// 右键显示方法
function showRMenu(type, x, y) {
	$("#rMenu ul").show();
	if (type == "root") {
		$("#m_del").hide();
	} else {
		$("#m_del").show();
	}
	rMenu.css({
		"top" : y + "px",
		"left" : x + "px",
		"visibility" : "visible"
	});

	$("body").bind("mousedown", onBodyMouseDown);
}

// 右键隐藏方法
function hideRMenu() {
	if (rMenu)
		rMenu.css({
			"visibility" : "hidden"
		});
	$("body").unbind("mousedown", onBodyMouseDown);
}

// 树形鼠标压下
function onBodyMouseDown(event) {
	if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length > 0)) {
		rMenu.css({
			"visibility" : "hidden"
		});
	}
}

// 清空错误信息
function clearErrInfo(name) {
	jQuery("input[name='" + name + "']").parent().parent('.form-group')
			.removeClass("has-error");
	jQuery("input[name='" + name + "']").parent().parent('.form-group').find(
			"small").attr("data-bv-result", "NOT_VALIDATED");
	jQuery("input[name='" + name + "']").parent().parent('.form-group').find(
			"small").css("display", "none");
}

/**
 * 
 * 文件描述: 系统中公有的js方法。
 * 
 * @author tanglvshuang
 * @version 1.0 创建时间： 2012-08-30 上午17:46:07
 *          ********************************更新记录******************************
 *          版本： 1.0 修改日期： 2012-12-01 修改人： tanglvshuang 修改内容：
 *          *********************************************************************
 */
// 系统根路径
var rootPath = getRootPath();

/**
 * 方法描述：获取系统根路径
 */
function getRootPath() {
	// 获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
	var curWwwPath = window.document.location.href;
	// 获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
	var pathName = window.document.location.pathname;
	var pos = curWwwPath.indexOf(pathName);
	// 获取主机地址，如： http://localhost:8083
	var localhostPaht = curWwwPath.substring(0, pos);
	// 获取带"/"的项目名，如：/uimcardprj
	var projectName = pathName.substring(0,
			pathName.substring(1).indexOf('/') + 1);
	return (localhostPaht + projectName);
}

// 隐藏显示 根节点
function hideOrShow(obj) {
	$(".menu-third-content:visible").hide();
	$(".treeview-menu li a").attr("class", "");
	if ($(obj).hasClass("fa-caret-down")) {
		$(obj).parent().parent().find(".treeview-menu:visible").eq(0).hide();
		$(obj).removeClass("fa-caret-down").addClass("fa-caret-up");
		return;
	} else {
		$(obj).parent().parent().find(".treeview-menu").eq(0).show();
		$(obj).removeClass("fa-caret-up").addClass("fa-caret-down");
		return;
	}

}
$(".treeview").find("i:eq(0)").click(function() {
	hideOrShow(this);
});

// 点击 三级树
$(".secondmenu-list").find("a").click(function() {
	
	var obj = this;
	if(!$(obj).attr("id")){
		return;
	}
	var url = rootPath + "/" + $(obj).attr("id");
	window.open(url, "_self");
});

// Index主页点击
$(".col-xs-3").find("a").click(function() {
	var obj = this;
	if(!$(obj).attr("id")){
		return;
	}
	var url = rootPath + "/" + $(obj).attr("id");
	window.open(url, "_self");
});

$(".homepage").find("a").click(function() {
	var obj = this;
	var url = rootPath + "/" + $(obj).attr("id");
	window.open(url, "_self");
});

$(".homepage").find("a").mouseover(function() {
	var obj = this;
	$(obj).css("cursor", "pointer");
});

$(".treeview-menu").find("a").click(function() {
	var obj = this;
	if(!$(obj).attr("id")){
		return;
	}
	var url = rootPath + "/" + $(obj).attr("id");
	window.open(url, "_self");
});
$(".treeview-menu").find("a").mouseover(function() {
	var obj = this;
	$(obj).css("cursor", "pointer");
});

// 左侧三级菜单显示隐藏菜单
$(".treeview-menu li a").hover(function(event) {
	// 当鼠标移入父节点时
	var obj = this;
	$(".treeview-menu li a").each(function(index) {
		if (obj == this) {
			$(".menu-third-content:visible").hide();
			$(".treeview-menu li a").attr("class", "");

			$(".menu-third-content:eq(" + index + ")").show();
			$(this).attr("class", "active");
			return true;
		}
	});
}, function(event) {
	// 当鼠标移入子节点时
	$(".menu-third-content").hover(function(event) {

	}, function(event) {
		$(".menu-third-content").hide();
	});
});

// 登出
$("#out").click(function() {
	var url = rootPath + "/logout";
	window.open(url, "_self");
});

$(function() {
	window.Modal = function() {
		var reg = new RegExp("\\[([^\\[\\]]*?)\\]", 'igm');
		var alr = $("#ycf-alert");
		var ahtml = alr.html();
		var _alert = function(options) {
			alr.html(ahtml); // 复原
			alr.find('.ok').removeClass('btn-success').addClass('btn-primary');
			alr.find('.cancel').hide();
			_dialog(options);

			return {
				on : function(callback) {
					if (callback && callback instanceof Function) {
						alr.find('.ok').click(function() {
							callback(true)
						});
					}
				}
			};
		};
		var _confirm = function(options) {
			alr.html(ahtml); // 复原
			alr.find('.ok').removeClass('btn-primary').addClass('btn-success');
			alr.find('.cancel').show();
			_dialog(options);

			return {
				on : function(callback) {
					if (callback && callback instanceof Function) {
						alr.find('.ok').click(function() {
							callback(true)
						});
						alr.find('.cancel').click(function() {
							callback(false)
						});
					}
				}
			};
		};

		var _dialog = function(options) {
			var ops = {
				msg : "提示内容",
				title : "操作提示",
				btnok : "确定",
				btncl : "取消"
			};
			$.extend(ops, options);
			console.log(alr);
			var html = alr.html().replace(reg, function(node, key) {
				return {
					Title : ops.title,
					Message : ops.msg,
					BtnOk : ops.btnok,
					BtnCancel : ops.btncl
				}[key];
			});
			alr.html(html);
			alr.modal({
				width : 500,
				backdrop : 'static'
			});
		};
		return {
			alert : _alert,
			confirm : _confirm
		};

	}();
});

var arrfieldLength = {};
// 录入数据类别  资产:0 软件:1 （注：判断树形，如资产属性中树形索引为11，软件属性中树形索引为10）
var inputType = 0;
// 属性验证 tanglvshuang
function filedsValidate(data, formId) {
	// 初始化信息
	var fields = data;
	// 自定义分类类型信息Json化
	var fieldsJson = eval(fields);
	var fieldMap = {};
	var nameMaps = {};
	for ( var i = 0; i < fieldsJson.length; i++) {
		nameMaps[fieldsJson[i].name] = fieldsJson[i].desc + "不能为空";
		var validators = {}, checks = {}, regexp = {}, message = {}, length = {}, callback = {}, between = {};
		var regStr = "";
		// 是否为空较验
		if (fieldsJson[i].isNull != null && fieldsJson[i].isNull != ""
				&& fieldsJson[i].isNull == "1") {
			message = {
				message : fieldsJson[i].desc + "不能为空"
			}
			checks['notEmpty'] = message;
		}
		// 最大值最小值较验
		if ((fieldsJson[i].min != null && fieldsJson[i].min != "")
				|| (fieldsJson[i].max != null && fieldsJson[i].max != "")) {
			between = {
				min : fieldsJson[i].min,
				max : fieldsJson[i].max,
				message : fieldsJson[i].desc + "在" + fieldsJson[i].min + "和"
						+ fieldsJson[i].max + "之间"
			}
			checks['between'] = between;
		}
		// 文本框长度较验
		if (fieldsJson[i].length != null && fieldsJson[i].length != "") {
			// 所有文本框长度
			arrfieldLength[fieldsJson[i].name] = fieldsJson[i].length;
			callback = {
				message : fieldsJson[i].desc + "最大长度为" + fieldsJson[i].length,
				callback : function(value, validator, obj) {
					var flag = false;
					// 取当前文本框Id
					var id = obj[0]['id'];
					// 取当前文本框最大长度
					var maxLength = arrfieldLength[id];
					// 取当前文本框文本长度
					var length = getStringLength(value);
					if (length <= parseInt(maxLength)) {
						flag = true;
					}
					return flag;
				}
			}
			checks['callback'] = callback;
		}
		// 如果正则表达式不为空，则较验正则表达式
		if (fieldsJson[i].validate != null && fieldsJson[i].validate != "") {fields.replace(fieldsJson[i].name, "treeName"
				+ fieldsJson[i].name)
				// 替换  / 为 \ 
			var v = (fieldsJson[i].validate).replace(/\//g,"\\");
			regStr = "^" + v + "$\|^$";
		}
		;
		regexp = {
			regexp : regStr, // 避免系统解析的字符不一样 ，把|转义试 $\|^$
			message : '格式不正确'
		};
		checks['regexp'] = regexp;
		validators['validators'] = checks;
		// 如果是树形 (注：软件属性 ，树形索引为10)
		if (fieldsJson[i].index == "11" || (fieldsJson[i].index == "10" && inputType == 1)) {
			fields = fields.replace(fieldsJson[i].name, "treeName"
					+ fieldsJson[i].name);
			fieldMap["treeName" + fieldsJson[i].name] = validators;
		}
		// 如果是文件 视频 图片
		else if (fieldsJson[i].index == "08" || fieldsJson[i].index == "09"
				|| fieldsJson[i].index == "10") {
			fields = fields.replace(fieldsJson[i].name, "photoCover"
					+ fieldsJson[i].name);
			fieldMap["photoCover" + fieldsJson[i].name] = validators;
		} else {
			fieldMap[fieldsJson[i].name] = validators;
		}
	}
	$('#' + formId).bootstrapValidator({
		fields : fieldMap
	});
	// 选择日期控件时，较验日期
	$('#' + formId).find(".form_datetime").each(
			function() {
				var obj = $(this);
				var id = obj[0].id;
				obj.on('change blur focus', function(e) {
					$('#' + formId).data('bootstrapValidator').updateStatus(id,
							'NOT_VALIDATED', null).validateField(id);
				});
			});
	// 选中上传文本框时，较验
	$('#' + formId).find(".input-large").each(
			function() {
				var obj = $(this);
				var id = obj[0].id;
				obj.on('change blur focus', function(e) {
					$('#' + formId).data('bootstrapValidator').updateStatus(id,
							'NOT_VALIDATED', null).validateField(id);
				});
			});
}

// 取字符长度
function getStringLength(str) {
	var totalLength = 0;
	var list = str.split("");
	for ( var i = 0; i < list.length; i++) {
		var s = list[i];
		if (s.match(/[\u0000-\u00ff]/g)) { // 半角
			totalLength += 1;
		} else if (s.match(/[\u4e00-\u9fa5]/g)) { // 中文
			totalLength += 2;
		} else if (s.match(/[\uff00-\uffff]/g)) { // 全角
			totalLength += 2;
		}
	}
	return totalLength;
}

// 分页
function appendPagination(dataInfo, items_per_page, page_index,
		num_display_entries, num_edge_entries) {
	$("#Pagination").pagination(dataInfo.totalCount, {
		// 每页显示的条目数
		'items_per_page' : items_per_page,
		// 连续分页主体部分显示的分页条目数
		'num_display_entries' : num_display_entries,
		// 两侧显示的首尾分页的条目数
		'num_edge_entries' : num_edge_entries,
		'prev_text' : "上一页",
		'next_text' : "下一页",
		'callback' : pageselectCallback,
		'current_page' : page_index
	});
}

// 首页末页添加
function appendPageFirstEnd() {
	var enableFirst = false;
	var enableEnd = false;
	$("#Pagination").find("a").each(function() {
		if ($(this).hasClass("prev")) {
			enableFirst = true;
		}
		if ($(this).hasClass("next")) {
			enableEnd = true;
		}
	});
	$("#first").html('');
	$("#last").html('');
	$("#gotoPage").html('');

	var appendFirstHtml = "";
	var appendEndHtml = "";
	var appendGotoPageHtml = "";

	// 总页数
	var totalPage;
	totalPage = Math.ceil(parseInt(totalCount) / parseInt(items_per_page)) - 1;

	// if (enableFirst) {
	// appendFirstHtml = "<a href=\"#\" class=\"prev\"
	// onclick=\"gotoPageFirstEnd(0)\">首页</a>";
	// } else {
	// appendFirstHtml = "<span class=\"current prev\">首页</span>";
	// }
	// if (enableEnd) {
	// appendEndHtml = "<a href=\"#\" class=\"next\"
	// onclick=\"gotoPageFirstEnd("
	// + totalPage + ")\">末页</a>";
	// } else {
	// appendEndHtml = "<span class=\"current next\">末页</span>";
	// }

	// appendGotoPageHtml = "<input type=\"text\" id=\"gotoPageInput\"
	// class=\"form-control input-sm pagination-input-custom\"
	// style=\"width:40px\">";
	// appendGotoPageHtml = appendGotoPageHtml
	// + "<input type=\"button\" class=\"btn btn-default input-sm
	// pagination-btn-custom\" onclick=\"gotoPageByIndex()\" value=\"跳转\">";

	$("#first").append(appendFirstHtml);
	$("#last").append(appendEndHtml);
	$("#gotoPage").append(appendGotoPageHtml);
}

// 分页回调
function pageselectCallback(page_index, jq) {
	// 清除信息
	getDataList(page_index);
}

// 首页末页调用
function gotoPageFirstEnd(pageIndex) {
	// 清除信息
	$("#Pagination").html('');
	page_index = pageIndex;
	getDataList(page_index);
}

/**
 * 打开窗口
 * 
 * @param actionUrl
 * @param title
 * @param width
 * @param height
 * @param callBack
 *            回调函数
 * 
 */
function openWindowInCenter(actionUrl, title, width, height, callBack) {
	art.dialog.open(actionUrl, {
		title : title,
		width : width,
		height : height,
		resizable : false,
		lock : true,
		close : function() {
			if (callBack) {
				callBack();
			}
			;
		}
	});
}

// 加点 keyup
function addPoint(propId) {
	$("#" + propId).val($("#X" + propId).val() + "," + $("#Y" + propId).val());
}

// 属性 树形 类型
function initTree(id, probId) {
	// 唯一较验不通过，树形弹出框不显示
	if(!disabled){
		$('#myModal').modal('show');
		// 属性树形设定
		var settingValue = {
			data : {
				simpleData : {
					enable : true
				}
			},
			edit : {
				enable : false
			},
			callback : {
				onClick : valueOnClick
			}
		};
		// 树形点击事件
		function valueOnClick(event, treeId, treeNode) {
			var propTree = $.fn.zTree.getZTreeObj("valueTree");
			$('#treeName' + probId).val(propTree.getSelectedNodes()[0].name);
			$('#' + probId).val(propTree.getSelectedNodes()[0].id);
			$('#myModal').modal('hide');
			$('#treeName' + probId).focus();
		}
		;
		var date = new Date().getTime();// 根据不同 类型查询不同数据
		var urlStr = rootPath + "/infrastructure/zcdy/zcgl/getJson.json" + "?id="
				+ id + "&time=" + date;
		$.ajax({
			url : urlStr,
			type : 'POST',
			dataType : "json",
			success : function(data) {
				// 自定义分类类型信息Json化
				var resCpflJsonInfo = eval(data);
				var jsonDataTree = transData(resCpflJsonInfo, 'id', 'pid',
						'children');
				// 属性树形绑定
				$.fn.zTree.init($("#valueTree"), settingValue, jsonDataTree);
			}
		});
	}
}

// 树形模态框隐藏后，属性修改页面添加滚动条样式
$('#myModal').on('hidden.bs.modal', function () {
	$('body').addClass('modal-open');
})

// 属性修改模态框隐藏后，是否唯一较验 不启用
$('#myEditModal').on('hidden.bs.modal', function () {
	disabled = false;
})


// 弹出地图界面
function openMap(propId) {
	// 唯一较验不通过，树形弹出框不显示
	if(!disabled){
	var url = rootPath + "/gis/arcGisCoordinate.htm?date="
				+ (new Date()).getMilliseconds() + "&propId=" + propId;
		openWindowInCenter(url, "选择坐标", 1000, 500, null);
	}
}
// 弹出地图 返回调用
function myFormValidateRest(propId) {
	$("#" + propId).focus();
	$("#X" + propId).focus();
	$("#Y" + propId).focus();
}
var disabled = false;
// 属性验证 JS
function checkTempValueOnly(urlStr, propValue, propId, desc) {
	if (propValue != "") {
		var url = rootPath + urlStr;
		// 设定Params
		var params = {};
		params["propValue"] = propValue;
		params["propId"] = propId;
		// 执行方法调用
		$.post(url, params, function(callback) {
			if (callback.isSuccess == true) {
				if (callback.isOnly) {
					$('#' + propId).blur();
					Modal.alert({
						msg : desc + "已存在",
						title : '操作提示',
					}).on(function() {

						$('#' + propId).select();
					});
					$("#myForm input").attr("disabled", "disabled");
					$("#" + propId).attr("disabled", false);
					disabled = true;
				} else {
					// 属性修改页面添加滚动条样式
					$('body').addClass('modal-open');
					$("#myForm input").attr("disabled", false);
					disabled = false;
				}
			} else {
				Modal.alert({
					msg : '获取信息失败',
					title : '操作提示',
				});
			}
		});
	}
}

// 改变背景颜色 审核
function changeBackground(id,obj){
	flObj = obj;
	$("#" + id + " .col-lg-2").each(function(){
		if(obj.id == this.id){
			$("#" + this.id + "_a").addClass("active");
		}else{
			$("#" + this.id + "_a").removeClass("active");
		}
	});
}

// 展开收缩
function expandNode(e) {
	var zTree = $.fn.zTree.getZTreeObj(e.data.tree),
	type = e.data.type,
	nodes = zTree.getSelectedNodes();
	if (type.indexOf("All")<0 && nodes.length == 0) {
		Modal.alert(
                {
                    msg: '请先选择一个父节点',
                    title: '操作提示',
                });
	}

	if (type == "expandAll") {
		zTree.expandAll(true);
	} else if (type == "collapseAll") {
		zTree.expandAll(false);
	} else {
		var callbackFlag = $("#callbackTrigger").attr("checked");
		for (var i=0, l=nodes.length; i<l; i++) {
			zTree.setting.view.fontCss = {};
			if (type == "expand") {
				zTree.expandNode(nodes[i], true, null, null, callbackFlag);
			} else if (type == "collapse") {
				zTree.expandNode(nodes[i], false, null, null, callbackFlag);
			} else if (type == "toggle") {
				zTree.expandNode(nodes[i], null, null, null, callbackFlag);
			} else if (type == "expandSon") {
				zTree.expandNode(nodes[i], true, true, null, callbackFlag);
			} else if (type == "collapseSon") {
				zTree.expandNode(nodes[i], false, true, null, callbackFlag);
			}
		}
	}
}

// 个人信息
function getUserInfo(userName) {
	var url = rootPath + "/auth/user/user.htm?userName="+userName+"";
	window.open(url, "_self");
}

// 修改密码
function passWordChange(userName) {
	var url = rootPath + "/auth/user/editPassWord.htm?userName="+userName+"";
	window.open(url, "_self");
}