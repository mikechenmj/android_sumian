webpackJsonp([28],{euPt:function(t,e){},xnL1:function(t,e,s){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var r=s("rHil"),i=s("1DHf"),a=s("32ER"),c=(a.a,r.a,i.a,{name:"introduce",components:{CellBox:a.a,Group:r.a,Cell:i.a},data:function(){return{playerOptions:{muted:!1,sources:[{type:"video/mp4",src:this.$route.query.video_url}],poster:this.$route.query.picture},showMake:!0,purchaseShow:!1,chapters:JSON.parse(this.$route.query.chapters)}},methods:{onPlayerPlay:function(){this.showMake=!1}},created:function(){}}),l={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"introduce"},[s("div",{staticClass:"video",staticStyle:{width:"100vw"}},[s("video-player",{staticClass:"vjs-big-play-centere vjs-custom-skin",staticStyle:{width:"7.5rem",height:"4.22rem"},attrs:{options:t.playerOptions},on:{play:function(e){t.onPlayerPlay(e)}}}),t._v(" "),t.showMake?s("div",{staticClass:"make"}):t._e()],1),t._v(" "),s("group",[s("cell",{attrs:{title:"课程介绍"}}),t._v(" "),s("cell-box",[s("div",{domProps:{innerHTML:t._s(t.$route.query.description)}})])],1),t._v(" "),s("group",{staticClass:"outline"},[s("cell",{attrs:{title:"课程大纲"}}),t._v(" "),s("cell-box",t._l(t.chapters,function(e){return t.chapters?s("p",{key:e.name},[t._v(t._s(e))]):t._e()}))],1),t._v(" "),t._m(0)],1)},staticRenderFns:[function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"circle_process"},[e("div",{staticClass:"wrapper right"},[e("div",{staticClass:"circle rightcircle"})]),this._v(" "),e("div",{staticClass:"wrapper left"},[e("div",{staticClass:"circle leftcircle",attrs:{id:"leftcircle"}})])])}]};var n=s("VU/8")(c,l,!1,function(t){s("euPt")},null,null);e.default=n.exports}});
//# sourceMappingURL=28.edfde5f18b747291e77f.js.map