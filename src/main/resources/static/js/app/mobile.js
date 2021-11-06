var myApp = angular.module('myApp', ['ngCookies']);

myApp.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});

myApp.directive('fileModel', ['$parse', function ($parse) {
    return {
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

myApp.service('fileUpload', ['$q','$http', '$window', '$cookies', function ($q,$http, $window, $cookies) {
    var deffered = $q.defer();
    var responseData;
    this.uploadFileToUrl = function(file, uploadUrl){
        var fd = new FormData();
        fd.append('file', file);
        return $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: { 'Content-Type' : undefined}
        })
            .success(function(response){
                /* $scope.errors = response.data.value; */
                console.log(response);
                $cookies.putObject('data', response);
                responseData = response;
                deffered.resolve(response);
                $window.open("/addproduct", '_self');
                //$window.open("/addproduct", '_self');
                return deffered.promise;
            })
            .error(function(error){
                deffered.reject(error);
                return deffered.promise;
            });
    }
    this.getResponse = function() {
        return responseData;
    }
}]);

myApp.controller('myCtrl', ['$scope', '$q', 'fileUpload', 'AuthService', function($scope, $q, fileUpload, AuthService){
    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.logout = AuthService.logout;
    $scope.dataUpload = true;
    $scope.errVisibility = false;
    $scope.uploadFile = function(){
        var file = $scope.myFile;
        console.log('file is ' );
        console.dir(file);
        var uploadUrl = "/upload";
        fileUpload.uploadFileToUrl(file, uploadUrl).then(function(result){
            $scope.errors = fileUpload.getResponse();
            console.log($scope.errors);
            $scope.errVisibility = true;
        }, function(error) {
            alert('error');
        })
    };
}]);

myApp.factory('AuthService', ['$cookies', '$http', '$window', function($cookies, $http, $window){

    var authService = {};

    authService.isAuthenticated = function() {
        if ($cookies.get("token") != null) {
            return true;
        }
        return false;
    };


    authService.logout = function() {
        var logoutReq = {
            method: 'POST',
            url: '/observatory/api/logout',
            headers: {
                'X-OBSERVATORY-AUTH': $cookies.get('token')
            }
        };

        $http(logoutReq).then(function successCallback(response) {
            // this callback will be called asynchronously
            $cookies.remove("token");
            $window.location.reload();
        });
    };

    return authService;
}]);

myApp.controller('LoginController', ['$scope', '$http','$cookies', '$window', function($scope, $http, $cookies, $window) {
    $scope.Submit = function() {
        var parameter = JSON.stringify({username:$scope.user, password:$scope.pass});
        console.log(parameter);
        $http.post('/observatory/api/login', parameter).
        success(function(data) {
            $cookies.put('token', data.token);
            $window.location.reload();
        }).
        error(function(data, status) {
            if (status === 400) {
                $window.alert('Wrong Username or Password!')
            }
            else {
                $window.alert('Something went wrong.\n' +
                    'Please try again.')
            }
        });
    };
}]);