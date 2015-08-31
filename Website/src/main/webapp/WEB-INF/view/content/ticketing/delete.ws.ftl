<#assign cssImports = [ "account/ticketing" ] />
<#include "../../inc/header.ftl" />

<#if thread??>
	<h1>Delete Message</h1>

	<@location>
		<@a mod="account" dest="manage.ws">Account Management</@a> &gt; <@a mod="ticketing" dest="inbox.ws">Message Center</@a> &gt; 
		<@a mod="ticketing" dest="view.ws?id=${thread.threadId}">View Message</@a> &gt; Delete Message
	</@location>
	
	<div id="lastMessage">
		<div class="text"><strong>Selected Message:</strong></div>
		
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
	
	<div class="scroll">
		<div class="content">
			<h2>Please Confirm</h2>
			
			<form method="post" action="${url('ticketing', 'delete.ws')}">
				<input type="hidden" name="id=" value="${thread.threadId}" />
				
				<p>Are you sure you wish to delete this message?</p>
				<p><strong>Please be aware that this action can never be undone.</strong></p>
				
				<button id="inputSubmit" name="inputSubmit" value="submit">Delete</button>&nbsp;
				<button id="inputCancel" name="inputCancel" value="cancel">Cancel</button>
			</form>
		</div>
	</div>

	<@location>
		<@a mod="account" dest="manage.ws">Account Management</@a> &gt; <@a mod="ticketing" dest="inbox.ws">Message Center</@a> &gt; 
		<@a mod="ticketing" dest="view.ws?id=${thread.threadId}">View Message</@a> &gt; Delete Message
	</@location>
<#else>
	<#include "notFound.ftl" />
</#if>

<#include "../../inc/footer.ftl" />