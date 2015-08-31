<#assign cssImports = [ "account/ticketing" ] />
<#include "../../inc/header.ftl" />

<#if thread??>
	<h1>View Message</h1>

	<@location>
		<@a mod="account" dest="manage.ws">Account Management</@a> &gt; <@a mod="ticketing" dest="inbox.ws">Message Center</@a> &gt; 
		View Message
	</@location>

	<#list thread.messageList as message>
		<#assign sealType = "player" />
		<#if message.authorStaff>
			<#assign sealType = "mod" />
		</#if>
		
		<div class="message">
			<span class="userDetails">
				${message.author}
				<@img src="content/account/ticketing/${sealType}.png" />
			</span> 
			
			<span class="messageDetails">
				<span class="inner">
					<#if message.authorStaff>
						${replaceNewLines(message.message)}
					<#else>
						${replaceNewLines(message.message?html)}
					</#if>
				</span>
			</span>
			
			<span class="dateDetails">
				${message.date}
				
				<#if message.readOn??>
					<img src="${url('main', 'resources/img/content/account/ticketing/broken_seal_${sealType}.png')}" 
						alt="Read on ${message.readOn}."
						title="Read on ${message.readOn}." />
				<#else>
					<img src="${url('main', 'resources/img/content/account/ticketing/seal_${sealType}.png')}" 
						alt="This message has not been read."
						title="This message has not been read." />
				</#if>
			</span>
		</div>
	</#list>
	
	<div class="center">
		<p>
			<#if thread.canReply>
				<@a mod="ticketing" dest="reply.ws?id=${thread.threadId}"><@img src="content/account/ticketing/navreply.png" /> Reply to Message</@a>&nbsp;&nbsp;&nbsp;
			</#if>
			
			<@a mod="ticketing" dest="delete.ws?id=${thread.threadId}"><@img src="content/account/ticketing/navdelete.png" /> Delete Message</@a>
		</p>
	</div>
	
	<@location>
		<@a mod="account" dest="manage.ws">Account Management</@a> &gt; <@a mod="ticketing" dest="inbox.ws">Message Center</@a> &gt; 
		View Message
	</@location>
<#else>
	<#include "notFound.ftl" />
</#if>

<#include "../../inc/footer.ftl" />