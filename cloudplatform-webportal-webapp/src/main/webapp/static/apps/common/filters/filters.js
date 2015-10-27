/**
 * Created by jiangfei on 2015/8/18.
 */
define(['./common.filter'], function (filterModule) {

  filterModule.filter('propsFilter', function () {
    return function (items, props) {
      var out = [];

      if (angular.isArray(items)) {
        items.forEach(function (item) {
          var itemMatches = false;

          var keys = Object.keys(props);
          for (var i = 0; i < keys.length; i++) {
            var prop = keys[i];
            var text = props[prop].toLowerCase();
            if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
              itemMatches = true;
              break;
            }
          }

          if (itemMatches) {
            out.push(item);
          }
        });
      } else {
        // Let the output be the input untouched
        out = items;
      }

      return out;
    }
  });

  filterModule.filter('mclusterTypeFilter', function () {
    return function (input) {
      var out = '';
      if (input) {
        out = '后台创建';
      } else {
        out = '系统创建';
      }
      return out;
    }
  });

  filterModule.filter('vmStatusFilter', ['Config', function (Config) {
    var allStatuses = Config.vmStatuses;
    return function (input) {
      var out = '';
      for (var i = 0, leng = allStatuses.length; i < leng; i++) {
        if (allStatuses[i].value == input) {
          out = allStatuses[i].text;
          break;
        }
      }
      return out || '未知';
    }
  }]);

  filterModule.filter('diskStatusFilter', ['Config', function (Config) {
    var allDiskStatuses = Config.vmDiskStatuses;
    return function (input) {
      var out = '';
      for (var i = 0, leng = allDiskStatuses.length; i < leng; i++) {
        if (allDiskStatuses[i].value == input) {
          out = allDiskStatuses[i].text;
          break;
        }
      }
      return out || '未知';
    }
  }]);

  filterModule.filter('vpcStatusFilter', ['Config', function (Config) {
    var allStatuses = Config.vmVpcStatuses;
    return function (input) {
      var out = '';
      for (var i = 0, leng = allStatuses.length; i < leng; i++) {
        if (allStatuses[i].value == input) {
          out = allStatuses[i].text;
          break;
        }
      }
      return out || '未知';
    }
  }]);

  filterModule.filter('vpcSubnetsFilter', ['Config', function (Config) {
    return function (input) {
      return input.map(function(subnet){return subnet.name;}).join(', ') || '未设置';
    }
  }]);

  filterModule.filter('buyPeriodFilter', [ function () {
    return function (period,isSelected) {
      var out = null;
      if (period <= 9) {
        out = isSelected ? period + '个月' : period;
      }
      else {
        out = parseInt(period / 12) + '年';
      }
      return out;
    }

  }]);

  filterModule.filter('publicNetworkGatewayFilter',[ function () {
    return function (input) {
      var out = '';
      if(input == 'true'){
        out = '开启'
      }
      else{
        out = '关闭'
      }
      return out;
    }
  }]);

  filterModule.filter('vmFlavorFilter', [ function () {
    return function (flavor) {
      return [flavor.vcpus+'核',Math.ceil(flavor.ram/1024)+'G',flavor.disk+'G'].join('/');
    }

  }]);

  filterModule.filter('sideMenuUrlFilter', [function () {
    /*return function (route, parentRoute) {
      var out = '';
      if (parentRoute != null) {//subroute
        out = ['/', parentRoute.url, '/#/', route.url].join('');
      }
      else {
        if (route.abstract) {
          out = 'javascript:void(0);';
        }
        else {
          out = '/' + route.url;
        }
      }
      return out || '未知';
    }*/
    return function (route) {
      var out = '';
      if (route.abstract) {
        out = 'javascript:void(0);';
      }
      else {
        out = route.isSpaUrl ? '#' + route.url : route.url;
      }
      return out || '未知';
    }
  }]);
  filterModule.filter('vmFloatIpFilter',['Config',function(Config){
    var allIpStatuses = Config.vmFloatIpStatuses;
    return function (input) {
      var out = '';
      out=allIpStatuses[input]
      return out || '未知';
    }
  }]);
  filterModule.filter('routerSubnetFilter',[function(){
    return function (input) {
      var out = '';
      if(input[0] == null){
        return '--'
      }else{
        for(var i= 0,len = input.length;i<len;i++){
          if(out !== ''){
            out = out +','+ input[i].name
          }else{
            out = input[i].name
          }
        }
        return out;
      }
    }
  }]);
  filterModule.filter('routerStatusFilter',[function(){
    return function (input) {
      var out = '';
      if(input === 'ACTIVE'){
        out='活跃';
      }else if(input === 'DOWN'){
        out = '关闭';
      }else if(input === 'BUILD'){
        out = '构建中';
      }else if(input === 'ERROR'){
        out = '错误';
      }else if(input === 'UNRECOGNIZED'){
        out = '未识别';
      }else{
        out = '未知';
      }
      return out;
    }
  }]);
});