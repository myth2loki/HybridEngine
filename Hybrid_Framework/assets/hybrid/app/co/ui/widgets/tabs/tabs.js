/**
 * @file 选项卡组件
 */

(function() {
    var CLASS_CONTENT = 'ui-content',
        CLASS_ACTIVE = 'ui-active',
        CLASS_BAR = 'ui-bar',
        CLASS_BAR_TAB = 'ui-bar-tab',
        CLASS_TAB_ITEM = 'ui-tab-item',
        CLASS_SCROLL_WRAPPER = 'ui-scroll-wrapper',
        CLASS_CONTROL_CONTENT = 'ui-control-content';

    var SELECTOR_ACTIVE = '.'+CLASS_ACTIVE,
        SELECTOR_SCROLL_WRAPPER = '.'+CLASS_SCROLL_WRAPPER;

        _uid = 1,
        uid = function(){
            return _uid++;
        },
        idRE = /^#(.+)$/;

    var render = function(){
            var _tb = this, opts = _tb.opts,items;

            _tb._nav =  _tb.ref.find('nav').first();
            if(_tb._nav) {
                _tb._content = $('<div></div>').appendTo(_tb.ref).addClass(CLASS_CONTENT);
                items = [];
                _tb._nav.children().each(function(){
                    var $a = $(this), href = $a?$a.attr('href'):_tb.callZ(this).attr('data-url'), id, $content;
                    id = idRE.test(href)? RegExp.$1: 'tabs_'+uid();
                    ($content = _tb.ref.find('#'+id) || $('<div id="'+id+'"></div>'))
                        .addClass(opts.transition?' '+opts.transition:'')
                        .appendTo(_tb._content);
                    items.push({
                        id: id,
                        href: href,
                        title: $a?$a.attr('href', 'javascript:;').text():_tb.callZ(this).text(),//如果href不删除的话，地址栏会出现，然后一会又消失。
                        content: $content
                    });
                });
                opts.items = items;
                opts.active = Math.max(0, Math.min(items.length-1, opts.active || _tb.callZ(SELECTOR_ACTIVE, _tb.ref).index()||0));
                getPanel.call(_tb).add(_tb._nav.children().eq(opts.active)).addClass(CLASS_ACTIVE);
                items[opts.active].actived = true;
            } 
            fitToContent.call(_tb,getPanel.call(_tb));
        };

    var bind = function(){
            var _tb = this, opts = _tb.opts, handler = $.proxy(eventHandler, _tb);
            
            _tb._nav.on(_tb.touchEve(), handler);
        };

    var getPanel = function(index){
            var _tb = this, opts = _tb.opts; 
            return $('#' + opts.items[index === undefined ? opts.active : index].id);
        };

    var fitToContent = function(div) {
            var _tb = this, opts = _tb.opts, $content = _tb._content;
            _tb._plus === undefined && (_tb._plus = parseFloat($content.css('border-top-width'))+parseFloat($content.css('border-bottom-width')))
            $content.height('100%');
        };

    var eventHandler = function (e) {
            var _tb = this, opts = _tb.opts;
            if((match = $(e.target).closest('a', _tb._nav)) && match.length) {
                e.preventDefault();
                _tb.switchTo(match.index());
            }
                    
        };

    /**
     * 选项卡组件
     */
    define(function(require, exports, module) {
        var $ui = require("ui");
        var $tabs = $ui.define('Tabs',{

            /**
             * @property {Number} [active=0] 初始时哪个为选中状态
             * @namespace options
             */
            active: 0,

            /**
             * @property {Array} [items=null] 
             */
            items:null,

            /**
             * @property {String} [transition='slide'] 设置切换动画，目前只支持slide动画，或无动画
             * @namespace options
             */
            transition: ''
         });
        
        //初始化
        $tabs.prototype.init = function () {
            render.call(this);
            bind.call(this);
        };
        /**
         * 切换到某个Tab
         * @method switchTo
         * @param {Number} index Tab编号
         * @chainable
         * @return {self} 返回本身。
         */
        $tabs.prototype.switchTo = function(index) {
            var _tb = this, opts = _tb.opts, items = opts.items, eventData, to, from, reverse, endEvent;
            if(!_tb._buzy && opts.active != (index = Math.max(0, Math.min(items.length-1, index)))) {
                to = $.extend({}, items[index]);//copy it.
                to.div = getPanel.call(_tb,index);
                to.index = index;

                from = $.extend({}, items[opts.active]);
                from.div = getPanel.call(_tb);
                from.index = opts.active;

                var eventStatus = _tb.ref.trigger('beforeActivate',[to,from])
                if(!eventStatus) return _tb;

                _tb._content.children().removeClass(CLASS_ACTIVE);
                to.div.addClass(CLASS_ACTIVE);
                _tb._nav.children().removeClass(CLASS_ACTIVE).eq(to.index).addClass(CLASS_ACTIVE);
                if(opts.transition) { 
                    _tb._buzy = true;
                    endEvent = $.fx.animationEnd + '.tabs';
                    reverse = index>opts.active?'':' reverse';
                    from.div.addClass('out'+reverse);
                    to.div.addClass('in'+reverse).on(endEvent, function(e){
                        if (e.target != e.currentTarget) return //如果是冒泡上来的，则不操作
                        to.div.off(endEvent, arguments.callee);//解除绑定
                        _tb._buzy = false;
                        from.div.removeClass('out reverse');
                        to.div.removeClass('in reverse');
                        _tb.ref.trigger('animateComplete',[to,from]);
                        fitToContent.call(_tb,to.div);
                    });
                }
                opts.active = index;
                if(!items[opts.active].actived){
                    items[opts.active].actived = true;
                    _tb.ref.trigger('activate',[to,from]);
                    opts.iscroll = _tb.ref.find(SELECTOR_SCROLL_WRAPPER).length>0;
                }

                opts.iscroll&&$(window).trigger('resize');
                _tb.ref.trigger('afteractivate',[to,from]);
                opts.transition ||  fitToContent.call(_tb,to.div);
            }
            return _tb;
        };

        /**
         * 当外部修改tabs内容好，需要调用refresh让tabs自动更新高度
         * @method refresh
         * @chainable
         * @return {self} 返回本身。
         */
        $tabs.prototype.refresh = function(){
            return fitToContent.call(this,getPanel.call(this));
        };

        /**
         * 销毁组件
         * @method destroy
         */
        $tabs.prototype.destroy = function () {
           
        };
        //注册$插件
        $.fn.tab = function(opts) {
            var tabObjs = [];
            opts|| (opts = {});
            this.each(function() {
                var tabObj = null;
                var id = this.getAttribute('data-tab');
                if (!id) {
                    opts = $.extend(opts, { ref : this});
                    id = ++$ui.uuid;
                    tabObj = $ui.data[id] = new $tabs(opts);
                    this.setAttribute('data-tab', id);
                } else {
                    tabObj = $ui.data[id];
                }
                tabObjs.push(tabObj);
            });
            return tabObjs.length > 1 ? tabObjs : tabObjs[0];
        };

    });
})();