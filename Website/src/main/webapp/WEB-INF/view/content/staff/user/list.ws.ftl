<#assign cssImports = [ "staff/userlist" ] />
<#include "../../../inc/header.ftl" />

<h1>User List</h1>

<@location>
	<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; User List
</@location>

<div class="scroll">
	<div class="content">
		<h2>Search</h2>
		
		<br />
		
		<form method="post" action="${url('staff', 'userlist.ws')}">
			<div class="inlineSection">
				<label for="usernameSearch">Username:</label>
			</div>
			
			<div class="inlineSection">
				<label for="ipSearch">IP:</label>
			</div>
			
			<br />
			
			<div class="inlineSection">
				<input type="text" id="usernameSearch" name="usernameSearch" maxlength="12" value="${userList.usernameSearch}" />
			</div>
			
			<div class="inlineSection">
				<input type="text" id="ipSearch" name="ipSearch" maxlength="50" value="${userList.ipSearch}" />
			</div>
			
			<div class="inlineSection">
				<button name="searchButton">Search</button>
			</div>
		</form>
		
		<hr />
		
		<h2>User List</h2>
		
		<#assign link="${url('staff', 'userlist.ws?usernameSearch=${userList.usernameSearch}&amp;ipSearch=${userList.ipSearch}')}" />
		<div id="pageNav">
			<#if userList.currentPage != 1>
				<a href="${link}&amp;page=1">&lt;&lt;</a> 
				<a href="${link}&amp;page=${userList.currentPage - 1}">&lt;</a> 
				&nbsp;
			</#if>
			
			Page ${userList.currentPage} of ${userList.pageCount}
			
			<#if userList.currentPage < userList.pageCount>
				&nbsp; 
				<a href="${link}&amp;page=${userList.currentPage + 1}">&gt;</a>
				<a href="${link}&amp;page=${userList.pageCount}">&gt;&gt;</a> 
			</#if>
		</div>
		
		<#list userList.userList>
			<table>
				<thead>
					<tr>
						<td>User ID</td>
						<td>Username</td>
						<td>Creation Date</td>
						<td>Creation IP</td>
						<td>Latest IP</td>
						<td>Actions</td>
					</tr>
				</thead>
				
				<tbody>
					<#items as user>
						<tr>
							<td>${user.id}</td>
							<td>${user.displayName}</td>
							<td>${user.creationDate}</td>
							<td>${user.creationIP}</td>
							<td>${user.currentIP}</td>
							<td>
								<@a mod="staff" dest="userdetails.ws?id=${user.id}">Details</@a>
							</td>
						</tr>
					</#items>
				</tbody>
			</table>
		<#else>
			<p>No users were found for the given search criteria.</p>
		</#list>
	</div>
</div>

<@location>
	<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; User List
</@location>

<#include "../../../inc/footer.ftl" />