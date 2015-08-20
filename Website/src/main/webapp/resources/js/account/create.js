(function () {
	
	var app = angular.module("CreateAccountApp", []);
	
	app.controller("CreateAccountCtrl", [ "$http", "$scope", function($http, $scope) {

		var usernameErrors = new Array(
			"Please input a valid username",
			"Usernames must be between 1 and 12 characters in length", 
			"Usernames may only contain letters, numbers, and spaces, please choose a different username",
			"Sorry, this username already exists. Please choose a new one",
			"An error has occurred, please try again"
		);
		
		var passwordErrors = new Array(
			"Please fill out both password fields.",
			"Passwords must be between 5 and 20 characters in length.",
			"Passwords may only contain letters and numbers.",
			"Please make sure that both passwords match."
		);
		
		var state = 0;
		
		this.waiting = false;
		
		this.dobError = false;
		this.countryError = false;
		this.usernameError = null;
		this.usernameSuccess = false;
		this.termsError = false;
		this.passwordError = null;
		
		this.dob = "";
		this.countryCode = "";
		this.username = "";
		this.terms = false;
		this.password1 = "";
		this.password2 = "";
		
		this.init = function() {
			if(passState) {
				this.dob = initialDob;
				this.countryCode = initialCountry;
				this.username = initialUsername;
				this.terms = true;
				state = 3;
			}
		};
		
		this.hasPasswordError = function() {
			return this.passwordError !== null;
		};
		
		this.hasUsernameError = function() {
			return this.usernameError !== null;
		};
		
		this.isOnState = function(checkState) {
			return state == checkState;
		};
		
		this.isPastState = function(checkState) {
			return state > checkState;
		};
		
		this.submitPasswords = function() {
			this.waiting = true;
			
			this.passwordError = null;
			
			var passwordReturn = this.validatePasswords();
			if(passwordReturn > -1) {
				this.passwordError = passwordErrors[passwordReturn];
			} else {
				$("#createContent").submit();
			}
			
			this.waiting = false;
		};
		
		this.validatePasswords = function() {
			if(this.password1 == "" || this.password2 == "") {
				return 0;
			}
			
			if(this.password1.length < 5 || this.password1.length > 20) {
				return 1;
			}
			
			if(!this.password1.match(/^[a-zA-Z0-9]{5,20}$/)) {
				return 2;
			}
			
			if(this.password1 !== this.password2) {
				return 3;
			}
			
			return -1;
		};
		
		this.submitTerms = function() {
			if(!this.terms) {
				this.termsError = true;
			} else {
				state = 3;
			}
		};
		
		this.submitUsername = function(shouldContinue) {
			this.waiting = true;
			
			this.usernameError = null;
			this.usernameSuccess = false;
			
			var usernameReturn = this.validateUsername();
			if(usernameReturn > -1) {
				this.usernameError = usernameErrors[usernameReturn];
				this.waiting = false;
				return;
			}
			
			var thisObj = this;
			checkUsername(this.username, 
				function(response) {
					if(response.code == -1) {
						// Username Free
						if(shouldContinue) {
							state = 2;
						} else {
							thisObj.usernameSuccess = true;
						}
					} else if(response.code == 3) {
						// Username Exists
						thisObj.usernameError = usernameErrors[3];
					} else if(response.code == 4) {
						// Error
						thisObj.usernameError = usernameErrors[4];
					}
					
					thisObj.waiting = false;
				}, function(response) {
					thisObj.usernameError = usernameErrors[4];
					thisObj.waiting = false;
				}
			);
		};
		
		this.validateUsername = function() {
			if(this.username == "") {
				return 0;
			}
			
			this.username = this.username.trim();
			while(this.username.indexOf("  ") > -1) {
				this.username = this.username.replace("  ", " ");
			}
			
			if(this.username.length < 1 || this.username.length > 12) {
				return 1;
			}
			
			if(!this.username.match(/^[a-zA-Z0-9 ]{1,12}$/)) {
				return 2;
			}
			
			return -1;
		};
		
		function checkUsername(username, callback, error) {
			$http({
				method: "GET",
				url: appUrl + "checkusername.ws",
				params: {
					"username": username
				}
			})
			.success(function(response) {
				callback(response);
			})
			.error(function(response) {
				error(response);
			});
		}
		
		this.submitAgeAndCountry = function() {
			this.waiting = true;
			
			var returnType = true;
			
			this.dobError = false;
			this.countryError = false;
			
			if(!this.validateDob()) {
				returnType = false;
				this.dobError = true;
			}
			
			if(!this.validateCountry()) {
				returnType = false;
				this.countryError = true;
			}
			
			if(returnType) {
				state = 1;
			}
			
			this.waiting = false;
		};
		
		this.validateCountry = function() {
			if(this.countryCode == "") {
				return false;
			}
			
			this.countryCode = this.countryCode.trim();
			if(this.countryCode.length < 1 || this.countryCode.length > 3) {
				return false;
			}
			
			var countryInt = parseInt(this.countryCode);
			if(isNaN(countryInt)) {
				return false;
			}
			
			if(countryInt < 0 || countryInt > 255) {
				return false;
			}
			
			return true;
		};
		
		this.validateDob = function() {
			if(this.dob == "") {
				return false;
			}
			
			this.dob = this.dob.trim();
			if(this.dob.length < 8 || this.dob.length > 10) {
				return false;
			}
			
			var parts = this.dob.split("/");
			if(parts.length !== 3) {
				return false;
			}
			
			var day = parts[0];
			var month = parts[1];
			var year = parts[2];
			
			if(day.length < 1 || day.length > 2) {
				return false;
			}
			if(month.length < 1 || month.length > 2) {
				return false;
			}
			if(year.length !== 4) {
				return false;
			}
			
			day = parseInt(day);
			month = parseInt(month);
			year = parseInt(year);
			
			if(isNaN(day) || isNaN(month) || isNaN(year)) {
				return false;
			}
			
			if(day < 1 || day > 31) {
				return false;
			}
			if(month < 1 || month > 12) {
				return false;
			}
			if(year < 1900 || year > 2015) {
				return false;
			}
			
			var date = new Date(year, month - 1, day);
			if(year !== date.getFullYear()) {
				return false;
			}
			if(month !== date.getMonth() + 1) {
				return false;
			}
			if(day !== date.getDate()) {
				return false;
			}
			
			return true;
		};
		
		this.init();
		
	}]);
	
})();