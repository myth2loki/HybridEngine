/**
 * @file 左右滑动手势插件
 */

(function () {
    var durationThreshold = 1000, // 时间大于1s就不算。
        horizontalDistanceThreshold = 30, // x方向必须大于30
        verticalDistanceThreshold = 70, // y方向上只要大于70就不算
        scrollSupressionThreshold = 30, //如果x方向移动大于这个直就禁掉滚动
        tabs = [],
        eventBinded = false,
        isFromTabs = function (target) {
            for (var i = tabs.length; i--;) {
                if ($.contains(tabs[i], target)) return true;
            }
            return false;
        };

    var tabsSwipeEvents =function() {
        var _tb = this, opts = _tb.opts;
        
        _tb._content.on('touchstart', function (e) {
            var point = e.touches ? e.touches[0] : e, start, stop;

            start = {
                x:point.clientX,
                y:point.clientY,
                time:Date.now(),
                el:$(e.target)
            }

            _tb._content.on('touchmove',function (e) {
                var point = e.touches ? e.touches[0] : e, xDelta;
                if (!start)return;
                stop = {
                    x:point.clientX,
                    y:point.clientY,
                    time:Date.now()
                }
                if ((xDelta = Math.abs(start.x - stop.x)) > scrollSupressionThreshold ||
                    xDelta > Math.abs(start.y - stop.y)) {
                    isFromTabs(e.target) && e.preventDefault();
                } else {//如果系统滚动开始了，就不触发swipe事件
                    _tb._content.off('touchmove touchend');
                }
            }).one('touchend', function () {
                    _tb._content.off('touchmove');
                    if (start && stop) {
                        if (stop.time - start.time < durationThreshold &&
                            Math.abs(start.x - stop.x) > horizontalDistanceThreshold &&
                            Math.abs(start.y - stop.y) < verticalDistanceThreshold) {
                            start.el.trigger(start.x > stop.x ? "tabsSwipeLeft" : "tabsSwipeRight");
                        }
                    }
                    start = stop = undefined;
                });
        });
    };
    
    /**
     * 添加 swipe功能
     */
    define(function(require, exports, module) {
        UI = require("UI");


        var tabsSinit = function () {
            var _tb = this, opts = _tb.opts;

            _tb.ref.on( 'readydom', function(){
                tabs.push(_tb._content.get(0));
                eventBinded =  eventBinded || (tabsSwipeEvents.call(_tb), true);
                _tb._content.on('tabsSwipeLeft tabsSwipeRight', $.proxy(_tb.eventHandler, _tb));
            } );
        };
        tabsSinit.eventHandler = function (e) {
            var _tb = this, opts = _tb.opts, items, index;
            switch (e.type) {
                case 'tabsSwipeLeft':
                case 'tabsSwipeRight':
                    items = opts.items;
                    if (e.type == 'tabsSwipeLeft' && opts.active < items.length - 1) {
                        index = opts.active + 1;
                    } else if (e.type == 'tabsSwipeRight' && opts.active > 0) {
                        index = opts.active - 1;
                    }
                    index !== undefined && (e.stopPropagation(), _tb.switchTo(index));
                    break;
                default://tap
                    return _tb;
            }
        };
        tabsSinit.destroy = function(){
           
        };

        UI.Tabs.prototype.extend.call(UI.Tabs,tabsSinit);
        UI.Tabs.prototype.plugins.push(tabsSinit);
    } );
})();