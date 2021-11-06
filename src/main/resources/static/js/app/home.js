var app = angular.module('homeApp', ['ngCookies']);

app.factory('AuthService', ['$cookies', '$http', '$window', function($cookies, $http, $window){

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



app.controller('homeController', ['$scope', '$http', '$window', 'AuthService', function($scope, $http, $window, AuthService) {

    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.logout = AuthService.logout;
    $http.get('/observatory/api/cheapsetstatistics').then(function (response) {
            $scope.stats = response.data;
        });

        $scope.showCategories = function(cat) {
            $window.open('/tryP?category='+cat, '_self');
        }

}]);

app.controller('LoginController', ['$scope', '$http','$cookies', '$window', function($scope, $http, $cookies, $window) {
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