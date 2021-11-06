var app = angular.module('statsApp', ['ngCookies', 'ng-fusioncharts']);

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

app.controller('GeneralController', ['$scope', '$http', '$window', '$cookies', 'AuthService', function($scope, $http, $window, $cookies, AuthService) {
    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.logout = AuthService.logout;
    $scope.chart="1";
    $scope.pie = {
        "chart": {
            "caption": "Πλήθος Προϊόντων ανά Κατάστημα που περιλαμβάνει η βάση δεδομένων της Cheapset",
            "subcaption": "Ποσοστό επί %",
            "showvalues": "1",
            "showpercentintooltip": "0",
            "numbersuffix": "%",
            "enablemultislicing": "0",
            "showLegend": "0",
            "theme": "zune"
        },
        "data": []
    };

    $scope.bar = {
        "chart": {
            "caption": "Πλήθος Προϊόντων ανά Κατηγορία",
            "showvalues": "1",
            "showpercentintooltip": "0",
            "enablemultislicing": "0",
            "showLegend": "0",
            "theme": "zune"
        },
        "data": []
    };

    $scope.bar2 = {
        "chart": {
            "caption": "10 Καταστήματα με τις περισσότερες χαμηλότερες τιμές",
            "yaxisname": "Πλήθος Χαμηλότερων Τιμών",
            "aligncaptionwithcanvas": "0",
            "plottooltext": "<b>$dataValue</b> leads received",
            "theme": "zune"
        },
        "data": []
    };

    $http.get('/observatory/api/shops?start=0&count=100&sort=id%7CASC&status=ACTIVE').success(function(data) {
        $scope.shops = data.shops;
    });
    if ($scope.isAuthenticated()) {
        var config = {headers: {"X-OBSERVATORY-AUTH": $cookies.get('token')}};
        $http.get('/observatory/api/cheapsetstatistics/pie', config).success(function (data) {
            $scope.pie.data = data;
            $http.get('/observatory/api/cheapsetstatistics/goodstores', config).success(function (data) {
                $scope.bar2.data = data;
            });
        }).error(function () {
            $window.alert('You should be a Merchant to view Statistics!');
        });
    }
    else {
        $window.alert('You should be logged in to view Statistics!');
        $window.history.back();
    }

    $scope.printDiagram = function() {
        console.log($scope.shopid);
        $http.get('/observatory/api/cheapsetstatistics/productnumcat/' + $scope.shopid, config).success(function (data) {
            $scope.bar.data = data;
            console.log($scope.bar.data);
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