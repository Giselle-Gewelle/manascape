<#assign cssImports = [ "staff/userlist" ] />
<#include "../../../../inc/header.ftl" />

<#if user??>
	<h1>Change Ban Status</h1>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; Change Ban Status
	</@location>
	
	<div class="scroll">
		<div id="userDetailList" class="content">
			
		</div>
	</div>
	
	<@location>
		<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; 
		<@a mod="staff" dest="userdetails.ws?id=${user.id}">User Details</@a> &gt; Change Ban Status
	</@location>
<#else>
	<#include "../userNotFound.ftl" />
</#if>

<#include "../../../../inc/footer.ftl" />