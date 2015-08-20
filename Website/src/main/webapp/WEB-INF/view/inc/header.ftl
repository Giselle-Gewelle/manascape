<#include "global.ftl" />
<html lang="en-US">
	<head>
		<meta charset="UTF-8" />
		
		<title>${gameName}</title>
		
		<link rel="stylesheet" type="text/css" href="${url('main', 'resources/css/layout.css?rt=${rsTime}')}" />
		
		<#if script??>
			<script type="text/javascript">
				<@script />
			</script>
		</#if>
		
		<script type="text/javascript" src="${url('main', 'resources/js/lib/jquery-2.1.4.min.js?rt=${rsTime}')}"></script>
		
		<#if angular??>
			<script type="text/javascript" src="${url('main', 'resources/js/lib/angular.min.js?rt=${rsTime}')}"></script>
		</#if>
		
		<script type="text/javascript">
			$(document).ready(function() {
				$("#noJS").css("display", "none");
				$("#content").css("display", "block");
			});
		</script>
		
		<#if header??>
			<@header />
		</#if>
		
		<#if cssImports??>
			<#list cssImports as cssImport>
				<link rel="stylesheet" type="text/css" href="${url('main', 'resources/css/${cssImport}.css?rt=${rsTime}')}" />
			</#list>
		</#if>
		
		<#if jsImports??>
			<#list jsImports as jsImport>
				<script type="text/javascript" src="${url('main', 'resources/js/${jsImport}.js?rt=${rsTime}')}"></script>
			</#list>
		</#if>
		
		<link rel="icon" type="image/x-icon" href="${url('main', 'favicon.ico')}" />
		<link rel="shortcut icon" type="image/x-icon" href="${url('main', 'favicon.ico')}" />
	</head>
	
	<body<#if angular??> ${angular}</#if>>
		<#if prepend??>
			<@prepend />
		</#if>
		
		<div id="noJS">
			JavaScript must be enabled to utilize many features of this website.
		</div>
		
		<div id="content">
			<div id="nav">
				<div>
					Not Logged In
				</div>
				
				<ul>
					<li><a href="">Home</a></li>
					<li><a href="">Play</a></li>
					<li><a href="">Account</a>
						<ul>
							<li><@a mod="create" dest="index.ws">Create an Account</@a></li>
						</ul>
					</li>
					<li><a href="">Community</a>
						<ul>
							<li><a href="">Forums</a></li>
							<li><a href="">Hiscores</a></li>
							<li><a href="">Polls</a></li>
						</ul>
					</li>
					<li><a href="">Help</a></li>
					<li><a href="">Login</a></li>
				</ul>
			</div>