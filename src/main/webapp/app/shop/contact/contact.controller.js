(function() {
    'use strict';

    angular
        .module('fauxshopApp')
        .controller('ContactController', ContactController);

    ContactController.$inject = ['$window', '$stateParams', '$state', '$scope', 'Auth', 'LoginService', 'CartService', 'MessageService', 'User', 'Principal'];

    function ContactController ($window, $stateParams, $state, $scope, Auth, LoginService, CartService, MessageService, User, Principal) {
        var vm = this;

        vm.saveMessage = saveMessage;
        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.cartInvoices = null;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            console.log('$window.localStorage.guestId: ' + $window.localStorage.guestId);
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
                if (vm.account != null){
                    getCartInvoices();
                } else {
                    getGuestCartInvoices();
                }
            });
        }

        function getGuestCartInvoices() {
            vm.cartInvoices = CartService.getCartByUserId($window.localStorage.guestId).get();
        }

    function register () {
        $state.go('register');
    }

    function getCartInvoices() {
        vm.cartInvoices = CartService.getCartByUserId(vm.account.id).get();
    }

    Auth.activateAccount({key: $stateParams.key}).then(function () {
        vm.error = null;
        vm.success = 'OK';
    }).catch(function () {
        vm.success = null;
        vm.error = 'ERROR';
    });

    function saveMessage(event) {
        MessageService.createMessage(
            JSON.stringify({type:"MessageDTO", name:$scope.name, email:$scope.email, message:$scope.message})
        )
    }


    }
})();
