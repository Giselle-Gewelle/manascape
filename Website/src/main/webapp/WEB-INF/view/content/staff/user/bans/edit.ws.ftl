<#assign cssImports = [ "staff/userlist" ] />
<#include "../../../../inc/header.ftl" />

<#if user??>
	<h1>Change Ban Status</h1>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; Change Ban Status
	</@location>
	
	<div class="scroll">
		<div id="userDetailList" class="content">
			<h2>Change Ban Status for User: ${user.displayName} (${user.username})</h2>
			<p>Selected Ban Type: <strong>${banType}</strong></p>
			
			<#if submitted??>
				<#if errorCode??>
					<hr />
					
					<#assign errorMessages = [
						"You must input a valid reason.",
						"Reasons must be between 1 and 65535 characters in length.",
						"An unknown error has occurred."
					] />
					
					<p>${errorMessages[errorCode]}</p>
					<p><@a mod="staff" dest="userbansedit.ws?userId=${user.id}&amp;type=${banType}">Click here to go back and try again.</@a></p>
				<#else>
					<hr />
					
					<#if lifting>
						<p>Ban successfully lifted.</p>
					<#else>
						<p>Ban successfully applied.</p>
					</#if>
					
					<p><@a mod="staff" dest="userdetails.ws?id=${user.id}">Click here to return to the user details page.</@a></p>
				</#if>
			<#else>
				<#if lifting>
					<p><strong>You are about to lift this user's ban for the type listed above. Please ensure that the correct user is selected and that this action is justified.</strong></p>
					<p>This ban was applied by <strong>${ban.addedBy}</strong> on <strong>${ban.date}</strong> for the following reason:
						<span class="banReason">${ban.reason}</span></p>
				<#else>
					<p><strong>You are about to add a ban to this user's account for the type listed above. Please ensure that the correct user is selected and that this action is justified. 
						The following features will be taken away from the user with this ban:</strong></p>
					<ul>
						<#if banType == "forums">
							<li>Posting new forum threads.</li>
							<li>Replying to forum threads.</li>
							<li>Editing forum posts.</li>
							<li>Sending private messages to other users (via the website).</li>
						<#elseif banType == "support">
							<li>Submitting Customer Support feedback and/or queries.</li>
							<li>Voting in website polls.</li>
							<li>Submitting bug reports.</li>
						</#if>
					</ul>
					
					<p><strong>The following features will still be available to the user if a ban of this type is administered:</strong></p>
					<ul>
						<#if banType == "forums">
							<li>Viewing forum threads.</li>
							<li>Viewing private messages received from other users (via the website).</li>
						<#elseif banType == "support">
							<li>Submitting account recovery requests.</li>
							<li>Submitting offense appeals.</li>
						</#if>
					</ul>
				</#if>
				
				<form method="post" action="${url('staff', 'userbansedit.ws')}" autocomplete="off">
					<input type="hidden" name="userId" value="${user.id}" />
					<input type="hidden" name="type" value="${banType}" />
					
					<label for="inputReason">Please describe the reason you are <#if lifting>lifting this ban<#else>applying this ban</#if>:</label>
					<textarea id="inputReason" name="inputReason" maxlength="65535"></textarea>
					
					<br />
					
					<button name="inputSubmit" value="submit"><#if lifting>Lift Ban<#else>Apply Ban</#if></button>&nbsp;
					<button name="inputCancel" value="cancel">Cancel</button>
				</form>
			</#if>
		</div>
	</div>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; Change Ban Status
	</@location>
<#else>
	<#include "../userNotFound.ftl" />
</#if>

<#include "../../../../inc/footer.ftl" />