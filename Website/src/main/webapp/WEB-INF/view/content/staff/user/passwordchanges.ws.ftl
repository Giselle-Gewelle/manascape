<#assign cssImports = [ "staff/userlist" ] />
<#include "../../../inc/header.ftl" />

<#if user??>
	<h1>View Password Changes</h1>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; View Password Changes
	</@location>
	
	<div class="scroll">
		<div id="userDetailList" class="content">
			<h2>Password Changes for User: ${user.displayName} (${user.username})</h2>
			
			<#list passwordChanges.entries>
				<p>User's latest IP Address: <strong>${user.currentIP}</strong></p>
				
				<p>A total of ${passwordChanges.pageInfo.fullEntryCount} password changes have been submitted against this user.</p>
				
				<#assign link="${url('staff', 'userpasswordchanges.ws?id=${user.id}')}" />
				<div id="pageNav">
					<#if passwordChanges.pageInfo.currentPage != 1>
						<a href="${link}&amp;page=1"><@img src="layout/scroll/first.png" /></a> 
						<a href="${link}&amp;page=${passwordChanges.pageInfo.currentPage - 1}"><@img src="layout/scroll/prev.png" /></a> 
						&nbsp;
					</#if>
					
					Page ${passwordChanges.pageInfo.currentPage} of ${passwordChanges.pageInfo.pageCount}
					
					<#if passwordChanges.pageInfo.currentPage < passwordChanges.pageInfo.pageCount>
						&nbsp; 
						<a href="${link}&amp;page=${passwordChanges.pageInfo.currentPage + 1}"><@img src="layout/scroll/next.png" /></a>
						<a href="${link}&amp;page=${passwordChanges.pageInfo.pageCount}"><@img src="layout/scroll/last.png" /></a> 
					</#if>
				</div>
				
				<table>
					<thead>
						<tr>
							<td>IP Address</td>
							<td>Date</td>
						</tr>
					</thead>
					
					<tbody>
						<#items as entry>
							<tr>
								<td>${entry.ip}</td>
								<td>${entry.date}</td>
							</tr>
						</#items>
					</tbody>
				</table>
			<#else>
				<p>No password changes have been made against this user.</p>
			</#list>
		</div>
	</div>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; View Password Changes
	</@location>
<#else>
	<#include "userNotFound.ftl" />
</#if>

<#include "../../../inc/footer.ftl" />