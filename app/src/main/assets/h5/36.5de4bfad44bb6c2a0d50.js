webpackJsonp([36],{ZyXP:function(t,e,s){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=s("aQR8"),a=s("rHil"),n=s("32ER"),r=s("odqc"),o=s("Znkm"),c=s("gyMJ"),d=(a.a,n.a,r.a,o.a,{name:"record",components:{Group:a.a,CellBox:n.a,Tab:r.a,TabItem:o.a},data:function(){return{swiperIndex:0,contentHeight:"",contentTop:"",advisoriesNo:[],advisoriesYes:[],endTime:[],setIntervalArray:[],pagingNo:{page:1,per_page:15},pagingYes:{page:1,per_page:15}}},methods:{refresh:function(t){0===this.swiperIndex?this.getAdvisories("advisoriesNo",t):this.getAdvisories("advisoriesYes",t)},infinite:function(t){console.log("infinite"),0===this.swiperIndex?(this.pagingNo.per_page=this.pagingNo.per_page+15,this.getAdvisories("advisoriesNo",t)):(this.pagingYes.per_page=this.pagingYes.per_page+15,this.getAdvisories("advisoriesYes",t))},getAdvisories:function(t,e){var s=this,a={params:{type:0,page:this.pagingNo.page,per_page:this.pagingNo.per_page,include:"package.servicePackage"}};return"advisoriesYes"===t&&(a.params.type=1,a.params.page=this.pagingYes.page,a.params.per_page=this.pagingYes.per_page),console.log(a),c.a.get("/advisories",a).then(function(a){s[t]=a.data.data,s[t].map(function(t,e){t.recorded_at=s.$moment.unix(t.recorded_at).format("YYYY.MM.DD HH:mm:ss"),t.status=i.a[t.status].text,t.endTime=t.end_at-s.$moment().unix(),"已回复"===t.status&&t.endTime>0&&t.endTime<18e3?(clearInterval(s.setIntervalArray[e]),s.setIntervalArray[e]=setInterval(function(){if(t.endTime<=0)return t.endTime=null,s.$set(s.endTime,e,null),void clearInterval(s.setIntervalArray[e]);t.endTime--;var i=s.$moment.duration(1e3*t.endTime),a=(i.get("hours")>10?i.get("hours"):"0"+i.get("hours"))+":"+(i.get("minutes")>10?i.get("minutes"):"0"+i.get("minutes"))+":"+(i.get("seconds")>10?i.get("seconds"):"0"+i.get("seconds"));s.$set(s.endTime,e,a)},1e3)):s.endTime.push(null)}),e&&e(!0)})}},mounted:function(){var t=this,e=this.$refs.tab;this.contentTop=e.$el.clientHeight+"px",this.contentHeight=i.c.height-e.$el.clientHeight+"px",this.getAdvisories("advisoriesNo").then(function(e){t.getAdvisories("advisoriesYes")}),document.documentElement.scrollTop=document.body.scrollTop=0},created:function(){}}),l={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{attrs:{id:"record"}},[s("tab",{ref:"tab",attrs:{"line-width":3,"custom-bar-width":"100px","active-color":"#6595F4"},model:{value:t.swiperIndex,callback:function(e){t.swiperIndex=e},expression:"swiperIndex"}},[s("tab-item",{staticClass:"vux-center"},[t._v("未完成")]),t._v(" "),s("tab-item",{staticClass:"vux-center"},[t._v("已完成")])],1),t._v(" "),0===t.swiperIndex?s("div",{staticClass:"not-used"},[s("scroller",{staticStyle:{width:"100%"},style:{height:t.contentHeight,top:t.contentTop},attrs:{"on-refresh":t.refresh,"on-infinite":t.infinite,noDataText:""}},[s("div",{staticClass:"scroller-box"},[s("div",{staticClass:"data"},t._l(t.advisoriesNo,function(e,i){return s("div",{key:e.id,staticClass:"bor-bt"},[s("router-link",{staticClass:"data-list",attrs:{to:{path:("未使用"===e.status?"/sketch-advice/matter-filling/":"/advisories/")+e.id}}},[s("p",{staticClass:"title"},[t._v(t._s(e.description))]),t._v(" "),s("p",{staticClass:"time"},[t._v(t._s(e.recorded_at))]),t._v(" "),s("div",{staticClass:"type"},[t._v(t._s(e.status))])]),t._v(" "),t.endTime[i]?s("div",{staticClass:"end-time"},[t._v("\n              该订单还有\n              "),s("svg-icon",{staticStyle:{margin:"0 .1rem"},attrs:{"icon-class":"graphic_icon_timing"}}),t._v(" "),s("span",[t._v(t._s(t.endTime[i])+" ")]),t._v("过期，请及时回复。\n            ")],1):t._e()],1)}))])]),t._v(" "),0===t.advisoriesNo.length?s("div",{staticClass:"no-data",style:{height:t.contentHeight}},[s("svg-icon",{staticClass:"no-data-icon",attrs:{"icon-class":"emptystate_img_advisory"}}),t._v(" "),s("p",{staticClass:"title"},[t._v("暂无咨询记录")]),t._v(" "),s("p",{staticClass:"decs"},[t._v("绑定医生后，可向主治医生咨询")])],1):t._e()],1):t._e(),t._v(" "),1===t.swiperIndex?s("div",{staticClass:"already-used"},[s("scroller",{staticStyle:{width:"100%"},style:{height:t.contentHeight,top:t.contentTop},attrs:{"on-refresh":t.refresh,"on-infinite":t.infinite,noDataText:""}},[s("div",{staticClass:"scroller-box"},[s("div",{staticClass:"data"},t._l(t.advisoriesYes,function(e){return s("router-link",{key:e.id,staticClass:"data-list",attrs:{to:{path:"/advisories/"+e.id}}},["已完成"===e.status&&0===e.start_at?s("p",{staticClass:"title"},[t._v("您的【图文咨询服务（"+t._s(e.package.servicePackage.name)+"）】已取消。")]):s("p",{staticClass:"title"},[t._v(t._s(e.description))]),t._v(" "),s("p",{staticClass:"time"},[t._v(t._s(e.recorded_at))]),t._v(" "),s("div",{staticClass:"type"},[t._v(t._s(e.status))])])}))])]),t._v(" "),0===t.advisoriesYes.length?s("div",{staticClass:"no-data",style:{height:t.contentHeight}},[s("svg-icon",{staticClass:"no-data-icon",attrs:{"icon-class":"emptystate_img_advisory"}}),t._v(" "),s("p",{staticClass:"title"},[t._v("暂无咨询记录")]),t._v(" "),s("p",{staticClass:"decs"},[t._v("绑定医生后，可向主治医生咨询")])],1):t._e()],1):t._e()],1)},staticRenderFns:[]};var v=s("VU/8")(d,l,!1,function(t){s("zLrS")},null,null);e.default=v.exports},zLrS:function(t,e){}});