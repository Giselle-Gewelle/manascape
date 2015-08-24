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
					
					<div class="article">
						<div class="header">
							<div class="floatRight">
								24-Aug-2015
							</div>
							
							<div>
								Test Title 3
							</div>
						</div>
						
						<div class="body">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam tincidunt lorem justo, ac auctor massa feugiat non. 
							Pellentesque vehicula pharetra felis, non tincidunt ipsum bibendum eget. Curabitur sit amet quam magna. 
							Quisque varius, enim varius convallis dictum, tellus nisi rutrum est, eget vestibulum ipsum lectus in sem. 
							Vestibulum posuere consectetur nibh tempus blandit. Pellentesque vulputate eleifend turpis.
						</div>
						
						<div class="more">
							<@a mod="news" dest="article.ws?id=">Read full article...</@a></li>
						</div>
					</div>
					
					<div class="article">
						<div class="header">
							<div class="floatRight">
								24-Aug-2015
							</div>
							
							<div>
								Test Title 2
							</div>
						</div>
						
						<div class="body">
							Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam tincidunt lorem justo, ac auctor massa feugiat non. 
							Pellentesque vehicula pharetra felis, non tincidunt ipsum bibendum eget. Curabitur sit amet quam magna. 
						</div>
						
						<div class="more">
							<@a mod="news" dest="article.ws?id=">Read full article...</@a></li>
						</div>
					</div>
					
					<div id="newsArchive">
						<@a mod="news" dest="archive.ws">Browse the News Archives</@a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="clear"></div>

<#include "../../inc/footer.ftl" />