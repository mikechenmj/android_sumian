webpackJsonp([61],{RYRn:function(t,s){},RcQE:function(t,s,i){"use strict";Object.defineProperty(s,"__esModule",{value:!0});var o=i("f4/6"),a=i("rHil"),e=i("1DHf"),n=i("32ER"),c=i("NYxO"),r=(o.a,a.a,e.a,n.a,Object(c.b)({profile:function(t){return t.user.profile}}),{name:"mine",components:{Tabbar:o.a,Group:a.a,Cell:e.a,CellBox:n.a},computed:Object(c.b)({profile:function(t){return t.user.profile}}),methods:{gotoModifyName:function(){this.$router.push("/modify-name")}},created:function(){var t=this;this.$store.dispatch("GetUserProfile").catch(function(){t.$vux.toast.text("获取用户信息失败")})}}),l={render:function(){var t=this,s=t.$createElement,o=t._self._c||s;return o("div",{attrs:{id:"mine"}},[o("tabbar",[o("div",{staticClass:"harder"},[o("div",{staticClass:"profile"},[o("router-link",{attrs:{to:"/profile"}},[o("img",{attrs:{src:t.profile.avatar?t.profile.avatar:i("1LRf"),alt:""}})]),t._v(" "),o("p",[o("router-link",{staticClass:"nick-name",attrs:{to:"/profile"}},[t._v(t._s(t.profile.nickname))])],1)],1)]),t._v(" "),o("group",[o("cell",{attrs:{title:"我的服务"}}),t._v(" "),o("cell-box",[o("div",{staticClass:"my-service"},[o("router-link",{attrs:{to:"/mine/record"}},[o("svg-icon",{staticClass:"icon",attrs:{"icon-class":"info_icon_graphic"}}),t._v(" "),o("p",[t._v("图文咨询")])],1),t._v(" "),o("router-link",{attrs:{to:"/bookings"}},[o("svg-icon",{staticClass:"icon",attrs:{"icon-class":"info_icon_telephone"}}),t._v(" "),o("p",[t._v("电话预约")])],1),t._v(" "),o("router-link",{attrs:{to:"/weekly-assess/lists"}},[o("svg-icon",{staticClass:"icon",attrs:{"icon-class":"img_sleepdiary"}}),t._v(" "),o("p",[t._v("日记评估")])],1)],1)])],1),t._v(" "),o("group",[o("cell",{attrs:{title:"我的档案"}}),t._v(" "),o("cell-box",[o("div",{staticClass:"my-archives"},[o("router-link",{attrs:{to:"/mine/medical"}},[o("svg-icon",{staticClass:"icon",attrs:{"icon-class":"info_icon_anamnesis"}}),t._v(" "),o("p",[t._v("睡眠档案")])],1),t._v(" "),o("router-link",{attrs:{to:"/new-scale-list?swiperIndex=1"}},[o("svg-icon",{staticClass:"icon",attrs:{"icon-class":"info_icon_text"}}),t._v(" "),o("p",[t._v("我的测评")])],1),t._v(" "),o("router-link",{attrs:{to:"/mine/report"}},[o("svg-icon",{staticClass:"icon",attrs:{"icon-class":"info_icon_report"}}),t._v(" "),o("p",[t._v("电子报告")])],1)],1)])],1),t._v(" "),o("group",[o("cell",{staticClass:"cell-setting",attrs:{title:"兑换中心","is-link":"",link:{path:"/exchange-core"}}},[o("div",{attrs:{slot:"icon"},slot:"icon"},[o("svg-icon",{staticClass:"icon_svg",attrs:{"icon-class":"info_icon_set_copy"}})],1)])],1),t._v(" "),o("group",[o("cell",{staticClass:"cell-setting",attrs:{title:"设置","is-link":"",link:{path:"/setting"}}},[o("div",{attrs:{slot:"icon"},slot:"icon"},[o("svg-icon",{staticClass:"icon_svg",attrs:{"icon-class":"info_icon_set"}})],1)])],1)],1)],1)},staticRenderFns:[]};var v=i("VU/8")(r,l,!1,function(t){i("RYRn")},null,null);s.default=v.exports}});