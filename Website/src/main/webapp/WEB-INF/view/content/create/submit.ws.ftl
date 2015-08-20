<#assign cssImports = [ "account/create" ] />
<#include "../../inc/header.ftl" />

<h1>Create an Account</h1>

<@location>
	Create an Account
</@location>

<div class="scroll">
	<div class="content">
		<div id="progressBar">
			<span class="complete">Age and Location</span>
			<span class="complete">Desired Username</span>
			<span class="complete">Terms of Use</span>
			<span class="complete">Desired Password</span>
			<span class="active">Finish</span>
		</div>
		
		<div id="createContent">
			<#if error??>
				<h2>An Error Has Occurred</h2>
				
				<p>An error has occurred while attempting to create your account.</p>
				
				<p>Please <@a mod="create" dest="index.ws">go back</@a> and try again.</p>
			<#elseif flooding??>
				<h2>Activity Limit Reached</h2>
				
				<p>You have created too many new accounts lately, please try again later.</p>
			<#else>
				<h2>Account Creation Complete</h2>
				
				<p>Your account has been successfully created with the username and password you have chosen.</p>
				
				<p><@a mod="main" dest="title.ws">Click here to return to the home page.</@a>
			</#if>
		</div>
	</div>
</div>

<@location>
	Create an Account
</@location>

<#include "../../inc/footer.ftl" />