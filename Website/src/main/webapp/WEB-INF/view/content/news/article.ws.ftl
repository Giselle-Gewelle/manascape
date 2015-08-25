<#assign cssImports = [ "media/news" ] />
<#include "../../inc/header.ftl" />

<#if article??>
	<#assign category = categories[article.category?string] />
	
	<h1>${category.getName()} News</h1>
	
	<@location>
		<@a mod="news" dest="archive.ws">News Archive</@a> &gt; ${category.getName()} News
	</@location>
	
	<div class="scroll">
		<div id="article" class="content">
			<div class="header">
				<strong>${article.date}</strong>
				
				<h2>${article.title}</h2>
			</div>
			
			<div class="body">
				<p>${doubleBreaksToParagraphs(article.body)}</p>
			</div>
		</div>
	</div>
	
	<@location>
		<@a mod="news" dest="archive.ws">News Archive</@a> &gt; ${category.getName()} News
	</@location>
<#else>
	<h1>Article Not Found</h1>
	
	<@location>
		<@a mod="news" dest="archive.ws">News Archive</@a> &gt; Article Not Found
	</@location>
	
	<div class="scroll">
		<div class="content center">
			<p><strong>The specified news article was not found.</strong></p>
			<p><@a mod="news" dest="archive.ws">Click here to view the news archives.</@a></p>
		</div>
	</div>
	
	<@location>
		<@a mod="news" dest="archive.ws">News Archive</@a> &gt; Article Not Found
	</@location>
</#if>

<#include "../../inc/footer.ftl" />