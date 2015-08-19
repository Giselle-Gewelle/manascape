<#assign cssImports = [ "account/create" ] />
<#assign jsImports = [ "account/create" ] />

<#assign angular = 'ng-app="CreateAccountApp" ng-controller="CreateAccountCtrl as ctrl"' />

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
		
		<form id="createContent">
			<div ng-show="ctrl.isOnState(0)">
				<h2>Indicate Age and Location</h2>
				
				<p>Your age and location are required for account identification purposes. Don't worry though, your personal information will never be handed out to third parties.</p>
				
				<label for="dob">Date of Birth:</label>
				<input type="text" id="dob" name="dob" value="" placeholder="DD/MM/YYYY" ng-model="ctrl.dob" />
				
				<div class="spacer"></div>
				
				<span class="label">Country of Residence:</span>
				<select id="country" name="country" ng-model="ctrl.countryCode">
					<option value=""></option>
					
					<#list countryMap?keys as key>
						<option value="${key}">${countryMap[key]}</option>
					</#list>
				</select>
				
				<div class="spacer"></div>
				
				<button>Continue</button>
			</div>
		</form>
	</div>
</div>

<@location>
	Create an Account
</@location>

<#include "../../inc/footer.ftl" />