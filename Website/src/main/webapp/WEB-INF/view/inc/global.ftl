<!DOCTYPE html>

<#function url mod dest>
	<#if mod == "main">
		<#local mod = "" />
	<#else>
		<#local mod = "/m=${mod}" />
	</#if>
	
	<#return "https://www.${hostName}${mod}/${dest}" />
</#function>

<#function replaceNewLines string>
	<#return string?replace("\n", "<br />")?replace("\r", "<br />") />
</#function>

<#function doubleBreaksToParagraphs string>
	<#return string?replace("\n\n", "</p><p>")?replace("\r\n", "</p><p>")?replace("\r\r", "</p><p>") />
</#function>

<#macro a mod dest><a href="${url(mod, dest)}"><#nested /></a></#macro>

<#macro location>
	<div class="location">
		<strong>Location:</strong> <span><@a mod="main" dest="title.ws">Home</@a> &gt; <#nested /></span>
	</div>
</#macro>