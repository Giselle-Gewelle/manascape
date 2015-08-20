<#assign cssImports = [ "account/login" ] />
<#include "../../inc/header.ftl" />

<h1>Secure Login</h1>

<@location>
	Login
</@location>

<div class="scroll">
	<div class="content">
		<form id="loginPage" method="POST" action="${url('account', 'login.ws')}" autocomplete="off">
			<!-- tomod and todest here -->
			
			<label for="loginPageUsername">Username:</label>
			<input type="text" id="loginPageUsername" name="loginPageUsername" maxlength="12" value="" />
			
			<div class="spacer"></div>
			
			<label for="loginPagePassword">Password:</label>
			<input type="password" id="loginPagePassword" name="loginPagePassword" maxlength="20" value="" />
			
			<div class="spacer"></div>
			
			<button name="submit">Submit Login</button>
		</form>
	</div>
</div>

<@location>
	Login
</@location>

<#include "../../inc/footer.ftl" />