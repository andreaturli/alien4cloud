/* global UTILS */

'use strict';

var alienApp = angular.module('alienUiApp');

alienApp.filter('bytes', function() {
  return function(bytes, precision) {
    if(UTILS.isUndefinedOrNull(bytes)) {
      return undefined;
    }
    if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) {
      return '-';
    }
    if (typeof precision === 'undefined') {
      precision = 1;
    }
    var units = ['B', 'KIB', 'MIB', 'GIB', 'TIB', 'PIB'],
      number = Math.floor(Math.log(bytes) / Math.log(1024));
    return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) + ' ' + units[number];
  };
});
