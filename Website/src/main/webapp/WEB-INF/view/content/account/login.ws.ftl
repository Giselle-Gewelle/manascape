<#assign cssImports = [ "account/login" ] />
<#include "../../inc/header.ftl" />

<h1>Secure Login</h1>

<@location>
	Login
</@location>

<div class="scroll">
	<div class="content">
		<#if loginAttempted??>
			<div class="center">
				<#if errorCode??>
					<#assign errorMessages = [
						"You must input a valid username.",
						"The username or password you have entered were incorrect.",
						"An unknown error has occurred."
					] />
					
					<p><strong>${errorMessages[errorCode]}</strong></p>
					<p><@a mod="account" dest="login.ws?mod=${toMod}&amp;dest=${toDest?html}">Click here to go back and try again.</@a>
				</#if>
			</div>
		<#else>
			<form id="loginPage" method="POST" action="${url('account', 'login.ws')}" autocomplete="off">
				<input type="hidden" name="mod" value="${toMod}" />
				<input type="hidden" name="dest" value="${toDest?html}" />
				
				<label for="loginPageUsername">Username:</label>
				<input type="text" id="loginPageUsername" name="loginPageUsername" maxlength="12" value="" />
				
				<div class="spacer"></div>
				
				<label for="loginPagePassword">Password:</label>
				<input type="password" id="loginPagePassword" name="loginPagePassword" maxlength="20" value="" />
				
				<div class="spacer"></div>
				
				<button name="submit">Submit Login</button>
			</form>
		</#if>
	</div>
</div>

<@location>
	Login
</@location>

<#include "../../inc/footer.ftl" />