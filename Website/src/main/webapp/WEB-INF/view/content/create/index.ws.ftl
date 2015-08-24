<#assign cssImports = [ "account/create" ] />
<#assign jsImports = [ "account/create" ] />

<#assign angular = 'ng-app="CreateAccountApp" ng-controller="CreateAccountCtrl as ctrl"' />

<#macro script>
	var appUrl = "${url('create', '')}";
	
	<#if passState??>
		var passState = true;
		var initialDob = "${dob}";
		var initialCountry = "${country}";
		var initialUsername = "${username}";
	<#else>
		var passState = false;
	</#if>
</#macro>

<#macro header>
	<script type="text/javascript" src="https://www.google.com/recaptcha/api.js"></script>
</#macro>

<#include "../../inc/header.ftl" />

<h1>Create an Account</h1>

<@location>
	Create an Account
</@location>

<div class="scroll">
	<div class="content">
		<div id="progressBar">
			<span ng-class="{ active: ctrl.isOnState(0), complete: ctrl.isPastState(0) }">Age and Location</span>
			<span ng-class="{ active: ctrl.isOnState(1), complete: ctrl.isPastState(1) }">Desired Username</span>
			<span ng-class="{ active: ctrl.isOnState(2), complete: ctrl.isPastState(2) }">Terms of Use</span>
			<span ng-class="{ active: ctrl.isOnState(3), complete: ctrl.isPastState(3) }">Desired Password</span>
			<span ng-class="{ active: ctrl.isOnState(4) }">Finish</span>
		</div>
		
		<form id="createContent" autocomplete="off" method="POST" action="${url('create', 'submit.ws')}">
			<div ng-show="ctrl.isOnState(0)">
				<h2>Indicate Age and Location</h2>
				
				<p>Your age and location are required for account identification purposes. Don't worry though, your personal information will never be handed out to third parties.</p>
				
				<label ng-hide="ctrl.dobError" for="dob">Date of Birth:</label>
				<label ng-show="ctrl.dobError" for="dob" class="error">Please input a valid Date of Birth:</label>
				<input type="text" id="dob" name="dob" value="" placeholder="DD/MM/YYYY" maxlength="10" ng-model="ctrl.dob" />
				
				<div class="spacer"></div>
				
				<span ng-hide="ctrl.countryError" class="label">Country of Residence:</span>
				<span ng-show="ctrl.countryError" class="label error">Please select a Country of Residence:</span>
				<select id="country" name="country" ng-model="ctrl.countryCode">
					<option value=""></option>
					
					<#list countryMap?keys as key>
						<option value="${key}">${countryMap[key]}</option>
					</#list>
				</select>
				
				<div class="spacer"></div>
				
				<button onclick="return false;" ng-click="ctrl.submitAgeAndCountry()" ng-disabled="ctrl.waiting">Continue</button>
			</div>
			
			<div ng-show="ctrl.isOnState(1)">
				<h2>Choose a Username</h2>
				
				<p>Usernames can be a maximum of 12 characters long and may contain letters, numbers, and spaces.</p>
				<p>It should not contain your real name, birth date, or other personally identifiable information, to better protect your identity.</p>
				<p>It should not be seriously offensive or break our <@a mod="main" dest="legal/terms.ws">Terms of Use</@a>.</p>
				<p>The Username you choose here is used as your Character name in the game. When playing ${gameName}, first letters in usernames are capitalized. 
					For example, the username <b>cm punk</b> would appear as <b>Cm Punk</b>.</p>
					
				<label ng-hide="ctrl.hasUsernameError() || ctrl.usernameSuccess" for="username">Desired Username:</label>
				<label ng-show="ctrl.hasUsernameError()" for="username" class="error">{{ ctrl.usernameError }}:</label>
				<label ng-show="ctrl.usernameSuccess" for="username" class="success">This username is currently available:</label>
				<input type="text" id="username" name="username" maxlength="12" 
					ng-model="ctrl.username" ng-trim="false" /> 
				<button onclick="return false;" ng-click="ctrl.submitUsername(false)" ng-disabled="ctrl.waiting">Check Availability</button>
				
				<div class="spacer"></div>
				
				<button onclick="return false;" ng-click="ctrl.submitUsername(true)" ng-disabled="ctrl.waiting">Continue</button>
			</div>
			
			<div ng-show="ctrl.isOnState(2)">
				<p>Before creating an account, please read both the ${gameName} <@a mod="main" dest="legal/terms.ws">Terms of Use</@a> and <@a mod="main" dest="legal/privacy.ws">Privacy Policy</@a>.</p>
				<p>If you don't agree with both documents in their entirety, please contact Customer Support and cease use of this website until such a time that you can agree to both documents in full.</p>
				
				<p ng-show="ctrl.termsError" class="error">You must agree with both the Terms of Use and Privacy Policy in order to create an account.</p>
				<input type="checkbox" id="terms" name="terms" ng-model="ctrl.terms" /> <label for="terms" class="inline">I have read and agree to the ${gameName} Terms of Use and Privacy Policy</label>
				
				<div class="spacer"></div>
				
				<button onclick="return false;" ng-click="ctrl.submitTerms()" ng-disabled="ctrl.waiting">Continue</button>
			</div>
			
			<div ng-show="ctrl.isOnState(3)">
				<ul id="passwordDetails">
					<li>Passwords must be between 5 and 20 characters long and may contain only letters and numbers.</li>
					<li>Passwords are case-sensitive.</li>
					<li><strong>NEVER</strong> give anyone your password, not even to ${companyName} staff.</li>
					<li>${companyName} staff will never ask you for your password.</li>
				</ul>
				
				<p class="error" ng-show="ctrl.hasPasswordError()">{{ ctrl.passwordError }}</p>
				
				<#if passState??>
					<p class="error">Invalid reCaptcha value entered, please try again.</p>
				</#if>
				
				<label for="password1">Desired Password:</label>
				<input type="password" id="password1" name="password1" maxlength="20" 
					ng-model="ctrl.password1" ng-trim="false" />
				
				<div class="spacer"></div>
				
				<label for="password1">Confirm Password:</label>
				<input type="password" id="password2" name="password2" maxlength="20" 
					ng-model="ctrl.password2" ng-trim="false" />
				
				<div class="spacer"></div>
				
				<div class="g-recaptcha" data-sitekey="6Lc8kAsTAAAAAC0pFqkczmIpOWU6zR9zZcxDoQ6T"></div>
				
				<div class="spacer"></div>
				
				<button onclick="return false;" ng-click="ctrl.submitPasswords()" ng-disabled="ctrl.waiting">Create Account</button>
			</div>
		</form>
	</div>
</div>

<@location>
	Create an Account
</@location>

<#include "../../inc/footer.ftl" />