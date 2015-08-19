(function () {
	
	var app = angular.module("CreateAccountApp", []);
	
	app.controller("CreateAccountCtrl", [ "$http", "$scope", function($http, $scope) {

		var state = 0;
		
		this.dob = "";
		this.countryCode = "";
		
		this.isOnState(checkState) {
			return state = checkState;
		};
		
		this.isPastState(checkState) {
			return state > checkState;
		};
		
	}]);
	
})();