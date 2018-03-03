/**
 * @file 弹出框组件
 */
(function() {
     var CLASS_MASK = 'ui-mask',
         CLASS_DIALOG_TITLE = 'ui-dialog-title',
         CLASS_DIALOG = 'ui-dialog',
         CLASS_DIALOG_CONTENT = 'ui-dialog-content',
         CLASS_DIALOG_BTNS = 'ui-dialog-btns',
         CLASS_BTN = 'ui-btn',
         CLASS_DIALOG_CONTAINER = 'ui-dialog-container',
         CLASS_STATE_HOVER = 'ui-state-hover',

         SELECTOR_DIALOG_CONTENT = '.'+CLASS_DIALOG_CONTENT,
         SELECTOR_DIALOG_BTNS = '.'+CLASS_DIALOG_BTNS+' .'+CLASS_BTN,


        tpl = {
            mask: '<div class="'+CLASS_MASK+'"></div>',
            title: '<div class="'+CLASS_DIALOG_TITLE+'"></div>',
            wrap: '<div class="'+CLASS_DIALOG+'">'+
                '<div class="'+CLASS_DIALOG_CONTENT+'"></div>'+
                '<% if(btns){ %>'+
                '<div class="'+CLASS_DIALOG_BTNS+'">'+
                '<% for(var i=0, length=btns.length; i<length; i++){var item = btns[i]; %>'+
                '<a class="'+CLASS_BTN+'"  data-btn = <%=item.index%> data-key="<%=item.key%>"><%=item.text%></a>'+
                '<% } %>'+
                '</div>'+
                '<% } %>' +
                '</div> '
        };


    //渲染组件
    var render = function(){
            var _dog = this, opts = _dog.opts, btns,i= 0,vars = {};
            _dog._container = $(opts.container || document.body);
            (_dog._cIsBody = _dog._container.is('body')) || _dog._container.addClass(CLASS_DIALOG_CONTAINER);
            vars.btns = btns= [];
            opts.buttons && $.each(opts.buttons, function(key){
                btns.push({
                    index: ++i,
                    text: key,
                    key: key
                });
            });
            _dog._mask = opts.mask ? $(tpl.mask).appendTo(_dog._container) : null;
            _dog._wrap = $(_dog.parseTpl(tpl.wrap, vars)).appendTo(_dog._container);
            _dog._content = $(SELECTOR_DIALOG_CONTENT, _dog._wrap);

            _dog._title = $(tpl.title);
            title.call(_dog,opts.title);
            content.call(_dog,opts.content);

            btns.length && $(SELECTOR_DIALOG_BTNS, _dog._wrap).highlight(CLASS_STATE_HOVER);
            _dog._wrap.css({
                width: opts.width,
                height: opts.height
            });
        };
    /**
         * 设置弹出框标题
         * @method title
         * @param {String} [value] 弹出框标题
         */
    var title = function(value) {
            var _dog = this, setter = value!==undefined;
            value = '<h3>'+value+'</h3>';
            setter && _dog._title.html(value).prependTo(_dog._wrap);
        };

        /**
         * 设置弹出框内容。value接受带html标签字符串和zepto对象。
         * @method content
         * @param {String|Element} [val] 弹出框内容
         */
    var content = function(val) {
            var _dog = this,opts = _dog.opts, setter = val!==undefined;
            setter && _dog._content.empty().append(opts.content = val);
        };    
     //绑定事件
    var bind = function(){
            var _dog = this, match, wrap, opts = _dog.opts;
                //bind events绑定事件
            _dog._wrap.on(_dog.touchEve(), function(evt){
                    var ele = evt.target;
                    wrap = _dog._wrap.get(0);
                    if( (match = $(ele).closest(SELECTOR_DIALOG_BTNS, wrap)) && match.length ) {
                        fn = opts.buttons[match.attr('data-key')];
                        fn && fn.apply(_dog, [ele,evt]);
                    }
            });
            _dog._mask && _dog._mask.on(_dog.touchEve(), function(evt){
                    var ele = evt.currentTarget;
                    if ($.isFunction(opts.maskClick)) {
                        opts.maskClick.apply(_dog, [ele,evt]);
                    }
            });
        };


     var tmove = function(e){
                var _dog = this, opts = _dog.opts;
                opts.scrollMove && e.preventDefault();
        };  



    var calculate = function(){
            var _dog = this, opts = _dog.opts, size, $win, root = document.body,
                ret = {}, isBody = _dog._cIsBody, round = Math.round;

            opts.mask && (ret.mask = isBody ? {
                width:  '100%',
                height: Math.max(root.scrollHeight, root.clientHeight)-1//不减1的话uc浏览器再旋转的时候不触发resize.奇葩！
            }:{
                width: '100%',
                height: '100%'
            });

            size = _dog._wrap.offset();
            $win = $(window);
            ret.wrap = {
                left: '50%',
                marginLeft: -round(size.width/2) +'px',
                top: isBody?round($win.height() / 2) + window.pageYOffset:'50%',
                marginTop: -round(size.height/2) +'px'
            }
            return ret;
        };  


        /**
         * @desc 销毁组件。
         * @name destroy
         */
    var destroy = function(){
            var _dog = this, opts = _dog.opts;
            $(document).off('touchmove',tmove);
            _dog._wrap.off().remove();
            _dog._mask && _dog._mask.off().remove();
        };   


    /**
     * 弹出框组件
     *
     */

    define(function(require, exports, module) {
        var $ui = require("ui");

        //pop
        var $dialog_plus = $ui.define('Dialog_Plus',{
                /**
                 * @property {Array} [buttons=null] 弹出框上的按钮
                 * @namespace options
                 */
                buttons: null,
                /**
                 * @property {Boolean} [mask=true] 是否有遮罩层
                 * @namespace options
                 */
                mask: true,
                /**
                 * @property {Number} [width=300] 弹出框宽度
                 * @namespace options
                 */
                width: 300,
                /**
                 * @property {Number|String} [height='auto'] 弹出框高度
                 * @namespace options
                 */
                height: 'auto',
                /**
                 * @property {String} [title=null] 弹出框标题
                 * @namespace options
                 */
                title: '提示框',
                /**
                 * @property {String} [content=null] 弹出框内容
                 * @namespace options
                 */
                content: null,
                /**
                 * @property {Boolean} [scrollMove=true] 是否禁用掉scroll，在弹出的时候
                 * @namespace options
                 */
                scrollMove: true,
                /**
                 * @property {Element} [container=null] 弹出框容器
                 * @namespace options
                 */
                container: null
            });

        //初始化
        $dialog_plus.prototype.init = function(){
            var _dog = this, opts = _dog.opts, btns,i= 0,vars = {};

            render.call(_dog);
            bind.call(_dog);
            _dog.open();
        };
        /**
         * 弹出弹出框
         * @method open
         * @param {String|Number} [x] X轴位置
         * @param {String|Number} [y] Y轴位置
         * @return {self} 返回本身
         */
        $dialog_plus.prototype.open = function(x, y){
            var _dog = this,opts = _dog.opts;
            _dog._isOpen = true;

            _dog._wrap.css('display', 'block');
            _dog._mask && _dog._mask.css('display', 'block');

            _dog.refresh();

            $(document).on('touchmove', $.proxy(tmove, _dog));
        };

        /**
         * 用来更新弹出框位置和mask大小。如父容器大小发生变化时，可能弹出框位置不对，可以外部调用refresh来修正。
         * @method refresh
         * @return {self} 返回本身
         */
        $dialog_plus.prototype.refresh = function(){
            var _dog = this, opts = _dog.opts, ret, action;
            if(_dog._isOpen) {

                action = function(){
                    ret = calculate.call(_dog);
                    ret.mask && _dog._mask.css(ret.mask);
                    _dog._wrap.css(ret.wrap);
                }

                //如果有键盘在，需要多加延时
                if( $.os.ios &&
                    document.activeElement &&
                    /input|textarea|select/i.test(document.activeElement.tagName)){

                    document.body.scrollLeft = 0;
                    setTimeout(action, 200);//do it later in 200ms.

                } else {
                    action();//do it now
                }
            }
        };

        /**
         * 关闭弹出框
         * @method close
         * @return {self} 返回本身
         */
        $dialog_plus.prototype.close = function(){
            var _dog = this, opts = _dog.opts;


            _dog._isOpen = false;
            _dog._wrap.css('display', 'none');
            _dog._mask && _dog._mask.css('display', 'none');
            destroy.call(_dog);
        };
        
        /*$.fn.dialog = function (opts) {
            opts = $.extend(opts, { ref : this[0] });
            return new $dialog_plus(opts);
        };*/

        module.exports = function(opts){
            opts|| (opts = {});
            return new $dialog_plus(opts);
        };

    });

})();
