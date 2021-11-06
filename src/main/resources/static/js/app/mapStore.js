var app = angular.module('mapStoresApp', ['ngCookies']);


app.config(function($locationProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});

app.service('Map', function($q) {
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

app.controller('MapController', ['$scope', '$http', 'Map', '$location', '$window','AuthService',function($scope, $http, Map, $location, $window, AuthService) {


    $scope.updateMap = function(){
        $window.open('/tryM?dist='+$scope.distance, '_self');
    };

    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.logout = AuthService.logout;
    $scope.StoreProducts = function(id){
        $window.open('/tryP?category=all&storeid='+id, '_self');
    };

    Map.init();

    console.log($location.search());
    if($location.search().dist === undefined) {

        $scope.url = '/observatory/api/shops?start=0&count=100&sort=id%7CASC&status=ACTIVE';

        var request = $http.get($scope.url).then(function (response) {
            $scope.stores = response.data.shops;
            return response.data.shops;
        }).then(function (stores) {
            angular.forEach(stores, function (value, index) {
                var lat = value.lat;
                var lng = value.lng;
                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(lat, lng),
                    map: Map.map,
                    icon: {url: "img/storeico.png", scaledSize: new google.maps.Size(34, 34)},
                    title: value.name,
                });

                marker.addListener('mouseover', function() {
                    var infowindow = new google.maps.InfoWindow({
                        content: marker.title
                    });
                    infowindow.open(map, marker);

                    marker.addListener('mouseout', function() {
                        infowindow.close();
                    });
                });


            });
        });
    }
    else {
            if (navigator.geolocation) {
                console.log("what");
                navigator.geolocation.getCurrentPosition(function (position) {
                    var pos = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    };
                    $scope.url = '/observatory/api/myshops?start=0&count=100&sort=id%7CASC&status=ACTIVE&lng=' + pos.lng + '&lat=' + pos.lat + "&dist=" + $location.search().dist;
                    var request = $http.get($scope.url).then(function (response) {
                        $scope.stores = response.data.shops;
                        return response.data.shops;
                    }).then(function (stores) {
                        angular.forEach(stores, function (value, index) {
                            var lat = value.lat;
                            var lng = value.lng;
                            var marker = new google.maps.Marker({
                                position: new google.maps.LatLng(lat, lng),
                                map: Map.map,
                                icon: {url: "img/storeico.png", scaledSize: new google.maps.Size(34, 34)},
                                title: value.name,
                            });

                            marker.addListener('mouseover', function() {
                                var infowindow = new google.maps.InfoWindow({
                                    content: marker.title
                                });
                                infowindow.open(map, marker);

                                marker.addListener('mouseout', function() {
                                    infowindow.close();
                                });
                            });


                        });
                    });
                    }, function () {
                    handleLocationError(true, infoWindow, Map.map.getCenter());
                });
            }

    };




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
}]);




