<#assign cssImports = [ "staff/userlist" ] />
<#include "../../../inc/header.ftl" />

<#if user??>
	<h1>View Login Sessions</h1>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; View Login Sessions
	</@location>
	
	<div class="scroll">
		<div id="userDetailList" class="content">
			<h2>Login Sessions for User: ${user.displayName} (${user.username})</h2>
			
			<#list loginSessions.entries>
				<p>A total of ${loginSessions.pageInfo.fullEntryCount} login sessions have opened against this user.</p>
				
				<#assign link="${url('staff', 'userloginsessions.ws?id=${user.id}')}" />
				<div id="pageNav">
					<#if loginSessions.pageInfo.currentPage != 1>
						<a href="${link}&amp;page=1"><@img src="layout/scroll/first.png" /></a> 
						<a href="${link}&amp;page=${loginSessions.pageInfo.currentPage - 1}"><@img src="layout/scroll/prev.png" /></a> 
						&nbsp;
					</#if>
					
					Page ${loginSessions.pageInfo.currentPage} of ${loginSessions.pageInfo.pageCount}
					
					<#if loginSessions.pageInfo.currentPage < loginSessions.pageInfo.pageCount>
						&nbsp; 
						<a href="${link}&amp;page=${loginSessions.pageInfo.currentPage + 1}"><@img src="layout/scroll/next.png" /></a>
						<a href="${link}&amp;page=${loginSessions.pageInfo.pageCount}"><@img src="layout/scroll/last.png" /></a> 
					</#if>
				</div>
				
				<table>
					<thead>
						<tr>
							<td>IP Address</td>
							<td>Start Date</td>
							<td>End Date</td>
							<td>Start Location</td>
							<td>Last Location</td>
							<td>Secure</td>
							<td>Active</td>
						</tr>
					</thead>
					
					<tbody>
						<#items as entry>
							<tr>
								<td>${entry.ip}</td>
								<td>${entry.startDate}</td>
								<td>${entry.endDate}</td>
								<td>${entry.startMod}<br />${entry.startDest}</td>
								<td>${entry.currentMod}<br />${entry.currentDest}</td>
								<td>${entry.secure?string("Yes", "No")}</td>
								<td>${entry.active?string("Yes", "No")}</td>
							</tr>
						</#items>
					</tbody>
				</table>
			<#else>
				<p>No login sessions have been opened against this user.</p>
			</#list>
		</div>
	</div>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; View Login Sessions
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