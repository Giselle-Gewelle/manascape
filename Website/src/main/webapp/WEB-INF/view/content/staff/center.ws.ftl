<#assign cssImports = [ "account/manage" ] />
<#include "../../inc/header.ftl" />

<h1>Staff Center</h1>

<@location>
	Staff Center
</@location>

<div class="scroll">
	<div class="content">
		<div id="managementContainer">
			<a href="${url('staff', 'userlist.ws')}" class="box double">
				<span class="bg"></span>
				
				<span class="inner">
					<span>User List</span>
					<span>View a list of registered user accounts.</span>
				</span>
			</a>
			
			<a href="${url('staff', 'newsarticle.ws')}" class="box double">
				<span class="bg"></span>
				
				<span class="inner">
					<span>Post News Article</span>
					<span>Publish a news article to the main page.</span>
				</span>
			</a>
		</div>
	</div>
</div>

<@location>
	Staff Center
</@location>

<#include "../../inc/footer.ftl" />