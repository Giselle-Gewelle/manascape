<#assign jsImports = [ "Charlimiter" ] />
<#include "global.ftl" />

<h1><#if update??>Edit<#else>Post</#if> News Article</h1>

<@location>
	<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <#if update??>Edit<#else>Post</#if> News Article
</@location>

<div class="scroll">
	<div class="content">
		<h2><#if update??>Edit<#else>Post</#if> News Article</h2>
		
		<hr />
		
		<#if postError??>
			<p><strong>An error has occurred while attempting to submit your article.</strong></p>
			<p><@a mod="staff" dest="newsarticle.ws">Click here to go back and try again.</@a></p>
		<#elseif newArticleId??>
			<#if update??>
				<p><strong>Your news article has been edited successfully.</strong></p>
			<#else>
				<p><strong>Your news article has been submitted successfully.</strong></p>
			</#if>
			<p><@a mod="news" dest="article.ws?id=${newArticleId}">Click here to view it.</@a></p>
		<#else>
			<ul id="tips">
				<li>Try to keep descriptions short, as the title page has a <strong>fixed</strong> height for news description sections.</li>
				<li>Double line-breaks in article bodies will be automatically translated into HTML &lt;p&gt;&lt;/p&gt; tags when viewing the article.</li>
			</ul>
			
			<hr />
			
			<form id="newsForm" method="post" action="${url('staff', 'newsarticle.ws')}">
				<#if update??>
					<input type="hidden" name="id" value="${update}" />
				</#if>
				
				<div class="section">
					<#if errors.title??>
						<label for="inputTitle" class="error">${errors.title}</label>
					<#else>
						<label for="inputTitle">Title:</label>
					</#if>
					<input type="text" id="inputTitle" name="inputTitle" maxlength="50" value="${fieldValues.title}" />
					<input type="hidden" id="inputTitleChars" value="50" />
					<div id="inputTitleCharlimiter" class="charlimiter"></div>
				</div>
				
				<div class="section">
					<#if errors.category>
						<div class="label error">Please select a valid category:</div>
					<#else>
						<div class="label">Category:</div>
					</#if>
					<select id="inputCategory" name="inputCategory">
						<option value=""></option>
						
						<#list categories?keys as key>
							<#if key != "0">
								<option value="${key}"<#if fieldValues.category == key> selected="selected"</#if>>${categories[key].getName()}</option>
							</#if>
						</#list>
					</select>
				</div>
				
				<div class="section">
					<#if errors.description??>
						<label for="inputDescription" class="error">${errors.description}</label>
					<#else>
						<label for="inputDescription">Description:</label>
					</#if>
					<textarea id="inputDescription" name="inputDescription" maxlength="1024">${fieldValues.description}</textarea>
					<input type="hidden" id="inputDescriptionChars" value="1024" />
					<div id="inputDescriptionCharlimiter" class="charlimiter"></div>
				</div>
				
				<div class="section">
					<#if errors.body??>
						<label for="inputBody" class="error">${errors.body}</label>
					<#else>
						<label for="inputBody">Body:</label>
					</#if>
					<textarea id="inputBody" name="inputBody" maxlength="65535">${fieldValues.body}</textarea>
					<input type="hidden" id="inputBodyChars" value="65535" />
					<div id="inputBodyCharlimiter" class="charlimiter"></div>
				</div>
				
				<div class="section">
					<button name="newsSubmit"><#if update??>Edit<#else>Post</#if> Article</button>
				</div>
			</form>
		</#if>
	</div>
</div>

<@location>
	<@a mod="staff" dest="center.ws">Staff Center</@a> &gt; <#if update??>Edit<#else>Post</#if> News Article
</@location>

<script type="text/javascript">
	new Charlimiter("inputTitle");
	new Charlimiter("inputDescription");
	new Charlimiter("inputBody");
</script>

<#include "../../../inc/footer.ftl" />