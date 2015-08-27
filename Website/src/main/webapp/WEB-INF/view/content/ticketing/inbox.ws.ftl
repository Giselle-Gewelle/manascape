<#assign cssImports = [ "account/ticketing" ] />
<#include "../../inc/header.ftl" />

<h1>Your Messages from ${gameName}</h1>

<@location>
	<@a mod="account" dest="manage.ws">Account Management</@a> &gt; Message Center
</@location>

<div class="scroll">
	<div class="content">
		<#macro queueItem item>
			<div class="item">
				<span class="title">${item.title}</span>
				<span class="messages">${item.messageCount}</span>
				<span class="date">${item.lastMessageDate}</span>
				<span class="actions">
					<@a mod="ticketing" dest="view.ws?id=${item.threadId}" secure=true><img src="${url('main', 'resources/img/content/account/ticketing/view.png', true)}" alt="View" title="View" /></@a>
					<@a mod="ticketing" dest="view.ws?id=${item.threadId}" secure=true><img src="${url('main', 'resources/img/content/account/ticketing/delete.png', true)}" alt="Delete" title="Delete" /></@a>
				</span>
			</div>
		</#macro>
		
		<#macro noMessages><div class="noMessages">No conversations in this section.</div></#macro>
		
		<div class="section">
			<img class="header" src="${url('main', 'resources/img/content/account/ticketing/received.png')}" alt="Received" />
			
			<#if receivedList??>
				<#list receivedList as item>
					<@queueItem item />
				</#list>
			<#else>
				<@noMessages />
			</#if>
		</div>
		
		<div class="section">
			<img class="header" src="${url('main', 'resources/img/content/account/ticketing/sent.png')}" alt="Sent" />
			
			<#if receivedList??>
				<#list sentList as item>
					<@queueItem item />
				</#list>
			<#else>
				<@noMessages />
			</#if>
		</div>
		
		<div class="section">
			<img class="header" src="${url('main', 'resources/img/content/account/ticketing/read.png')}" alt="Read" />
			
			<#if receivedList??>
				<#list readList as item>
					<@queueItem item />
				</#list>
			<#else>
				<@noMessages />
			</#if>
		</div>
	</div>
</div>

<@location>
	<@a mod="account" dest="manage.ws">Account Management</@a> &gt; Message Center
</@location>

<#include "../../inc/footer.ftl" />