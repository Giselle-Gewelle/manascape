<#assign cssImports = [ "main/title" ] />
<#include "../../inc/header.ftl" />

<div id="titlePage">
	<div id="left">
		<fieldset>
			<legend>${gameName}</legend>
			
			<ul>
				<li><@a mod="game" dest="worldlist.ws">Play ${gameName}</@a></li>
				<li><@a mod="create" dest="index.ws">Create a Free Account</@a></li>
			</ul>
		</fieldset>
		
		<fieldset>
			<legend>Account Management</legend>
			
			<ul>
				<li><@a mod="password" dest="changepass.ws">Change Your Password</@a></li>
			</ul>
		</fieldset>
		
		<fieldset>
			<legend>Latest Poll</legend>
			
			<div>Random Poll?! YOU decide!</div>
			
			<ul>
				<li><@a mod="poll" dest="latest.ws">Vote in this Poll</@a></li>
				<li><@a mod="poll" dest="archive.ws">Poll Archives</@a></li>
			</ul>
		</fieldset>
		
		<fieldset>
			<legend>Community</legend>
			
			<ul>
				<li><@a mod="forum" dest="forums.ws">Official Forums</@a></li>
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