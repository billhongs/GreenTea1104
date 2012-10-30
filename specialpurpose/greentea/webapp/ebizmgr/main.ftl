<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=7" />
<title>电商管理后台</title>

<link href="/images/dwz-ria/themes/purple/style.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="/images/dwz-ria/themes/css/core.css" rel="stylesheet" type="text/css" media="screen"/>
<link href="/images/dwz-ria/themes/css/print.css" rel="stylesheet" type="text/css" media="print"/>
<link href="/images/dwz-ria/uploadify/css/uploadify.css" rel="stylesheet" type="text/css" media="screen"/>
<!--[if IE]>
<link href="/images/dwz-ria/themes/css/ieHack.css" rel="stylesheet" type="text/css" media="screen"/>
<![endif]-->

<script src="/images/dwz-ria/js/speedup.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/jquery-1.7.2.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/jquery.cookie.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/jquery.validate.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/jquery.bgiframe.js" type="text/javascript"></script>
<script src="/images/dwz-ria/xheditor/xheditor-1.1.14-zh-cn.min.js" type="text/javascript"></script>
<script src="/images/dwz-ria/uploadify/scripts/swfobject.js" type="text/javascript"></script>
<script src="/images/dwz-ria/uploadify/scripts/jquery.uploadify.v2.1.0.js" type="text/javascript"></script>

<!-- svg图表  supports Firefox 3.0+, Safari 3.0+, Chrome 5.0+, Opera 9.5+ and Internet Explorer 6.0+ -->
<script type="text/javascript" src="/images/dwz-ria/chart/raphael.js"></script>
<script type="text/javascript" src="/images/dwz-ria/chart/g.raphael.js"></script>
<script type="text/javascript" src="/images/dwz-ria/chart/g.bar.js"></script>
<script type="text/javascript" src="/images/dwz-ria/chart/g.line.js"></script>
<script type="text/javascript" src="/images/dwz-ria/chart/g.pie.js"></script>
<script type="text/javascript" src="/images/dwz-ria/chart/g.dot.js"></script>

<script src="/images/dwz-ria/js/dwz.core.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.util.date.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.validate.method.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.regional.zh.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.barDrag.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.drag.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.tree.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.accordion.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.ui.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.theme.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.switchEnv.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.alertMsg.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.contextmenu.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.navTab.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.tab.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.resize.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.dialog.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.dialogDrag.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.sortDrag.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.cssTable.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.stable.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.taskBar.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.ajax.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.pagination.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.database.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.datepicker.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.effects.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.panel.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.checkbox.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.history.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.combox.js" type="text/javascript"></script>
<script src="/images/dwz-ria/js/dwz.print.js" type="text/javascript"></script>
<!--
<script src="bin/dwz.min.js" type="text/javascript"></script>
-->
<script src="/images/dwz-ria/js/dwz.regional.zh.js" type="text/javascript"></script>

<script type="text/javascript">
$(function(){
	DWZ.init("/images/dwz-ria/dwz.frag.xml", {
		loginUrl:"login_dialog.html", loginTitle:"登录",	// 弹出登录对话框
//		loginUrl:"login.html",	// 跳到登录页面
		statusCode:{ok:200, error:300, timeout:301}, //【可选】
		pageInfo:{pageNum:"pageNum", numPerPage:"numPerPage", orderField:"orderField", orderDirection:"orderDirection"}, //【可选】
		debug:false,	// 调试模式 【true|false】
		callback:function(){
			initEnv();
			$("#themeList").theme({themeBase:"themes"}); // themeBase 相对于index页面的主题base路径
		}
	});
});
function auditNotPass(){
	setTimeout(function(){$.pdialog.open("demo_page4.html", "addOpinionDia", "填写不通过原因", {width:"600", height:"250"});}, 10)
	return false;
}

function ebizOpenTab(tabid, url,data){
	navTab.openTab(tabid, url,data);
}
</script>
</head>

<body scroll="no">
	<div id="layout">
		<div id="header">
			<div class="headerNav">
				<a class="logo" href="http://www.wuyuan.org"><img src="/images/ebizmgr/green_tea3.jpg"></a>
				<ul class="nav">
					<li><a href="<@ofbizUrl>logout</@ofbizUrl>">退出</a></li>
				</ul>
			</div>

			<!-- navMenu -->
			
		</div>

		<div id="leftside">
			<div id="sidebar_s">
				<div class="collapse">
					<div class="toggleCollapse"><div></div></div>
				</div>
			</div>
			<div id="sidebar">
				<div class="toggleCollapse"><h2>管理后台</h2><div>收缩</div></div>

				<div class="accordion" fillSpace="sidebar">
					<div class="accordionHeader">
						<h2><span>Folder</span>产品管理</h2>
					</div>
					<div class="accordionContent">
						<ul class="tree treeFolder">
							<li><a>产品分类</a>
								<ul>
									<li><a href="<@ofbizUrl>FindCategory</@ofbizUrl>" target="navTab"  rel="FindCategory" external="true">分类查询</a></li>
								</ul>
							</li>
							
							<li><a>产品</a>
								<ul>
									<li><a href="<@ofbizUrl>FindProduct</@ofbizUrl>" target="navTab" rel="FindProduct" external="true">产品查询</a></li>
								</ul>
							</li>
									

						</ul>
					</div>
					<div class="accordionHeader">
						<h2><span>Folder</span>订单管理</h2>
					</div>
					<div class="accordionContent">
						
					</div>
				</div>
			</div>
		</div>
		<div id="container">
			<div id="navTab" class="tabsPage">
				<div class="tabsPageHeader">
					<div class="tabsPageHeaderContent"><!-- 显示左右控制时添加 class="tabsPageHeaderMargin" -->
						<ul class="navTab-tab">
							<li tabid="main" class="main"><a href="javascript:;"><span><span class="home_icon">我的主页</span></span></a></li>
						</ul>
					</div>
					<div class="tabsLeft">left</div><!-- 禁用只需要添加一个样式 class="tabsLeft tabsLeftDisabled" -->
					<div class="tabsRight">right</div><!-- 禁用只需要添加一个样式 class="tabsRight tabsRightDisabled" -->
					<div class="tabsMore">more</div>
				</div>
				<ul class="tabsMoreList">
					<li><a href="javascript:;">我的主页</a></li>
				</ul>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div class="page unitBox">
						热烈欢迎使用GreenTea电商管理平台
						
					</div>
					
				</div>
			</div>
		</div>

	</div>

	<div id="footer">Copyright &copy; 2012-2022 www.wuyuan.org</div>



</body>
</html>