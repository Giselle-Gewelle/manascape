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
			
			function openLoginDialog() {
				$("#dialogOverlay").css("display", "block");
				$("#loginDialog").css("display", "block");
				$("#loginPageUsername").focus();
			}
			
			function closeLoginDialog() {
				$("#loginDialog").css("display", "none");
				$("#dialogOverlay").css("display", "none");
			}
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
		
		<#if style??>
			<style type="text/css">
				<@style />
			</style>
		</#if>
		
		<link rel="icon" type="image/x-icon" href="${url('main', 'favicon.ico')}" />
		<link rel="shortcut icon" type="image/x-icon" href="${url('main', 'favicon.ico')}" />
	</head>
	
	<body<#if angular??> ${angular}</#if>>
		<div id="dialogOverlay"></div>
		
		<#if !(hideSessionButton??)>
			<div id="loginDialog" class="dialog">
				<div class="header">
					<div class="close" onclick="closeLoginDialog()"></div>
					
					<h2>Secure Login</h2>
				</div>
				
				<div class="content">
					<form method="POST" action="${url('account', 'login.ws')}" autocomplete="off" novalidate>
						<input type="hidden" name="mod" value="${currentMod}" />
						<input type="hidden" name="dest" value="${currentFullDest?html}" />
						
						<div class="section">
							<label for="loginPageUsername">Username:</label>
							<input type="text" id="loginPageUsername" name="loginPageUsername" maxlength="12" />
						</div>
						
						<div class="section">
							<label for="loginPagePassword">Password:</label>
							<input type="password" id="loginPagePassword" name="loginPagePassword" maxlength="20" />
						</div>
						
						<button name="loginDialogSubmit">Submit Secure Login</button>
						
						<div class="box">
							<h3>Don't have an account?</h3>
							
							<p class="start end">Don't have a ${gameName} account yet? <@a mod="create" dest="index.ws">Click here</@a> to visit our account creation page and get started!</p>
						</div>
					</form>
				</div>
			</div>
		</#if>
		
		<#if prepend??>
			<@prepend />
		</#if>
		
		<div id="noJS">
			JavaScript must be enabled to utilize many features of this website.
		</div>
		
		<div id="content">
			<div id="nav">
				<#if loginSession.loggedIn>
					<div>
						Logged in as <span class="orange">${loginSession.user.displayName}</span>
					</div>
				<#else>
					<div>
						Not Logged In
					</div>
				</#if>
				
				<ul>
					<li><@a mod="main" dest="title.ws">Home</@a></li>
					
					<#--<li><a href="">Play</a></li>-->
					
					<li><@a mod="account" dest="manage.ws">Account</@a>
						<ul>
							<li><@a mod="create" dest="index.ws">Create an Account</@a></li>
							<li><@a mod="password" dest="changepass.ws">Change Your Password</@a></li>
						</ul>
					</li>
					
					<li><a href="">Community</a>
						<ul>
							<li><a href="">Forums</a></li>
							<#--<li><a href="">Hiscores</a></li>
							<li><a href="">Polls</a></li>-->
						</ul>
					</li>
					
					<#--<li><a href="">Help</a></li>-->
					
					<#if loginSession.loggedIn>
						<#if loginSession.user.staff>
							<li><@a mod="staff" dest="center.ws">Staff</@a>
								<ul>
									<li><@a mod="staff" dest="newsarticle.ws">Post News Article</@a></li>
									<li><@a mod="staff" dest="userlist.ws">User List</@a></li>
								</ul>
							</li>
						</#if>
					</#if>
					
					<#if !(hideSessionButton??)>
						<#if loginSession.loggedIn>
							<li><a class="logout" href="${url('account', 'logout.ws?mod=${currentMod}&amp;dest=${currentFullDest?html}')}">Logout</a></li>
						<#else>
							<#--<li><@a mod="account" dest="login.ws?mod=${currentMod}&amp;dest=${currentFullDest?html}">Login</@a></li>-->
							<li><span class="login" onclick="openLoginDialog()">Login</span></li>
						</#if>
					</#if>
				</ul>
			</div>