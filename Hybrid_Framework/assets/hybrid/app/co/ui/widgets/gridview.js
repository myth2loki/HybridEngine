/**
 * @file 列表组件
 */
(function() {
        var CLASS_ACTIVE = 'ui-active',
            CLASS_TABLE_VIEW_CELL = 'ui-table-view-cell',
            CLASS_BTN = 'ui-btn',
            CLASS_TOGGLE = 'ui-switch',
            CLASS_TABLE_VIEW = 'ui-table-view',
            CLASS_SCROLL_WRAPPER = 'ui-scroll-wrapper',
            CLASS_SCROLL = 'ui-scroll',

            tarEl;

        var loading = '<div class="ui-loading">'+
                    '<div class="ui-spinner"></div>'+
                    '</div>';     

        var render = function(){
            var _gv = this,opts = _gv.opts;
            $(opts.tpl.ul).appendTo( _gv.ref );
            _gv._ul = _gv.ref.find('ul.'+CLASS_TABLE_VIEW);
            _gv._lis = [];
            _gv.renderData(opts.data);
        };

         //绑定事件
        var bind = function(){
            var _gv = this,opts = _gv.opts,startY;
            _gv.ref.on( _gv.touchStart() , function(evt) {
                _gv.log('touchStart');
                var touch = evt.touches[0];
                startY = touch.pageY;
                _gv.log('touchStart -->'+startY);
                tarEl = false;
                var tar = evt.target;
                var ele = $(tar).closest('li.'+CLASS_TABLE_VIEW_CELL);
                if(ele.length == 0)return;
                if ((tar.tagName === 'INPUT' && tar.type !== 'radio' && tar.type !== 'checkbox') || tar.tagName === 'BUTTON') {
                    
                    return;
                }else if ((ele[0].querySelector('input') || ele[0].querySelector('.' +CLASS_BTN) || ele[0].querySelector('.' + CLASS_TOGGLE))) {
                     
                     return;
                }
                if (ele.hasClass(CLASS_TABLE_VIEW_CELL)) {
                    tarEl = ele;
                    ele.addClass(CLASS_ACTIVE);
                }
            }).on( _gv.touchMove() , function(evt) {
                if (tarEl) {
                    var touch = evt.touches[0];
                    if(touch.pageY != startY){
                        tarEl.removeClass(CLASS_ACTIVE);
                        tarEl = false;
                    }
                }
            }).on( _gv.touchEve() , function(evt) {
                _gv.log('tab');
                _gv.stopPropagation(evt);
                if (tarEl) {
                    tarEl.removeClass(CLASS_ACTIVE);
                    if ($.isFunction(_gv.callback)) {
                        _gv.callback.apply(_gv, [tarEl[0],evt]);
                    }
                }else{
                }
            }).on( _gv.longTap() , function(evt) {
                if (tarEl) {
                    tarEl.removeClass(CLASS_ACTIVE);
                    tarEl = false;
                }
            }).on( _gv.touchCancel() , function(evt) {
                if (tarEl) {
                    tarEl.removeClass(CLASS_ACTIVE);
                    tarEl = false;
                }
            })

        };

        var bingSwipe = function(swipe){
            var _gv = this,opts = _gv.opts;
            var swipeouts = _gv.ref.find('.swipeout');
            if(swipeouts.length>0){
                swipe.initSwipe(_gv.ref);
                swipeouts.find('.swipeout-delete').on(_gv.touchEve(),function(evt){
                   var tar = $(this);
                   swipe.swipeDelete(tar.parents('.swipeout'));
                   // alert(tar);
                   // if()
                });
                swipeouts.on('delete',function(e){
                    // return false;
                })
            }
        }

    define(function(require, exports, module) {
        var $ui = require("ui");

        //gridview
        var $gridview = $ui.define('Gridview',{
                /**
                 * 模板對象
                 * @type {function}
                 */
                 tpl : {
                    ul: '<ul class="'+CLASS_TABLE_VIEW+'" ></ul>',
                    li: '<li class="'+CLASS_TABLE_VIEW_CELL+'"><%=cont%></li>' 
                },
                /**
                 * 渲染數據
                 */
                 data: [],
                 iscroll:false
            });

        //初始化
        $gridview.prototype.init = function(){
            var _gv = this,opts = _gv.opts;
            render.call(_gv);
            bind.call(_gv);
            if(opts.iscroll){
                require.async('scroll', function() {
                    _gv.ref.scroll({
                            scrollbars: true,
                            interactiveScrollbars: true,
                            shrinkScrollbars: 'scale',
                            fadeScrollbars: true
                    });
                });
            }
            require.async('swipeout', function(swipe) {
                bingSwipe.call(_gv,swipe);
            });
        };
        /**
         * 根據傳入數據渲染
         * @type {function}
         * lis -> array
         */
        $gridview.prototype.renderData = function(lis){
            var _gv = this,opts = _gv.opts;
            if($.isArray(lis)&&lis.length>0){
                _gv._ul.empty();
                _gv._parseFn||(_gv._parseFn = _gv.parseTpl(opts.tpl.li));
                var _lis = [];
                if(lis.length>0){
                    $.each(lis, function(index, item){
                        _lis[index] = _gv._parseFn(item);
                    })
                    _gv._lis = $(_lis.join('')).appendTo( _gv._ul );
                    _gv._lis.attr('data-ui-gli',true);
                }
            }
            if(opts.iscroll){
                $(window).trigger('resize');
            }
            return _gv;
        };

        /**
         * 根據后台返回数据數據渲染
         * @type {function}
         * lis -> array
         */
        $gridview.prototype.ajax = function(uri){

            //_gv._loading = $(loading).appendTo(_gv.ref);
            //_gv._loading.hide();
        };

        /**
         * 根據傳入HTML附加到列表
         * @type {function}
         * lis -> array
         */
        $gridview.prototype.appendHtml = function(html){
            if(typeof html === 'string'){
                var _gv = this,opts = _gv.opts;
                var appLis = $(html).appendTo( _gv._ul );
                appLis.attr('data-ui-gli',true);
                //bind.call(_gv,appLis);
                _gv._lis = _gv._lis.concat(appLis);
            }else{
                _gv.log('html必須是字符串');
                throw new Error( 'html必須是字符串' );
            }
            return _gv;
        };

         /**
         * 根據傳入數據附加到列表
         * @type {function}
         * lis -> array
         */
        $gridview.prototype.appendData = function(lis){
            if($.isArray(lis)){
                var _gv = this,opts = _gv.opts;
                _gv._parseFn||(_gv._parseFn = _gv.parseTpl(opts.tpl.li));
                var _lis = [];
                if(lis.length>0){
                    $.each(lis, function(index, item){
                        _lis[index] = _gv._parseFn(item);
                    })
                    _lis = $(_lis.join('')).appendTo( _gv._ul );
                    _lis.attr('data-ui-gli',true);
                    //bind.call(_gv,_lis);
                    _gv._lis = _gv._lis.concat(_lis);
                }
            }else{
                this.log('lis必須是數組對象');
                throw new Error( 'lis必須是數組對象' );
            }
            return _gv;
        };

        //注册$插件
        $.fn.gridview = function (opts) {
            var gridviewObjs = [];
            opts|| (opts = {});
            this.each(function() {
                var gridviewObj = null;
                var id = this.getAttribute('data-gridview');
                if (!id) {
                    opts = $.extend(opts, { ref : this});
                    id = ++$ui.uuid;
                    gridviewObj = $ui.data[id] = new $gridview(opts);
                    this.setAttribute('data-gridview', id);
                } else {
                    gridviewObj = $ui.data[id];
                }
                gridviewObjs.push(gridviewObj);
            });
            return gridviewObjs.length > 1 ? gridviewObjs : gridviewObjs[0];
        };

        /*module.exports = function(opts){
            return new botton(opts);
        };
    */
    });
})();