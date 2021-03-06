'use strict';

describe('Controller Tests', function () {

    beforeEach(module('fauxshopApp'));

    describe('CheckoutController', function () {
        var $scope, authService;

        beforeEach(inject(function ($injector, $rootScope, $controller, Auth) {
            $scope = $rootScope.$new();
            authService = Auth;
            $controller('CheckoutController as vm',
                {
                    $scope: $scope,
                    Auth: authService,
                    $uibModalInstance: null
                });
        }));

        it('$scope should exist', function () {
            expect($scope).toBeDefined();
        });

        it('checkoutData should exist', function () {
            expect($scope.vm.checkoutData).toBeDefined();
        });
    });
});
