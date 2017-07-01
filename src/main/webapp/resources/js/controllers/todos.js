var module = angular.module("todo", []);

function todoController ($scope, $http, todosSrv) {
    $scope.todos;
    $scope.totalPages;
    $scope.column = 'username';
    $scope.currentPage = 1;

    todosSrv.loadTodos(0, $scope.column).then(function (result) {
        $scope.todos = result.data;
    });

    $http.get("/totalPages").then(function(response) {
        if(response.data === 0) {
            $scope.totalPages = 1;
        } else {
            $scope.totalPages = response.data;
        }
    });

    $scope.nextPage = function () {
        if($scope.currentPage === $scope.totalPages) {
            return;
        }
        $scope.currentPage += 1;
        todosSrv.loadTodos($scope.currentPage - 1, $scope.column).then(function (result) {
            $scope.todos = result.data;
        });
    }

    $scope.previousPage = function () {
        if($scope.currentPage === 1) {
            return;
        }
        $scope.currentPage -= 1;
        todosSrv.loadTodos($scope.currentPage - 1, $scope.column).then(function (result) {
            $scope.todos = result.data;
        });
    }

    $scope.sortTodos = function(column) {
        if($scope.currentPage === 1) {
            return;
        }
        $scope.column = column;
        todosSrv.loadTodos($scope.currentPage - 1, $scope.column).then(function (result) {
            $scope.todos = result.data;
        });
    }

}

module.service('todosSrv', function($http) {
    this.loadTodos = function (index, column) {
        return $http.get("/todoes/" + index + "/column/" + column);
    };
    this.totalPages = function () {
        return $http.get("/totalPages");
    };
});

var controller = module.controller("todoController", todoController);