<#assign cssImports = [ "account/ticketing" ] />
<#include "../../inc/header.ftl" />

<h1>Message Center</h1>

<@location>
	<@a mod="account" dest="manage.ws">Account Management</@a> &gt; Message Center
</@location>

<br />

<div id="inbox">
	<#macro queueItem item>
		<div class="item">
			<span class="title">${item.title}</span>
			<span class="messages">${item.messageCount}</span>
			<span class="date">${item.lastMessageDate}</span>
			<span class="actions">
				<@a mod="ticketing" dest="view.ws?id=${item.threadId}"><img src="${url('main', 'resources/img/content/account/ticketing/view.png')}" alt="View" title="View Ticket" /></@a>
				<@a mod="ticketing" dest="delete.ws?id=${item.threadId}"><img src="${url('main', 'resources/img/content/account/ticketing/delete.png')}" alt="Delete" title="Delete Ticket" /></@a>
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
		
		<#if sentList??>
			<#list sentList as item>
				<@queueItem item />
			</#list>
		<#else>
			<@noMessages />
		</#if>
	</div>
	
	<div class="section">
		<img class="header" src="${url('main', 'resources/img/content/account/ticketing/read.png')}" alt="Read" />
		
		<#if readList??>
			<#list readList as item>
				<@queueItem item />
			</#list>
		<#else>
			<@noMessages />
		</#if>
	</div>
</div>

<@location>
	<@a mod="account" dest="manage.ws">Account Management</@a> &gt; Message Center
</@location>

<#include "../../inc/footer.ftl" />