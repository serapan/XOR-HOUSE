var app = angular.module('SignUp', ['ngCookies']);

app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});


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

app.controller('SignUpController', ['$scope', '$http', '$window', 'AuthService', '$cookies', function($scope, $http, $window, AuthService, $cookies) {
    $scope.role="subscribed";
    console.log("what");
    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.logout = AuthService.logout;
    $scope.Register= function(){
        console.log($scope.password, $scope.cpassword);
        if($scope.password !== $scope.cpassword) $window.alert('Passwords don\'t match!');
        else {
            var parameter = JSON.stringify({
                fname: $scope.fname,
                lname: $scope.lname,
                uname: $scope.uname,
                password: $scope.password,
                email: $scope.email,
                role: $scope.role
            });
            $http.post('/observatory/api/user/signup', parameter).success(function () {
                $http.post('/observatory/api/login', {"username":$scope.uname, "password":$scope.password}).
                success(function(data) {
                    $cookies['token'] = data.token;
                    $window.location.href = $window.location.origin + '/';
                });
            }).error(function (data) {
                $window.alert(data.message);
            });
        }
    }}
]);

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