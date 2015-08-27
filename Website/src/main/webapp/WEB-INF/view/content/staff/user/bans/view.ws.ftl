<#assign cssImports = [ "staff/userlist" ] />
<#include "../../../../inc/header.ftl" />

<#if ban??>
	
	<h1>View Ban Details</h1>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${ban.userId}">User Details</@a> &gt; View Ban Details
	</@location>
	
	<div class="scroll">
		<div id="userDetailList" class="content">
			<h2>Ban Details for User: ${ban.bannedUser}</h2>
			
			<hr />
			
			<p><strong><#if ban.active>This ban is currently active.<br />
				<@a mod="staff" dest="userbansedit.ws?userId=${ban.userId}&amp;type=${ban.type}">Lift this ban.</@a><#else>This ban has been lifted.</#if></strong></p>
			<p>Ban Type: <strong>${ban.type}</strong></p>
			<p>This ban was applied by <strong>${ban.addedBy}</strong> on <strong>${ban.date}</strong> for the following reason:
				<span class="banReason">${ban.reason}</span></p>
				
			<#if ban.liftDate??>
				<hr />
				
				<p>This ban was lifted by <strong>${ban.liftor}</strong> on <strong>${ban.liftDate}</strong> for the following reason:
					<span class="banReason">${ban.liftReason}</span></p>
			</#if>
		</div>
	</div>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${ban.userId}">User Details</@a> &gt; View Ban Details
	</@location>
<#else>
	<h1>Ban Details Not Found</h1>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		Ban Details Not Found
	</@location>
	
	<div class="scroll">
		<div id="userDetailList" class="content">
			<h2>Ban Details Not Found</h2>
			<p>The ban/offense you were looking for was not found.</p>
			<p><@a mod="staff" dest="userlist.ws">Click here to return to the user list.</@a></p>
		</div>
	</div>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		Ban Details Not Found
	</@location>
</#if>

<#include "../../../../inc/footer.ftl" />