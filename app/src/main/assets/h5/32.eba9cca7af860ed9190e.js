webpackJsonp([32],{IhsR:function(t,e){},"T+/8":function(t,e,i){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s=i("mvHQ"),n=i.n(s),a=i("pDNl"),o=i("2sLL"),c=i("gyMJ"),r=i("2CHa"),u=(a.a,o.a,{data:function(){return{showPage:!1,isLogin:!1,requestingCaptcha:!1,showCount:!1,currentTime:90,mobile:"",captcha:"",agreeTerms:!0}},computed:{captchaBtnDisabled:function(){return this.showCount},loginBtnDisabled:function(){return!this.mobile||!this.captcha||!this.agreeTerms}},components:{XInput:a.a,XButton:o.a},created:function(){var t=this,e=this.$route.query;e.unionid||e.openid?c.a.post("authorizations/socialite-bound",{type:1,union_id:e.unionid,openid:e.openid,nickname:e.nickname}).then(function(e){var i=e.data;t.$store.commit("SET_TOKEN",i.token),t.$store.commit("SET_PROFILE",i.user),Object(r.c)(i.token),t.redirect()}).catch(function(){t.showPage=!0}):this.showPage=!0},methods:{tick:function(){var t=this;this.interval=setInterval(function(){t.currentTime>0?t.currentTime--:t.stop()},1e3)},stop:function(){this.currentTime=90,this.showCount=!1,clearInterval(this.interval)},toggle:function(){this.agreeTerms=!this.agreeTerms},redirect:function(){var t=this,e=this.$route.query.from?this.$route.query.from:"/";this.$store.dispatch("GetUserProfile").then(function(){t.$router.push({path:e})})},loginClick:function(){var t=this;if(this.mobile)if(this.captcha){this.isLogin=!0;var e=this.$route.query,i={path:"authorizations",params:{mobile:this.mobile,captcha:this.captcha}};(e.unionid||e.openid)&&(i.path="authorizations/socialite-bind",i.params.type=1,i.params.info=n()({unionid:e.unionid,openid:e.openid,nickname:e.nickname})),this.$store.dispatch("Login",i).then(function(e){console.log(e),e.data.is_new?t.$router.push({name:"guide",query:{from:"newUser"}}):t.redirect(),t.isLogin=!1}).catch(function(e){console.log("少时诵诗书"),t.isLogin=!1,t.$vux.toast.text(e.message,"middle")})}else this.$vux.toast.text("请输入验证码","middle");else this.$vux.toast.text("请输入手机号","middle")},requestCaptcha:function(){var t=this;this.mobile?c.a.post("captcha",{mobile:this.mobile}).then(function(e){t.$vux.toast.text("验证码已发送","middle"),t.showCount=!0,t.tick()}).catch(function(e){e&&e.error?t.$vux.toast.text(e.error.internal_message,"middle"):t.$vux.toast.text(e.message,"middle")}):this.$vux.toast.text("请输入手机号","middle")}}}),l={render:function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{directives:[{name:"show",rawName:"v-show",value:t.showPage,expression:"showPage"}],staticClass:"login-page"},[t._m(0),t._v(" "),i("div",{staticClass:"mobile-section"},[i("x-input",{staticClass:"input",staticStyle:{"padding-right":"0"},attrs:{type:"tel",placeholder:"请输入手机号"},model:{value:t.mobile,callback:function(e){t.mobile=e},expression:"mobile"}})],1),t._v(" "),i("div",{staticClass:"captcha-section"},[i("x-input",{staticClass:"input",staticStyle:{"padding-right":"0"},attrs:{placeholder:"请输入验证码"},model:{value:t.captcha,callback:function(e){t.captcha=e},expression:"captcha"}}),t._v(" "),i("x-button",{staticClass:"captcha-btn",attrs:{type:"primary",disabled:t.captchaBtnDisabled},nativeOn:{click:function(e){return t.requestCaptcha(e)}}},[t._v(t._s(t.showCount?t.currentTime+" s":"获取验证码"))])],1),t._v(" "),i("div",{staticClass:"login-section"},[i("x-button",{attrs:{type:"primary"},nativeOn:{click:function(e){return t.loginClick(e)}}},[t._v("登录")])],1),t._v(" "),i("div",{staticClass:"terms-section"},[i("span",{staticClass:"check-icon",on:{click:t.toggle}},[t.agreeTerms?i("svg-icon",{attrs:{"icon-class":"check"}}):i("svg-icon",{attrs:{"icon-class":"uncheck"}})],1),t._v(" "),i("router-link",{attrs:{to:"/user-agreement"}},[t._v("睡眠医生用户协议")]),t._v(" "),i("span",[t._v("及")]),t._v(" "),i("router-link",{attrs:{to:"/privacy-policy"}},[t._v("用户隐私政策")])],1)])},staticRenderFns:[function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"welcome-section"},[e("div",{staticClass:"hello"},[this._v("您好，")]),this._v(" "),e("div",[this._v("欢迎使用速眠医生，请登录")])])}]};var h=i("VU/8")(u,l,!1,function(t){i("IhsR")},null,null);e.default=h.exports}});