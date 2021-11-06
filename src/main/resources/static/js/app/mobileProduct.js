var app = angular.module('mobileApp', ['ngCookies']);

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

app.controller('GeneralController', ['$scope', '$http', '$window', '$cookies', 'AuthService', '$filter', function($scope, $http, $window, $cookies, AuthService, $filter) {
    var data = $cookies.getObject('data');

    $scope.catopt = $filter('lowercase')(data.Category);
    $scope.name = data.Model;
    $scope.desc = data.Description;
    $scope.tags = data.Tags;
    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.logout = AuthService.logout;

    if($scope.catopt === 'laptop') {
        $scope.cpu = data.CPU;
        $scope.storage = data.Storage;
        $scope.ram = data.Ram;
        $scope.graphics = data.Graphics;
        $scope.brand = data.Brand;
    }
    else if($scope.catopt === 'phone') {
        $scope.brand = data.Brand;
        $scope.cpu = data.CPU;
        $scope.ram = data.Ram;
        $scope.screen = data.Screen;

    }
    else if($scope.catopt === 'tablet') {
        $scope.brand = data.Brand;
        $scope.cpu = data.CPU;
        $scope.ram = data.Ram;
        $scope.screen = data.Screen;
        $scope.conn = data.Connectivity;
    }
    else if($scope.catopt === 'gaming') {
        $scope.storage = data.Storage;
        $scope.output = data.Output;
        $scope.brand = data.Brand;
    }
    else if($scope.catopt === 'tv') {
        $scope.type = data.Type;
        $scope.size = data.Size;
        $scope.res = data.Resolution;
        $scope.freq = data.Frequency;
        $scope.smart = data.Smart;
        $scope.brand = data.Brand;
    }


    var extraData;
    $scope.submitProduct = function() {
        if($scope.catopt === 'laptop') {
            extraData = {"rating":$scope.rating+"", "cpu":$scope.cpu+"", "storage":$scope.storage+"", "graphics":$scope.graphics+"", "brand":$scope.brand+"", "ram":$scope.ram+""};
        }
        else if($scope.catopt === 'phone') {
            extraData = {'rating':$scope.rating+"", 'screen size/technology':$scope.screen+"", "cpu":$scope.cpu+"", "ram/rom":$scope.ram+"", "brand":$scope.brand+""};
        }
        else if($scope.catopt === 'tablet') {
            extraData = {'rating':$scope.rating+"", 'screen size/technology':$scope.screen+"", 'connectivity':$scope.conn+"", "cpu":$scope.cpu+"", "ram/rom":$scope.ram+"", "brand":$scope.brand+""};
        }
        else if($scope.catopt === 'gaming') {
            extraData = {"rating":$scope.rating+"", "storage":$scope.storage+"", "output type":$scope.output+"", "brand":$scope.brand+""};
        }
        else if($scope.catopt === 'tv') {
            extraData = {"rating":$scope.rating+"", "type":$scope.type+"", "size":$scope.size+"", "resolution":$scope.res+"", "frequency":$scope.freq+"", "brand":$scope.brand+"", "smart":$scope.smart+""};
        }
        var parameter = {name:$scope.name, description:$scope.desc, category:$scope.catopt, tags:[$scope.tags], extraData:extraData};

        var req = {
            method: 'POST',
            url: '/observatory/api/myproducts/',
            headers: {
                'Content-Type': 'application/json',
                'X-OBSERVATORY-AUTH': $cookies.get("token")
            },
            data: JSON.stringify(parameter)
        };
        $http(req).
        success(function(data) {
            $window.alert("Product was successfully added!");
            $window.open('/addstore?id='+data.id, '_self');
        }).
        error(function(data, status) {
            if(status === 403 || !$cookies.get("token")) $window.alert('You are not Authorized to add a product!');
            else {
                req = {
                    method: 'POST',
                    url: '/observatory/api/myproducts/findid/',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    data: JSON.stringify(parameter)
                };
                $http(req).
                success(function (data) {
                    $window.alert('Product already exists!');
                    $window.open('/addstore?id=' + data.id, '_self');
                }).error(function (data, status) {
                    console.log(data, status);
                    console.log("Error", req.data);
                });
            }
        });
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