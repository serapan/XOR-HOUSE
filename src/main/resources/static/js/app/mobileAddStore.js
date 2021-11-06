var app = angular.module('mobileStoreApp', ['ngCookies']);

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

app.service('Map', function($q) {

    function createMarker(place) {
        var marker = new google.maps.Marker({
            map: map,
            position: place.geometry.location
        });

        google.maps.event.addListener(marker, 'mouseover', function() {
            var infowindow = new google.maps.InfoWindow({
                content: place.name
            });
            infowindow.open(map, marker);

            marker.addListener('mouseout', function() {
                infowindow.close();
            });
        });

        google.maps.event.addListener(marker, 'click', function() {
            var storeName = place.name;
            var storeAddress = place.vicinity;
            var lng = place.geometry.location.lng();
            var lat = place.geometry.location.lat();
            console.log(storeName);
            console.log(storeAddress);
            console.log(lng);
            console.log(lat);
        });

    }
    this.init = function() {

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                var pos = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };

                var MapOptions = {
                    center:  new google.maps.LatLng(pos.lat, pos.lng),
                    zoom: 13,
                    disableDefaultUI: false,
                    draggable: true,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };

                this.map = new google.maps.Map(document.getElementById("map"), MapOptions);

                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(pos.lat, pos.lng),
                    map: this.map,
                    icon: {url: "img/youarehere.png", scaledSize: new google.maps.Size(34, 34)}
                });

                var request = {
                    location: marker.position,
                    radius: 500,
                    type: ['electronics_store', 'school', 'library']
                };

                service = new google.maps.places.PlacesService(map);
                service.nearbySearch(request, callback);

            }, function () {
                handleLocationError(true, infoWindow, Map.map.getCenter());
            });
        } else {
            // Browser doesn't support Geolocation
            handleLocationError(false, infoWindow, Map.map.getCenter());
        }
    };
    function callback(results, status) {
        if (status == google.maps.places.PlacesServiceStatus.OK) {
            for (var i = 0; i < results.length; i++) {
                var place = results[i];
                createMarker(results[i]);
            }
        }
    }
});

app.controller('MapController', ['$scope', '$http', 'Map', '$location', '$window','AuthService', '$cookies', '$filter',function($scope, $http, Map, $location, $window, AuthService, $cookies, $filter) {
    $scope.id = $location.search().id;
    $scope.isAuthenticated = AuthService.isAuthenticated;
    $scope.logout = AuthService.logout;
    $scope.PickedStore = false;


    $scope.createMarker = function(place) {
        var marker = new google.maps.Marker({
            map: map,
            position: place.geometry.location
        });

        google.maps.event.addListener(marker, 'mouseover', function() {
            var infowindow = new google.maps.InfoWindow({
                content: place.name
            });
            infowindow.open(map, marker);

            marker.addListener('mouseout', function() {
                infowindow.close();
            });
        });

        google.maps.event.addListener(marker, 'click', function() {
            $scope.$apply(function () {
                $scope.storeName = place.name;
                $scope.storeAddress = place.vicinity;
                $scope.lng = place.geometry.location.lng();
                $scope.lat = place.geometry.location.lat();
                $scope.PickedStore = true;
            });
            console.log($scope.storeName);
            console.log($scope.storeAddress);
            console.log($scope.lng);
            console.log($scope.lat);
        });

    }
    $scope.init = function() {

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                var pos = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };

                var MapOptions = {
                    center:  new google.maps.LatLng(pos.lat, pos.lng),
                    zoom: 15.3,
                    disableDefaultUI: false,
                    draggable: true,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };

                this.map = new google.maps.Map(document.getElementById("map"), MapOptions);

                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(pos.lat, pos.lng),
                    map: this.map,
                    icon: {url: "img/youarehere.png", scaledSize: new google.maps.Size(34, 34)}
                });

                var request = {
                    location: marker.position,
                    radius: 500,
                    type: ['electronics_store', 'school', 'library']
                };

                service = new google.maps.places.PlacesService(map);
                service.nearbySearch(request, $scope.callback);

            }, function () {
                handleLocationError(true, infoWindow, Map.map.getCenter());
            });
        } else {
            // Browser doesn't support Geolocation
            handleLocationError(false, infoWindow, Map.map.getCenter());
        }
    };
    $scope.callback = function(results, status) {
        if (status == google.maps.places.PlacesServiceStatus.OK) {
            for (var i = 0; i < results.length; i++) {
                var place = results[i];
                $scope.createMarker(results[i]);
            }
        }
    };


    $scope.getStoreId = function() {
        var req = {
            method: 'POST',
            url: '/observatory/api/myshops/',
            headers: {
                'Content-Type': 'application/json',
                'X-OBSERVATORY-AUTH': $cookies.get('token')
            },
            data: JSON.stringify({name: $scope.storeName, address: $scope.storeAddress, lat: $scope.lat, lng: $scope.lng, tags: '["Computing"]', withdrawn: false})
        };

        $http(req).
        success(function(data) {
            $scope.storeId = data.id;
        }).
        error(function(data, status) {
            $http.get('/observatory/api/myshops/findid?lng='+$scope.lng+"&lat="+$scope.lat).
            success(function(data) {
                $window.alert("Store already exists");
                $scope.storeId = data.id;
                console.log($scope.storeId);
            })
        });
    };


    $scope.SubmitPrice = function () {
        $scope.date = $filter('date')(new Date(), 'yyyy-MM-dd');

        var req = {
            method: 'POST',
            url: '/observatory/api/myprices/',
            headers: {
                'Content-Type': 'application/json',
                'X-OBSERVATORY-AUTH': $cookies.get('token')
            },
            data: JSON.stringify({price: $scope.price, productId: $location.search().id, storeId: $scope.storeId,dateTo: $scope.date, dateFrom: $scope.date})
        };

        $http(req).
        success(function(data) {
            $window.alert("Thank you");
            $window.open("/", '_self')

        }).
        error(function(data, status) {
            $window.alert("Something went wrong!");

        });
    };

    $scope.init()


}]);
