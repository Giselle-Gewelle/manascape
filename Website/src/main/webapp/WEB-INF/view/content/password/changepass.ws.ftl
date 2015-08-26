<#assign cssImports = [ "account/changepass" ] />
<#include "../../inc/header.ftl" />

<h1>Change Your Password</h1>

<@location>
	<@a mod="account" dest="manage.ws">Account Management</@a> &gt; Change Your Password
</@location>

<div class="scroll">
	<div class="content">
		<#if (submitted?? && successful?? && successful)>
			<h2>Request Accepted</h2>
			<p>Your password change request has been accepted. You may now log into the game and website using your new password.</p>
			<p><@a mod="account" dest="manage.ws">Click here to return to the Account Management menu.</@a>
		<#else>
			<h2>Password Change Form</h2>
			
			<p>Use this form if you want to change the password you use for logging this account into ${gameName} and our website. If you do not wish to set a new password at this time, 
				please leave this page.</p>
			<p>Please be absolutely sure that you only ever enter or change your password at <strong>https://www.${formattedHostName}/</strong></p>
			<p>Please note that passwords <strong>are</strong> case-sensitive, must be <strong>5 to 20</strong> characters in length, and may only contain <strong>letters</strong> and <strong>numbers</strong>. 
				We recommend that you use a mixture of upper-case letters, lower-case letters, and numbers in your password to make it difficult for someone other than you to guess.</p>
				
			<#if submitted?? && errorCode??>
				<#assign errorMessages = [
					"Please fill out the all three password fields.",
					"An invalid password was submitted, please see the password requirements above before trying again.",
					"The values entered in the New Password and Confirm New Password boxes did not match, please make sure they match and try again.",
					"The Current Password entered did not match against our records, please try again.",
					"The New Password entered is the same as your Current Password, please choose a different password before trying again."
					"An unknown error has occurred, please try again."
				] />
				
				<hr />
				
				<p>
					<strong>There was an error with your submission:</strong><br />
					<span class="error">${errorMessages[errorCode]}</span>
				</p>
				
				<hr />
			</#if>
				
			<form id="passwordChangeForm" method="post" action"${url('password', 'changepass.ws')}" autocomplete="off">
				<div class="section">
					<label for="inputCurrentPassword">Current Password:</label>
					<input type="password" id="inputCurrentPassword" name="inputCurrentPassword" value="" maxlength="20" />
				</div>
				
				<div class="spacer"></div>
				
				<div class="section">
					<label for="inputPassword1">New Password:</label>
					<input type="password" id="inputPassword1" name="inputPassword1" value="" maxlength="20" />
				</div>
				
				<div class="section">
					<label for="inputPassword2">Confirm New Password:</label>
					<input type="password" id="inputPassword2" name="inputPassword2" value="" maxlength="20" />
				</div>
				
				<button id="inputSubmit" name="inputSubmit" value="submit">Submit</button>
			</form>
		</#if>
	</div>
</div>

<@location>
	<@a mod="account" dest="manage.ws">Account Management</@a> &gt; Change Your Password
</@location>

<#include "../../inc/footer.ftl" />