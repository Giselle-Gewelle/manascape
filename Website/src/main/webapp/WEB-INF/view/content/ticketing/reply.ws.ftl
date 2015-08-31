<#assign cssImports = [ "account/ticketing" ] />
<#assign jsImports = [ "Charlimiter" ] />
<#include "../../inc/header.ftl" />

<#if thread??>
	<h1>Reply to Message</h1>

	<@location>
		<@a mod="account" dest="manage.ws">Account Management</@a> &gt; <@a mod="ticketing" dest="inbox.ws">Message Center</@a> &gt; 
		<@a mod="ticketing" dest="view.ws?id=${thread.threadId}">View Message</@a> &gt; Reply to Message
	</@location>
	
	<#if thread.canReply>
		<div id="lastMessage">
			<div class="text"><strong>Replying to Message:</strong></div>
			
			<#assign sealType = "player" />
			<#if lastMessage.authorStaff>
				<#assign sealType = "mod" />
			</#if>
			
			<div class="message">
				<span class="userDetails">
					${lastMessage.author}
					<@img src="content/account/ticketing/${sealType}.png" />
				</span> 
				
				<span class="messageDetails">
					<span class="inner">
						<#if lastMessage.authorStaff>
							${replaceNewLines(lastMessage.message)}
						<#else>
							${replaceNewLines(lastMessage.message?html)}
						</#if>
					</span>
				</span>
				
				<span class="dateDetails">
					${lastMessage.date}
					
					<#if lastMessage.readOn??>
						<img src="${url('main', 'resources/img/content/account/ticketing/broken_seal_${sealType}.png')}" 
							alt="Read on ${lastMessage.readOn}."
							title="Read on ${lastMessage.readOn}." />
					<#else>
						<img src="${url('main', 'resources/img/content/account/ticketing/seal_${sealType}.png')}" 
							alt="This message has not been read."
							title="This message has not been read." />
					</#if>
				</span>
			</div>
		</div>
	</#if>
	
	<div class="scroll">
		<div class="content">
			<#if !thread.canReply>
				<h2>Not Allowed</h2>
				
				<p>A staff member has determined that a reply to this message is not necessary.</p>
				<p><@a mod="ticketing" dest="inbox.ws">Click here to return to your inbox.</@a></p>
			<#else>
				<#if successful??>
					<h2>Reply Sent</h2>
					
					<p>Your reply has been successfully sent.</p>
					<p><@a mod="ticketing" dest="inbox.ws">Click here to return to your inbox.</@a></p>
				<#else>
					<h2>Reply to Message</h2>
					
					<#if loginSession.user.staff>
						<#assign messageLength = 65535 />
					<#else>
						<#assign messageLength = 1024 />
					</#if>
					
					<#if errorCode??>
						<#assign errorMessages = [
							"You must input a valid message.",
							"Messages must be between 1 and ${messageLength} characters in length.",
							"An unknown error has occurred, please try again."
						] />
						
						<hr />
						
						<p><strong>${errorMessages[errorCode]}</strong></p>
						
						<hr />
					</#if>
					
					<form method="post" action="${url('ticketing', 'reply.ws')}" autocomplete="off">
						<input type="hidden" name="id" value="${thread.threadId}" />
						
						<div class="section">
							<label for="inputMessage">Your Message:</label>
							<textarea id="inputMessage" name="inputMessage" maxlength="${messageLength}"></textarea>
							<input type="hidden" id="inputMessageChars" value="${messageLength}" />
							<div id="inputMessageCharlimiter" class="charlimiter"></div>
						</div>
						
						<div class="section">
							<button id="inputSubmit" name="inputSubmit" value="submit">Submit</button>&nbsp;
							<button id="inputCancel" name="inputCancel" value="cancel">Cancel</button>
						</div>
					</form>
	
					<script type="text/javascript">
						new Charlimiter("inputMessage");
					</script>
				</#if>
			</#if>
		</div>
	</div>

	<@location>
		<@a mod="account" dest="manage.ws">Account Management</@a> &gt; <@a mod="ticketing" dest="inbox.ws">Message Center</@a> &gt; 
		<@a mod="ticketing" dest="view.ws?id=${thread.threadId}">View Message</@a> &gt; Reply to Message
	</@location>
<#else>
	<#include "notFound.ftl" />
</#if>

<#include "../../inc/footer.ftl" />