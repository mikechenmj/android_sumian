webpackJsonp([26],{ML8m:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=a("taJy"),n=a("gyMJ"),r=a("o0rJ"),s=a("mHYM"),o=(i.a,{data:function(){for(var t=this.$store.state.user.profile,e=Object(s.a)(),a=[],i=new Date,n=i.getFullYear(),r=i.getMonth()+1,o=1920;o<=n;o++)if(a.push({parent:"0",name:o.toString(),value:o.toString()}),o!==n)for(var c=1;c<=12;c++)a.push({parent:o.toString(),name:c.toString()<10?"0"+c.toString():c.toString(),value:c.toString()<10?"0"+c.toString():c.toString()});for(var u=1;u<=r;u++)a.push({parent:n.toString(),name:u.toString()<10?"0"+u.toString():u.toString(),value:u.toString()<10?"0"+u.toString():u.toString()});var l=void 0;l=t.birthday&&"未设置"!==t.birthday?t.birthday.split("-"):["1980","01"];var h=[];return t.education?h.push(t.education):h=["大专"],{query:{},birthdayList:a,birthday:l,educationList:[{name:"本科或以上",value:"本科或以上"},{name:"大专",value:"大专"},{name:"高中",value:"高中"},{name:"初中或以下",value:"初中或以下"},{name:"其它",value:"其它"}],education:h,env:e}},components:{Picker:i.a},methods:{finish:function(){var t=this,e={birthday:this.birthday.join("-"),education:this.education[0]};n.a.patch("user/profile",e).then(function(a){var i=a.data;t.$store.commit("SET_PROFILE",i),t.$router.push({path:"/perfect-info-step3?theme="+t.theme,query:e})}).catch(function(e){2===t.env?Object(r.a)(function(t){t.callHandler("showToast",{type:"text",message:e.message,duration:1e3})}):t.$vux.toast.text(e.message)})},change:function(t){console.log(t)}},created:function(){this.query=this.$route.query,this.theme=this.$route.query.theme||"white"}}),c={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"perfect_info_page2",class:{black:"black"===t.theme}},[a("div",{staticClass:"label"},[t._v("出生年月")]),t._v(" "),a("div",{staticClass:"picker_wrapper"},[a("picker",{staticClass:"picker sd-picker",attrs:{data:t.birthdayList,columns:2},on:{"on-change":t.change},model:{value:t.birthday,callback:function(e){t.birthday=e},expression:"birthday"}})],1),t._v(" "),a("div",{staticClass:"label"},[t._v("教育程度")]),t._v(" "),a("div",{staticClass:"picker_wrapper",staticStyle:{"margin-bottom":"1.4rem"}},[a("picker",{staticClass:"picker sd-picker",attrs:{data:t.educationList,columns:2},on:{"on-change":t.change},model:{value:t.education,callback:function(e){t.education=e},expression:"education"}})],1),t._v(" "),a("div",{staticClass:"finish",on:{click:t.finish}},[t._v("下一步")])])},staticRenderFns:[]};var u=a("VU/8")(o,c,!1,function(t){a("m4AQ")},null,null);e.default=u.exports},m4AQ:function(t,e){}});