<#assign cssImports = [ "staff/userlist" ] />
<#include "../../../inc/header.ftl" />

<#if user??>
	<h1>View Login Attempts</h1>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; View Login Attempts
	</@location>
	
	<div class="scroll">
		<div id="userDetailList" class="content">
			<h2>Login Attempts for User: ${user.displayName} (${user.username})</h2>
			
			<#list loginAttempts.entries>
				<p>A total of ${loginAttempts.pageInfo.fullEntryCount} login attempts have been made against this user.</p>
				
				<#assign link="${url('staff', 'userloginattempts.ws?id=${user.id}')}" />
				<div id="pageNav">
					<#if loginAttempts.pageInfo.currentPage != 1>
						<a href="${link}&amp;page=1"><@img src="layout/scroll/first.png" /></a> 
						<a href="${link}&amp;page=${loginAttempts.pageInfo.currentPage - 1}"><@img src="layout/scroll/prev.png" /></a> 
						&nbsp;
					</#if>
					
					Page ${loginAttempts.pageInfo.currentPage} of ${loginAttempts.pageInfo.pageCount}
					
					<#if loginAttempts.pageInfo.currentPage < loginAttempts.pageInfo.pageCount>
						&nbsp; 
						<a href="${link}&amp;page=${loginAttempts.pageInfo.currentPage + 1}"><@img src="layout/scroll/next.png" /></a>
						<a href="${link}&amp;page=${loginAttempts.pageInfo.pageCount}"><@img src="layout/scroll/last.png" /></a> 
					</#if>
				</div>
				
				<table>
					<thead>
						<tr>
							<td>IP Address</td>
							<td>Date</td>
							<td>Successful?</td>
						</tr>
					</thead>
					
					<tbody>
						<#items as entry>
							<tr>
								<td>${entry.ip}</td>
								<td>${entry.date}</td>
								<td>${entry.successful?string("Yes", "No")}</td>
							</tr>
						</#items>
					</tbody>
				</table>
			<#else>
				<p>No login attempts have been made against this user.</p>
			</#list>
		</div>
	</div>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; View Login Attempts
	</@location>
<#else>
	<h1>User Not Found</h1>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		User Not Found
	</@location>
	
	<div class="scroll">
		<div id="userDetailList" class="content">
			<h2>User Not Found</h2>
			
			<p>The user you were looking for was not found.</p>
			<p><@a mod="staff" dest="userlist.ws">Click here to return to the user list.</@a></p>
		</div>
	</div>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		User Not Found
	</@location>
</#if>

<#include "../../../inc/footer.ftl" />