<#include "global.ftl" />

<h1>Delete News Article</h1>

<@location>
	<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; Delete News Article
</@location>

<div class="scroll">
	<div class="content">
		<h2>Delete News Article</h2>
		
		<hr />
		
		<#if article??>
			<#if deleted??>
				<p><strong>Your news article has been deleted successfully.</strong></p>
				<p><@a mod="staff" dest="center.ws">Click here to go to the Staff Center index.</@a></p>
			<#else>
				<p>Are you sure you wish to delete the news article titled [<strong>${article.title}</strong>]?</p>
				<p>Please be aware that this action <strong>CAN NOT</strong> be undone.</p>
				<p>Well I mean it can, since this is just a "soft" delete and all but like... It'd be a pain in the ass, and unsafe, to have to undo this in the production environment... 
					So like, don't do it unless you're absolutely sure it's no longer needed.</p>
				<p><strong>No, seriously.</strong> I'll break your face if you ask me to undo this in production.</p>
				<p>Faces will be broken.</p>
				<p>No amount of science will be able to fix how broken your face will be.</p>
				<p>Broken forever. <span title="Greater-Than Colon Left-Parenthesis">&gt;:(</span></p>
				
				<form method="post" action="${url('staff', 'newsdelete.ws')}">
					<input type="hidden" name="id" value="${article.id}" />
					
					<button name="inputYes" value="yes">Delete</button>
					&nbsp;
					<button name="inputNo" value="no">Cancel</button>
				</form>
			</#if>
		<#else>
			<p><strong>The article you were looking for was not found.</strong></p>
		</#if>
	</div>
</div>

<@location>
	<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; Delete News Article
</@location>

<#include "../../../inc/footer.ftl" />