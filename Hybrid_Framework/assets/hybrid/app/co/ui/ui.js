define(function(require, exports, module) {
	var $ui = {},Base = {},$N = window.rd;
   /*
        判断是否存在原生插件对象
    */
  Base.isPlus = !!$N;

  Base.eachObj = function( obj, iterator ) {
        obj && Object.keys( obj ).forEach(function( key ) {
            iterator( key, obj[ key ] );
        });
  };
  Base.init = function(){};
  /**
  * @name extend
  * @desc 扩充现有组件
  */
  Base.extend = function( obj ) {
      var proto = this.prototype;
      Base.eachObj( obj, function( key, val ) {
          proto[ key ] = val;
      } );
      return this;
  };

  Base.parseTpl = function(str, data){
    var tmpl = 'var __p=[];' + 'with(obj||{}){__p.push(\'' +
                str.replace( /\\/g, '\\\\' )
                .replace( /'/g, '\\\'' )
                .replace( /<%=([\s\S]+?)%>/g, function( match, code ) {
                    return '\',' + code.replace( /\\'/, '\'' ) + ',\'';
                } )
                .replace( /<%([\s\S]+?)%>/g, function( match, code ) {
                    return '\');' + code.replace( /\\'/, '\'' )
                            .replace( /[\r\n\t]/g, ' ' ) + '__p.push(\'';
                } )
                .replace( /\r/g, '\\r' )
                .replace( /\n/g, '\\n' )
                .replace( /\t/g, '\\t' ) +
                '\');}return __p.join("");',

            func = new Function( 'obj', tmpl );
    
        return data ? func( data ) : func;
  };

  /*
        判断是否Touch屏幕
    */
  Base.isTouchScreen = function(){
        return (('ontouchstart' in window) || window.DocumentTouch && document instanceof DocumentTouch);
  };

  Base.touchEve = function(str, data){
    return this.isTouchScreen()? "tap" : "mousedown"
  };

  Base.touchStart = function(str, data){
    return this.isTouchScreen()? "touchstart" : "mousedown"
  };

  Base.touchEnd = function(str, data){
    return this.isTouchScreen()? "touchend" : "mouseup"
  };

  Base.touchCancel = function(str, data){
    return this.isTouchScreen()? "touchcancel" : "mouseup"
  };

  Base.touchMove = function(str, data){
    return this.isTouchScreen()? "touchmove" : "mouseup"
  };

  Base.longTap = function(str, data){
    return this.isTouchScreen()? "longTap" : "mouseup"
  };

  Base.touchOver = function(str, data){
    return this.isTouchScreen()? "touchend touchmove" : "mouseup"
  };


  Base.log = function(str){
    console.log(str);
    return this;
  };
  Base.callZ = (function() {
            instance = $();
            instance.length = 1;

        return function( item) {
            instance[ 0 ] = item;
            return instance;
        };
    })()

  Base.stopPropagation = function(e) {
        e.stopPropagation();
        return this;
  };

  Base.preventDefault = function(e) {
        e.preventDefault();
        return this;
  };
    
 
  Base.focus = function(element) {
        if ($.os.ios) {
            setTimeout(function() {
                element.focus();
            }, 10);
        } else {
            element.focus();
        }
        return this;
    };  
  Base.back = function(id,ottions) {
        id = id||'root';
       $local.Win.backWin(id,ottions);
    }; 
  Base.openWin = function(url,id,options,type) {
       $local.Win.openWin(url,id,options,type);
    };   

  $ui.define = function( name, options) {
        if($ui[ name ])return $ui[ name ];
         var defOpts =  {
                /**
                 * 参照对象
                 * @property {String} [ref=null]
                 */
                ref     : null,    //参照目标 

                /**
                 * 点击回调函数
                 * @type {function}
                 */
                 callback: null
         }
        var klass = function(opts) {
            var baseOpts = $.extend(true,{},this.options);
            this.opts = $.extend(true,baseOpts, opts); 
            this.ref = $(this.opts.ref);
            this.callback = this.opts.callback;
            this.init($N);
        }
        $ui[ name ] = Base.extend.call(klass,Base);
        $ui[ name ].prototype.options = $.extend(defOpts, options); 
        return $ui[ name ];
    };
    $ui.uuid = 0;
    $ui.data = {};
    //exports
    module.exports = $ui;

});