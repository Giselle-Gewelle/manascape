<#assign cssImports = [ "staff/userlist" ] />
<#include "../../../inc/header.ftl" />

<h1>User Details</h1>

<@location>
	<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; User Details
</@location>

<div class="scroll">
	<div id="userDetails" class="content">
		<#if user??>
			<h2>User Details: ${user.displayName} (${user.username})</h2>
			
			<#if currentlyLoggedIn??>
				<div class="alert">
					User has an active login session using the IP ${currentlyLoggedIn}
				</div>
			</#if>
			
			<#if failedLoginAttempts != 0>
				<div class="alert">
					<#if failedLoginAttempts = 1>
						1 of 5 recent login attempts made against this user failed.
					<#else>
						${failedLoginAttempts} in 5 recent login attempts made against this user failed. Is someone trying a brute force attack?
					</#if>
				</div>
			</#if>
			
			<h3>Basic Details</h3>
			<table>
				<thead>
					<tr>
						<td>User ID</td>
						<td>Username</td>
						<td>Latest IP</td>
						<td>Rights</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td>${user.id}</td>
						<td>${user.username}</td>
						<td>${user.currentIP}</td>
						<td>
							<#if user.staff>
								Staff
							<#elseif user.fmod && user.pmod>
								Global Mod
							<#elseif user.fmod>
								Forum Mod
							<#elseif user.pmod>
								Player Mod
							<#else>
								User
							</#if>
						</td>
					</tr>
				</tbody>
			</table>
			
			<h3>Account Creation</h3>
			<table>
				<thead>
					<tr>
						<td>Creation Date</td>
						<td>Creation IP</td>
						<td>Date of Birth</td>
						<td>Country</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td>${user.creationDate}</td>
						<td>${user.creationIP}</td>
						<td>${user.dob}</td>
						<td>${user.country}</td>
					</tr>
				</tbody>
			</table>
			
			<h3>Bans &amp; Offenses</h3>
			<table>
				<thead>
					<tr>
						<td title="Basic support includes: Bug reporting, feedback submission, etc... Account recovery and such will remain usable.">Basic Support Revoked?</td>
						<td>Forums Revoked?</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td>${user.supportDisabled?string("Yes", "No")}</td>
						<td>No</td>
					</tr>
				</tbody>
			</table>
			
			<h3>Recent Login Attempts</h3>
			<#list loginAttempts>
				<p class="start end">Showing the 5 most recent attempts, <@a mod="staff" dest="userloginattempts.ws?id=${user.id}">click here</@a> to view all login attempts made against this user.</p>
				<table>
					<thead>
						<tr>
							<td>IP Address</td>
							<td>Date</td>
							<td>Successful?</td>
						</tr>
					</thead>
					
					<tbody>
						<#items as attempt>
							<tr>
								<td>${attempt.ip}</td>
								<td>${attempt.date}</td>
								<td>${attempt.successful?string("Yes", "No")}</td>
							</tr>
						</#items>
					</tbody>
				</table>
			<#else>
				<p class="start">No login attempts have been made against this user.</p>
			</#list>
			
			<h3>Recent Login Sessions</h3>
			<#list loginSessions>
				<p class="start end">Showing the 5 most recent sessions, <@a mod="staff" dest="userloginsessions.ws?id=${user.id}">click here</@a> to view all login sessions opened against this user.</p>
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
						<#items as session>
							<tr>
								<td>${session.ip}</td>
								<td>${session.startDate}</td>
								<td>${session.endDate}</td>
								<td>${session.startMod}<br />${session.startDest}</td>
								<td>${session.currentMod}<br />${session.currentDest}</td>
								<td>${session.secure?string("Yes", "No")}</td>
								<td>${session.active?string("Yes", "No")}</td>
							</tr>
						</#items>
					</tbody>
				</table>
			<#else>
				<p class="start">No login sessions have been opened against this user.</p>
			</#list>
			
			<h3>Recent Password Changes</h3>
			<#list passwordChanges>
				<p class="start end">Showing the 5 most recent password changes, <@a mod="staff" dest="userpasswordchanges.ws?id=${user.id}">click here</@a> to view all password changes submitted against this user.</p>
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
				<p class="start">This user's password has never been changed.</p>
			</#list>
		<#else>
			<h2>User Not Found</h2>
			
			<p>The user you were looking for was not found.</p>
			<p><@a mod="staff" dest="userlist.ws">Click here to return to the user list.</@a></p>
		</#if>
	</div>
</div>

<@location>
	<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <@a mod="staff" dest="userlist.ws">User List</@a> &gt; User Details
</@location>

<#include "../../../inc/footer.ftl" />