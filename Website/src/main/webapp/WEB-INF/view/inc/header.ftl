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
		
		<div id="content">
			<div id="nav">
				<div>
					Not Logged In
				</div>
				
				<ul>
					<li><a href="">Home</a></li>
					<li><a href="">Play</a></li>
					<li><a href="">Account</a></li>
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