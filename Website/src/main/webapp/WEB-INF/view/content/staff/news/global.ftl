<#macro style>
	<#--
	Doing this because we don't want outside sources to be able to see Staff Page styling.
	-->
	
	#tips {
		margin: 0;
		padding: 0;
	}
	#tips li {
		list-style: square inside;
		margin: 0;
		padding: 0 5px;
	}
	
	.section {
		margin-top: 1em;
	}
	#newsForm input, #newsForm textarea, #newsForm select {
		display: block;
		margin-top: 2px;
	}
	#newsForm textarea {
		width: 100%;
		max-width: 100%;
		min-width: 100%;
		box-sizing: border-box;
	}
	#inputTitle {
		width: 350px;
	}
	#inputDescription {
		height: 60px;
		min-height: 60px;
		max-height: 60px;
	}
	#inputBody {
		height: 250px;
		min-height: 200px;
		max-height: 500px;
	}
	.charlimiter {
		margin-top: 2px;
	}
	
</#macro>

<#include "../../../inc/header.ftl" />