<#assign cssImports = [ "account/manage" ] />
<#include "../../inc/header.ftl" />

<h1>Account Management</h1>

<@location>
	Account Management
</@location>

<div class="scroll">
	<div class="content">
		<div id="managementContainer">
			<a href="${url('create', 'index.ws')}" class="box double">
				<span class="bg"></span>
				
				<span class="inner">
					<span>Create an Account</span>
					<span>Don't yet have an account? Click here to start your greatest adventure!</span>
				</span>
			</a>
			
			<a href="${url('password', 'changepass.ws')}" class="box double">
				<span class="bg"></span>
				
				<span class="inner">
					<span>Change Your Password</span>
					<span>Change the password you use to log into the game and website.</span>
				</span>
			</a>
		</div>
	</div>
</div>

<@location>
	Account Management
</@location>

<#include "../../inc/footer.ftl" />