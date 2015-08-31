<#assign cssImports = [ "account/ticketing" ] />
<#assign jsImports = [ "Charlimiter" ] />
<#include "../../inc/header.ftl" />

<h1>Submit Support Query</h1>

<@location>
	<@a mod="account" dest="manage.ws">Account Management</@a> &gt; <@a mod="ticketing" dest="inbox.ws">Message Center</@a> &gt; 
	Submit Support Query
</@location>

<div class="scroll">
	<div class="content">
		<#if supportDisabled??>
			<h2>Customer Support Revoked</h2>
			
			<p>We're sorry, but the Customer Support privileges for this account have been revoked.</p>
			<p>For more information, please visit your <@a mod="offense" dest="history.ws">Offense Center</@a>.</p>
		<#else>
			<#if flooding??>
				<h2>Activity Limit Reached</h2>
				
				<p>You may only submit one Support Query every 15 minutes, please try again later.</p>
			<#else>
				<#if submissionError??>
					<h2>Error</h2>
					
					<p>An unknown error has occurred.</p>
					<p><@a mod="ticketing" dest="query.ws">Click here to go back and try again.</@a></p>
				<#elseif successful??>
					<h2>Message Sent</h2>
					
					<p>Your message has been successfully sent.</p>
					<p>If deemed necessary, you will receive a reply from our staff shortly regarding your query.</p>
					<p><@a mod="ticketing" dest="inbox.ws">Click here to visit your Message Center.</@a></p>
				<#else>
					<h2>Submit Support Query</h2>
					
					<form method="post" action="${url('ticketing', 'query.ws')}" autocomplete="off">
						<div class="section">
							<#if queryTypeError??>
								<div class="label error">Please select a valid Query Type:</div>
							<#else>
								<div class="label">Query Type:</div>
							</#if>
							
							<select id="inputType" name="inputType">
								<option value=""></option>
								<option value="privacy"<#if queryType?? && queryType = "privacy"> selected="selected"</#if>>Privacy Concern</option>
								<option value="complaint"<#if queryType?? && queryType = "complaint"> selected="selected"</#if>>Complaint</option>
								<option value="feedback"<#if queryType?? && queryType = "feedback"> selected="selected"</#if>>General Feedback</option>
								<option value="other"<#if queryType?? && queryType = "other"> selected="selected"</#if>>Other</option>
							</select>
						</div>
						
						<div class="section">
							<#if messageError??>
								<#assign errorMessages = [
									"Please input a valid message",
									"Messages must be between 1 and 1024 characters in length"
								] />
								
								<label for="inputMessage" class="error">${errorMessages[messageError]}:</label>
							<#else>
								<label for="inputMessage">Your Message:</label>
							</#if>
							
							<textarea id="inputMessage" name="inputMessage" maxlength="1024"><#if message??>${message?html}</#if></textarea>
							<input type="hidden" id="inputMessageChars" value="1024" />
							<div id="inputMessageCharlimiter" class="charlimiter"></div>
						</div>
						
						<div class="section">
							<button id="inputSubmit" name="inputSubmit" value="submit">Submit</button>
						</div>
					</form>
		
					<script type="text/javascript">
						new Charlimiter("inputMessage");
					</script>
				</#if>
			</#if>
		</#if>
	</div>
</div>

<@location>
	<@a mod="account" dest="manage.ws">Account Management</@a> &gt; <@a mod="ticketing" dest="inbox.ws">Message Center</@a> &gt; 
	Submit Support Query
</@location>

<#include "../../inc/footer.ftl" />