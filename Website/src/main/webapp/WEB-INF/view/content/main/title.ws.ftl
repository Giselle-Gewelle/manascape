<#assign cssImports = [ "main/title" ] />
<#include "../../inc/header.ftl" />

<#macro nav mod dest icon>
	<li style="list-style-image: url('${url("main", "resources/img/content/main/title/bullets/${icon}.png")}');"><a href="${url(mod, dest)}"><#nested /></a></li>
</#macro>

<div id="titlePage">
	<div id="left">
		<fieldset>
			<legend>${gameName}</legend>
			
			<ul>
				<@nav mod="create" dest="index.ws" icon="create">Create a Free Account</@nav>
				<@nav mod="news" dest="archive.ws" icon="news">Latest News</@nav>
			</ul>
		</fieldset>
		
		<fieldset>
			<legend>Account Management</legend>
			
			<ul>
				<@nav mod="password" dest="changepass.ws" icon="changepass">Change Your Password</@nav>
			</ul>
		</fieldset>
		
		<fieldset>
			<legend>Latest Poll</legend>
			
			<div>Random Poll?! YOU decide!</div>
			
			<ul>
				<@nav mod="poll" dest="latest.ws" icon="pollvote">Vote in this Poll</@nav>
				<@nav mod="poll" dest="archive.ws" icon="pollarchive">Poll Archives</@nav>
			</ul>
		</fieldset>
		
		<fieldset>
			<legend>Community</legend>
			
			<ul>
				<@nav mod="forum" dest="forums.ws" icon="forums">Official Forums</@nav>
			</ul>
		</fieldset>
	</div>
	
	<div id="right">
		<div class="scroll">
			<div class="content">
				<div id="newsFeed">
					<div id="latestNews"></div>
					
					<#if newsFeed??>
						<#list newsFeed as article>
							<div class="article">
								<div class="header">
									<div class="floatRight">${article.date}</div>
									
									<div>${article.title}</div>
								</div>
								
								<div class="body">${article.description}</div>
								
								<div class="more">
									<@a mod="news" dest="article.ws?id=${article.id}">Read full article...</@a></li>
								</div>
							</div>
						</#list>
						
						<div id="newsArchive">
							<@a mod="news" dest="archive.ws">Browse the News Archives</@a>
						</div>
					<#else>
						<p>An error has occurred while attempting to load the news feed, please reload the page.</p>
					</#if>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="clear"></div>

<#include "../../inc/footer.ftl" />