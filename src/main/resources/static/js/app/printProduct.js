angular.module('printProductApp', ['ngCookies'])
    .config(function($locationProvider) {
        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        });
    })
    .controller('ProductController', ['$scope', '$http','$location', '$window', '$cookies', 'AuthService', function($scope, $http, $location, $window, $cookies, AuthService) {
        $scope.isAuthenticated = AuthService.isAuthenticated;
        $scope.logout = AuthService.logout;
        $scope.Math = window.Math;
        var $temp = $location.search();



        $scope.id = $temp.id;
        $http.get('/observatory/api/products/' + $temp.id).then(function (response) {
            $scope.product= response.data;
        });



        $scope.mySubmit = function(myrating) {
            var req = {
                method: 'PATCH',
                url: '/observatory/api/myproducts/' + $scope.id,
                headers: {
                    'Content-Type': 'application/json',
                    'X-OBSERVATORY-AUTH': $cookies.get("token")
                },
                data: {extraData: {rating: myrating}}
            };

            $http(req).success(function(){ $window.location.reload()}).error(function(){$window.alert("You be logged in to rate this product")});
        };

        $scope.range = function(max) {
            var input = [];
            for (var i = 1; i <= max; i += 1) {
                input.push(i);
            }
            return input;
        };
    }])
    .service('Map', function($q) {
    this.init = function() {
        var MapOptions = {
            center:  {lat: 37.978714, lng:  23.782834},
            zoom: 13,
            disableDefaultUI: false,
            draggable: true,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        this.map = new google.maps.Map(document.getElementById("map"), MapOptions);
    }
    })

    .controller('MapController', function($scope, $http, Map) {
        Map.init();
        $http.get('/observatory/api/display/products/'+$scope.id).then(function (response) {
            $scope.stores = response.data.prices;
            console.log($scope.stores);
            return $scope.stores;
    }).then(function (stores) {
        angular.forEach(stores, function (value, index) {
            var lat = value.lat;
            var lng = value.lng;
            var marker = new google.maps.Marker({
                position: new google.maps.LatLng(lat, lng),
                map: Map.map,
                icon: {url: "img/storeico.png", scaledSize: new google.maps.Size(34, 34)},
                title: value.name
            });

            marker.addListener('mouseover', function() {
                var infoWindow = new google.maps.InfoWindow({
                    content: marker.title
                });
                infoWindow.open(map, marker);

                marker.addListener('mouseout', function() {
                    infoWindow.close();
                });
            });


        });
    });

// Try HTML5 geolocation.
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var pos = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            Map.map.setCenter(pos);

            var marker = new google.maps.Marker({
                position: new google.maps.LatLng(pos.lat, pos.lng),
                map: Map.map,
                icon: {url:"img/youarehere.png", scaledSize: new google.maps.Size(34, 34)}
            });

        }, function() {
            handleLocationError(true, infoWindow, Map.map.getCenter());
        });




    } else {
        // Browser doesn't support Geolocation
        handleLocationError(false, infoWindow, Map.map.getCenter());
    }

    function handleLocationError(browserHasGeolocation, infoWindow, pos) {
        infoWindow.setPosition(pos);
        infoWindow.setContent(browserHasGeolocation ?
            'Error: The Geolocation service failed.' :
            'Error: Your browser doesn\'t support geolocation.');
        infoWindow.open(Map.map);
    }


    $scope.centerOnStore = function(lat, lng) {
        Map.map.setCenter(new google.maps.LatLng(lat, lng));
        Map.map.setZoom(18);
    }
    })

    .factory('AuthService', ['$cookies', '$http', '$window', function($cookies, $http, $window){

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
}])

    .controller('LoginController', ['$scope', '$http','$cookies', '$window', function($scope, $http, $cookies, $window) {
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