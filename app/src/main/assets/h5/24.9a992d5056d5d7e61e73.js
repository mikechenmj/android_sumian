webpackJsonp([24],{"70T8":function(t,e){},Q9Q8:function(t,e,i){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var n=i("mvHQ"),s=i.n(n),o=i("Zrlr"),a=i.n(o),r=i("wxAW"),c=i.n(r),u=i("BEQ0"),h=i.n(u),l=function(t){return Array.prototype.slice.call(t)},d=function(){function t(e){if(a()(this,t),this._default={container:".vux-swiper",item:".vux-swiper-item",direction:"vertical",activeClass:"active",threshold:50,duration:300,auto:!1,loop:!1,interval:3e3,height:"auto",minMovingDistance:0},this._options=h()(this._default,e),this._options.height=this._options.height.replace("px",""),this._start={},this._move={},this._end={},this._eventHandlers={},this._prev=this._current=this._goto=0,this._width=this._height=this._distance=0,this._offset=[],this.$box=this._options.container,this.$container=this._options.container.querySelector(".vux-swiper"),this.$items=this.$container.querySelectorAll(this._options.item),this.count=this.$items.length,this.realCount=this.$items.length,this._position=[],this._firstItemIndex=0,this.count)return this._init(),this._auto(),this._bind(),this._onResize(),this}return c()(t,[{key:"_auto",value:function(){var t=this;t.stop(),t._options.auto&&(t.timer=setTimeout(function(){t.next()},t._options.interval))}},{key:"updateItemWidth",value:function(){this._width=this.$box.offsetWidth||document.documentElement.offsetWidth,this._distance="horizontal"===this._options.direction?this._width:this._height}},{key:"stop",value:function(){this.timer&&clearTimeout(this.timer)}},{key:"_loop",value:function(){return this._options.loop&&this.realCount>=3}},{key:"_onResize",value:function(){var t=this;this.resizeHandler=function(){setTimeout(function(){t.updateItemWidth(),t._setOffset(),t._setTransform()},100)},window.addEventListener("orientationchange",this.resizeHandler,!1)}},{key:"_init",value:function(){this._height="auto"===this._options.height?"auto":this._options.height-0,this.updateItemWidth(),this._initPosition(),this._activate(this._current),this._setOffset(),this._setTransform(),this._loop()&&this._loopRender()}},{key:"_initPosition",value:function(){for(var t=0;t<this.realCount;t++)this._position.push(t)}},{key:"_movePosition",value:function(t){if(t>0){var e=this._position.splice(0,1);this._position.push(e[0])}else if(t<0){var i=this._position.pop();this._position.unshift(i)}}},{key:"_setOffset",value:function(){var t=this,e=t._position.indexOf(t._current);t._offset=[],l(t.$items).forEach(function(i,n){t._offset.push((n-e)*t._distance)})}},{key:"_setTransition",value:function(t){var e="none"===(t=t||this._options.duration||"none")?"none":t+"ms";l(this.$items).forEach(function(t,i){t.style.webkitTransition=e,t.style.transition=e})}},{key:"_setTransform",value:function(t){var e=this;t=t||0,l(e.$items).forEach(function(i,n){var s=e._offset[n]+t,o="translate3d("+s+"px, 0, 0)";"vertical"===e._options.direction&&(o="translate3d(0, "+s+"px, 0)"),i.style.webkitTransform=o,i.style.transform=o})}},{key:"_bind",value:function(){var t=this,e=this;e.touchstartHandler=function(t){e.stop(),e._start.x=t.changedTouches[0].pageX,e._start.y=t.changedTouches[0].pageY,e._setTransition("none")},e.touchmoveHandler=function(i){if(1!==e.count){e._move.x=i.changedTouches[0].pageX,e._move.y=i.changedTouches[0].pageY;var n=e._move.x-e._start.x,s=e._move.y-e._start.y,o=s,a=Math.abs(n)>Math.abs(s);"horizontal"===e._options.direction&&a&&(o=n),t._options.loop||t._current!==t.count-1&&0!==t._current||(o/=3),(e._options.minMovingDistance&&Math.abs(o)>=e._options.minMovingDistance||!e._options.minMovingDistance)&&a&&e._setTransform(o),a&&i.preventDefault()}},e.touchendHandler=function(t){if(1!==e.count){e._end.x=t.changedTouches[0].pageX,e._end.y=t.changedTouches[0].pageY;var i=e._end.y-e._start.y;"horizontal"===e._options.direction&&(i=e._end.x-e._start.x),0!==(i=e.getDistance(i))&&e._options.minMovingDistance&&Math.abs(i)<e._options.minMovingDistance||(i>e._options.threshold?e.move(-1):i<-e._options.threshold?e.move(1):e.move(0),e._loopRender())}},e.transitionEndHandler=function(t){e._activate(e._current);var i=e._eventHandlers.swiped;i&&i.apply(e,[e._prev%e.count,e._current%e.count]),e._auto(),e._loopRender(),t.preventDefault()},e.$container.addEventListener("touchstart",e.touchstartHandler,!1),e.$container.addEventListener("touchmove",e.touchmoveHandler,!1),e.$container.addEventListener("touchend",e.touchendHandler,!1),e.$items[1]&&e.$items[1].addEventListener("webkitTransitionEnd",e.transitionEndHandler,!1)}},{key:"_loopRender",value:function(){var t=this;t._loop()&&(0===t._offset[t._offset.length-1]?(t.$container.appendChild(t.$items[0]),t._loopEvent(1)):0===t._offset[0]&&(t.$container.insertBefore(t.$items[t.$items.length-1],t.$container.firstChild),t._loopEvent(-1)))}},{key:"_loopEvent",value:function(t){var e=this;e._itemDestoy(),e.$items=e.$container.querySelectorAll(e._options.item),e.$items[1]&&e.$items[1].addEventListener("webkitTransitionEnd",e.transitionEndHandler,!1),e._movePosition(t),e._setOffset(),e._setTransform()}},{key:"getDistance",value:function(t){return this._loop()?t:t>0&&0===this._current?0:t<0&&this._current===this.realCount-1?0:t}},{key:"_moveIndex",value:function(t){0!==t&&(this._prev=this._current,this._current+=this.realCount,this._current+=t,this._current%=this.realCount)}},{key:"_activate",value:function(t){var e=this._options.activeClass;Array.prototype.forEach.call(this.$items,function(i,n){i.classList.remove(e),t===Number(i.dataset.index)&&i.classList.add(e)})}},{key:"go",value:function(t){var e=this;return e.stop(),t=t||0,t+=this.realCount,t%=this.realCount,t=this._position.indexOf(t)-this._position.indexOf(this._current),e._moveIndex(t),e._setOffset(),e._setTransition(),e._setTransform(),e._auto(),this}},{key:"next",value:function(){return this.move(1),this}},{key:"move",value:function(t){return this.go(this._current+t),this}},{key:"on",value:function(t,e){return this._eventHandlers[t]&&console.error("[swiper] event "+t+" is already register"),"function"!=typeof e&&console.error("[swiper] parameter callback must be a function"),this._eventHandlers[t]=e,this}},{key:"_itemDestoy",value:function(){var t=this;this.$items.length&&l(this.$items).forEach(function(e){e.removeEventListener("webkitTransitionEnd",t.transitionEndHandler,!1)})}},{key:"destroy",value:function(){if(this.stop(),this._current=0,this._setTransform(0),window.removeEventListener("orientationchange",this.resizeHandler,!1),this.$container.removeEventListener("touchstart",this.touchstartHandler,!1),this.$container.removeEventListener("touchmove",this.touchmoveHandler,!1),this.$container.removeEventListener("touchend",this.touchendHandler,!1),this._itemDestoy(),this._options.loop&&2===this.count){var t=this.$container.querySelector(this._options.item+"-clone");t&&this.$container.removeChild(t),(t=this.$container.querySelector(this._options.item+"-clone"))&&this.$container.removeChild(t)}}}]),t}(),p=i("0FxO"),v=(Array,String,Boolean,Boolean,String,String,Boolean,Boolean,Number,Number,Number,String,Number,Number,Number,{name:"swiper",created:function(){this.index=this.value||0,this.index&&(this.current=this.index)},mounted:function(){var t=this;this.hasTwoLoopItem(),this.$nextTick(function(){t.list&&0===t.list.length||t.render(t.index),t.xheight=t.getHeight(),t.$emit("on-get-height",t.xheight)})},methods:{hasTwoLoopItem:function(){2===this.list.length&&this.loop?this.listTwoLoopItem=this.list:this.listTwoLoopItem=[]},clickListItem:function(t){Object(p.a)(t.url,this.$router),this.$emit("on-click-list-item",JSON.parse(s()(t)))},buildBackgroundUrl:function(t){return t.fallbackImg?"url("+t.img+"), url("+t.fallbackImg+")":"url("+t.img+")"},render:function(){var t=this,e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:0;this.swiper&&this.swiper.destroy(),this.swiper=new d({container:this.$el,direction:this.direction,auto:this.auto,loop:this.loop,interval:this.interval,threshold:this.threshold,duration:this.duration,height:this.height||this._height,minMovingDistance:this.minMovingDistance,imgList:this.imgList}).on("swiped",function(e,i){t.current=i%t.length,t.index=i%t.length}),e>0&&this.swiper.go(e)},rerender:function(){var t=this;this.$el&&!this.hasRender&&(this.hasRender=!0,this.hasTwoLoopItem(),this.$nextTick(function(){t.index=t.value||0,t.current=t.value||0,t.length=t.list.length||t.$children.length,t.destroy(),t.render(t.value)}))},destroy:function(){this.hasRender=!1,this.swiper&&this.swiper.destroy()},getHeight:function(){var t=parseInt(this.height,10);return t?this.height:t?void 0:this.aspectRatio?this.$el.offsetWidth*this.aspectRatio+"px":"180px"}},props:{list:{type:Array,default:function(){return[]}},direction:{type:String,default:"horizontal"},showDots:{type:Boolean,default:!0},showDescMask:{type:Boolean,default:!0},dotsPosition:{type:String,default:"right"},dotsClass:String,auto:Boolean,loop:Boolean,interval:{type:Number,default:3e3},threshold:{type:Number,default:50},duration:{type:Number,default:300},height:{type:String,default:"auto"},aspectRatio:Number,minMovingDistance:{type:Number,default:0},value:{type:Number,default:0}},data:function(){return{hasRender:!1,current:this.index||0,xheight:"auto",length:this.list.length,index:0,listTwoLoopItem:[]}},watch:{auto:function(t){t?this.swiper&&this.swiper._auto():this.swiper&&this.swiper.stop()},list:function(t){this.rerender()},current:function(t){this.index=t,this.$emit("on-index-change",t)},index:function(t){var e=this;t!==this.current&&this.$nextTick(function(){e.swiper&&e.swiper.go(t)}),this.$emit("input",t)},value:function(t){this.index=t}},beforeDestroy:function(){this.destroy()}}),f={render:function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"vux-slider"},[i("div",{staticClass:"vux-swiper",style:{height:t.xheight}},[t._t("default"),t._v(" "),t._l(t.list,function(e,n){return i("div",{staticClass:"vux-swiper-item",attrs:{"data-index":n},on:{click:function(i){t.clickListItem(e)}}},[i("a",{attrs:{href:"javascript:"}},[i("div",{staticClass:"vux-img",style:{backgroundImage:t.buildBackgroundUrl(e)}}),t._v(" "),t.showDescMask?i("p",{staticClass:"vux-swiper-desc"},[t._v(t._s(e.title))]):t._e()])])}),t._v(" "),t._l(t.listTwoLoopItem,function(e,n){return t.listTwoLoopItem.length>0?i("div",{staticClass:"vux-swiper-item vux-swiper-item-clone",attrs:{"data-index":n},on:{click:function(i){t.clickListItem(e)}}},[i("a",{attrs:{href:"javascript:"}},[i("div",{staticClass:"vux-img",style:{backgroundImage:t.buildBackgroundUrl(e)}}),t._v(" "),t.showDescMask?i("p",{staticClass:"vux-swiper-desc"},[t._v(t._s(e.title))]):t._e()])]):t._e()})],2),t._v(" "),i("div",{directives:[{name:"show",rawName:"v-show",value:t.showDots,expression:"showDots"}],class:[t.dotsClass,"vux-indicator","vux-indicator-"+t.dotsPosition]},t._l(t.length,function(e){return i("a",{attrs:{href:"javascript:"}},[i("i",{staticClass:"vux-icon-dot",class:{active:e-1===t.current}})])}))])},staticRenderFns:[]};var _=i("VU/8")(v,f,!1,function(t){i("ZSiT")},null,null).exports,m={render:function(){var t=this.$createElement;return(this._self._c||t)("div",{staticClass:"vux-swiper-item"},[this._t("default")],2)},staticRenderFns:[]},g=i("VU/8")({name:"swiper-item",mounted:function(){var t=this;this.$nextTick(function(){t.$parent.rerender()})},beforeDestroy:function(){var t=this.$parent;this.$nextTick(function(){t.rerender()})}},m,!1,null,null,null).exports,y=i("/AfO"),x=i("2sLL"),w=i("gyMJ"),$=i("mHYM"),k=i("o0rJ"),T=(y.a,x.a,{name:"relaxation-detaile",components:{Swiper:_,SwiperItem:g,XCircle:y.a,XButton:x.a},data:function(){return{relaxations:null,backgroundImage:"",statusType:"not",APlayer:null,duration:null,durationNum:null,iScancel:!0,setInterval:null,aplayer:null,firstPlay:!1,music:{src:"https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/cbti/relaxation/88f63f53-f1b9-4897-b28a-aa59860049a5.mp3"}}},computed:{percent:function(){return Math.abs(100-Math.round(this.duration/this.durationNum*1e4)/100)},currentTime:function(){var t=0|this.duration;return(t/60|0)+":"+this._pad(t%60)}},methods:{playSelect:function(){console.log(this.$refs),"ing"!==this.statusType?this.$refs.aplayer.play():this.$refs.aplayer.pause()},loadedmetadata:function(t){this.aplayer=t,this.duration=t.target.duration,this.durationNum=t.target.duration},_pad:function(t){for(var e=arguments.length>1&&void 0!==arguments[1]?arguments[1]:2,i=t.toString().length;i<e;)t="0"+t,i++;return t},cancel:function(){2===Object($.a)()&&Object(k.a)(function(t){t.callHandler("updatePageUI",{showNavigationBar:!0})}),this.$router.go(-1)},playing:function(t){var e=this;this.statusType="ing",null!==this.setInterval&&clearInterval(this.setInterval),this.setInterval=setInterval(function(){console.log(t),e.duration<0&&(e.statusType="not",e.duration=e.durationNum,console.log(e.statusType,e.duration),clearInterval(e.setInterval)),e.duration=t.target.duration-t.target.currentTime,console.log(1)},1e3),console.log(t,"开始播放")},pause:function(t){clearInterval(this.setInterval),t.target.currentTime===this.durationNum?(this.statusType="not",this.firstPlay=!1,this.duration=this.durationNum):this.firstPlay?(this.statusType="not",this.firstPlay=!1):this.statusType="suspend",console.log(t,"暂停播放")}},mounted:function(){var t=this;w.a.get("/cbti-relaxations/"+this.$route.params.id).then(function(e){var i=e.data;t.relaxations=i,t.music.src=i.audio,t.backgroundImage=i.background,t.$nextTick(function(){t.$refs.aplayer.play(),t.$refs.aplayer.pause(),t.$refs.aplayer.currentTime=0,t.firstPlay=!0})}).catch(function(e){t.$vux.toast.text(e.message)}),2!==Object($.a)()&&(this.iScancel=!1)},created:function(){2===Object($.a)()&&Object(k.a)(function(t){t.callHandler("updatePageUI",{showNavigationBar:!1})})}}),b={render:function(){var t=this,e=t.$createElement,i=t._self._c||e;return t.relaxations?i("div",{staticClass:"relaxation-detaile",style:{backgroundImage:"url("+t.backgroundImage+")"}},[t.iScancel?i("svg-icon",{staticClass:"cancel",attrs:{"icon-class":"nav_icon_cancel"},nativeOn:{click:function(e){return t.cancel(e)}}}):t._e(),t._v(" "),i("swiper",{attrs:{height:"100vh","dots-position":"center"}},[i("swiper-item",{staticClass:"item"},[i("div",{staticClass:"name"},[t._v(t._s(t.relaxations.name))]),t._v(" "),i("div",{staticClass:"circle-progress"},[i("x-circle",{staticStyle:{width:"4.02rem",height:"4.02rem"},attrs:{percent:t.percent,"stroke-width":1.5,"trail-width":.8,anticlockwise:!1,"stroke-color":"#6595F4","trail-color":"#ffffff"}}),t._v(" "),i("div",{staticClass:"circle-num"},[t._v(t._s(t.currentTime))])],1),t._v(" "),i("div",{staticClass:"audio-setting"},["not"===t.statusType||"ing"===t.statusType?i("div",{staticClass:"suspend",class:{active:"ing"===t.statusType},on:{click:t.playSelect}},[t._v(t._s("not"===t.statusType?"开始放松":"暂停")+"\n        ")]):t._e(),t._v(" "),"suspend"===t.statusType?i("div",{staticClass:"play",on:{click:t.playSelect}},[t._v("继续")]):t._e()]),t._v(" "),i("audio",{ref:"aplayer",attrs:{src:t.music.src},on:{loadedmetadata:t.loadedmetadata,playing:t.playing,pause:t.pause}})]),t._v(" "),i("swiper-item",{staticClass:"item"},[i("div",{staticClass:"name"},[t._v(t._s(t.relaxations.name))]),t._v(" "),i("div",{staticClass:"description"},[t._v(t._s(t.relaxations.description))])])],1)],1):t._e()},staticRenderFns:[]};var I=i("VU/8")(T,b,!1,function(t){i("70T8")},null,null);e.default=I.exports},ZSiT:function(t,e){}});