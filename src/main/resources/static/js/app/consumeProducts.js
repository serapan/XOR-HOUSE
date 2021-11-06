var $price = document.getElementById("productPrice");

var app = angular.module('consumeProductsApp', ['ngCookies']);

app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});

app.directive('bootstrapSelectpicker', function(){
    var bootDirective = {
        restrict : 'A',
        link: function(scope, element, attr){
            $(element).selectpicker();
        }
    };
    return bootDirective;
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

app.controller('ProductsController', ['$scope', '$http', '$window','$location', 'AuthService', function($scope, $http, $window, $location, AuthService) {
    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.logout = AuthService.logout;
    $scope.counter = 100;
    $scope.Math = window.Math;
    var $temp = $location.search();

    $scope.byRange = function (fieldName, minLikes, maxLikes) {
        if (minLikes === undefined || maxLikes === undefined || maxLikes === null || minLikes === null) {
            return function predicateFunctionTwo(item) {
                return item[fieldName];
            }
        } else {
            return function predicateFunc(item) {
                return minLikes <= item[fieldName] && item[fieldName] <= maxLikes;
            };
        }
    };


    $scope.propertify = function (string) {
        var p = $parse(string);
        var s = p.assign;
        return function(newVal) {
            if (newVal) {
                s($scope,newVal);
            }
            return p($scope);
        } ;
    };

    if ($temp.category === 'all') {
        $scope.all = true;
        $scope.Try = {}
        $scope.Try.name = $location.search().search;
        $scope.counter = 100;
        if($temp.storeid) {
            $scope.urlAPI='/observatory/api/display/stores/'+$temp.storeid;
        }
        else $scope.urlAPI = '/observatory/api/products?start=0&count=+' + ($scope.counter) + '+&sort=id%7CASC&status=ALL';
        $http.get($scope.urlAPI).then(function (response) {
            $scope.products = response.data;
        });
        $scope.showMore = function() {
            $scope.counter = $scope.counter + 12;
            $scope.urlAPI = '/observatory/api/products?start=0&count=+' + ($scope.counter) + '+&sort=id%7CASC&status=ALL';
            $http.get($scope.urlAPI).then(function (response) {
                $scope.products= angular.extend({},$scope.products, response.data);
            });
        };
    }
    else {


        if ($temp.category === 'laptop') {
            $scope.laptop = true;
        }
        else if ($temp.category === 'phone') {
            $scope.phone = true;
        }
        else if ($temp.category === 'tablet') {
            $scope.tablet = true;
        }
        else if ($temp.category === 'tv') {
            $scope.tv = true;
        }

        $scope.urlAPI = '/observatory/api/category/'+ $temp.category + '?start=0&count=+' + ($scope.counter);


        $http.get($scope.urlAPI).then(function (response) {
            $scope.products = response.data;
        });
        $price.style.display = "block";

        $scope.urlAPI = '/observatory/api/category/extradata/'+ $temp.category;
        $http.get($scope.urlAPI).then(function (response) {
            $scope.filters = response.data;
        });

        $scope.clearSearch = function(param) {
            param = {};
        };

        $scope.showMore = function() {
            $scope.counter = $scope.counter + 12;
            $scope.urlAPI = '/observatory/api/category/'+ $temp.category + '?start=0&count=+' + ($scope.counter);
            $http.get($scope.urlAPI).then(function (response) {
                $scope.products= angular.extend({},$scope.products, response.data);
            });
        };
    }

    $scope.range = function(max) {
        var input = [];
        for (var i = 1; i <= max; i += 1) {
            input.push(i);
        }
        return input;
    };

    $scope.goToProduct = function (id) {
        $window.open('/product?id='+id, '_self');
    };
}]);

app.controller('LoginController', ['$scope', '$http','$cookies', '$window', function($scope, $http, $cookies, $window) {
    $scope.Submit = function() {
        var parameter = JSON.stringify({username:$scope.user, password:$scope.pass});
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


