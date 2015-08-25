<#assign cssImports = [ "media/news" ] />
<#include "../../inc/header.ftl" />

<#if category.getId() == 0>
	<#assign title = "News Archive" />
<#else>
	<#assign title = "${category.getName()} News Archive" />
</#if>

<h1>${title}</h1>

<@location>
	${title}
</@location>

<div class="scroll">
	<div class="content">
		<div id="categoryList">
			<#list categories?keys as key>
				<@a mod="news" dest="archive.ws?cat=${categories[key].getId()}"><img src="${url('main', 'resources/img/content/media/news/${categories[key].getIcon()}.png')}" alt="" /> ${categories[key].getName()}</@a><#sep>&nbsp;&nbsp;&nbsp;</#sep>
			</#list>
		</div>
		
		<#if newsArchive??>
			<div id="archive">
				<table>
					<thead>
						<tr>
							<td style="width: 25%;">Category</td>
							<td style="width: 50%;">Title</td>
							<td style="width: 25%;">Date</td>
						</tr>
					</thead>
					
					<tbody>
						<#list newsArchive as article>
							<tr>
								<td><@a mod="news" dest="artchive.ws?cat=${article.category}"><img src="${url('main', 'resources/img/content/media/news/${categories[article.category?string].getIcon()}.png')}" alt="" /> ${categories[article.category?string].getName()}</@a></td>
								<td><@a mod="news" dest="article.ws?id=${article.id}">${article.title}</@a></td>
								<td>${article.date}</td>
							</tr>
						</#list>
					</tbody>
				</table>
			</div>
		<#else>
			<div class="center">
				<p id="noNews"><strong>There is currently no news to display for this category.</strong></p>
			</div>
		</#if>
	</div>
</div>

<@location>
	${title}
</@location>

<#include "../../inc/footer.ftl" />