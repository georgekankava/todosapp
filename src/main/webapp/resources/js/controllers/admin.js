var module = angular.module("todo", []);

function admController ($scope, adminSrv) {

    $scope.currentPage = 1;
    $scope.column = 'username';
    adminSrv.loadTodos(0, $scope.column).then(function (result) {
        $scope.todos = result.data;
    });
    $scope.update = function(index) {
        // console.log($scope.todos[index]);
        adminSrv.update($scope.todos[index]);
    };
    adminSrv.totalPages().then(function (result) {
        if(result.data === 0) {
            $scope.totalPages = 1;
        } else {
            $scope.totalPages = result.data;
        }
    });
    $scope.nextPage = function () {
        if($scope.currentPage === $scope.totalPages) {
            return;
        }
        $scope.currentPage += 1;
        adminSrv.loadTodos($scope.currentPage - 1, $scope.column).then(function (result) {
            $scope.todos = result.data;
        });
    }
    $scope.previousPage = function () {
        if($scope.currentPage === 1) {
            return;
        }
        $scope.currentPage -= 1;
        adminSrv.loadTodos($scope.currentPage - 1, $scope.column).then(function (result) {
            $scope.todos = result.data;
        });
    }
    $scope.sortTodos = function(column) {
        $scope.column = column;
        adminSrv.loadTodos($scope.currentPage - 1, $scope.column).then(function (result) {
            $scope.todos = result.data;
        });
    }
}


module.service('adminSrv', function($http) {
    this.update = function (todo) {
        $http.post("/updateTodo", todo );
    };
    this.loadTodos = function (index, column) {
        return $http.get("/todoes/" + index + "/column/" + column);
    };
    this.totalPages = function () {
        return $http.get("/totalPages");
    };
});

var controller = module.controller("admController", admController);