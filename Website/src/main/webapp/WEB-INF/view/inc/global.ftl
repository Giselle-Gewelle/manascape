<!DOCTYPE html>

<#function url mod dest secure=true>
	<#if mod == "main">
		<#local mod = "" />
	<#else>
		<#local mod = "/m=${mod}" />
	</#if>
	
	<#if secure && sslEnabled>
		<#local protocol = "https" />
	<#else>
		<#local protocol = "http" />
	</#if>
	
	<#return "${protocol}://www.${hostName}${mod}/${dest}" />
</#function>

<#function replaceNewLines string>
	<#return string?replace("\n", "<br />") />
</#function>

<#macro a mod dest secure=false><a href="${url(mod, dest, secure)}"><#nested /></a></#macro>