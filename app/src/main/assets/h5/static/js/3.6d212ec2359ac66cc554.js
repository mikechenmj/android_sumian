webpackJsonp([3],{"+QBg":function(e,t,n){var a=n("O/nt"),r={}.hasOwnProperty;e.exports={JDPAY_WAP_URL_OLD:"https://m.jdpay.com/wepay/web/pay",JDPAY_H5_URL:"https://h5pay.jd.com/jdpay/saveOrder",JDPAY_PC_URL:"https://wepay.jd.com/jdpay/saveOrder",handleCharge:function(e){var t=e.credential[e.channel],n=this.JDPAY_H5_URL;r.call(t,"channelUrl")?(n=t.channelUrl,delete t.channelUrl):r.call(t,"merchantRemark")&&(n=this.JDPAY_WAP_URL_OLD),a.formSubmit(n,"post",t)}}},"+l/b":function(e,t,n){var a=n("qaAm"),r=n("O/nt"),i=n("6ylr"),l=n("xTW6"),c={}.hasOwnProperty;e.exports={PINGPP_NOTIFY_URL_BASE:"https://notify.pingxx.com/notify",handleCharge:function(e){for(var t=e.credential[e.channel],n=["appId","timeStamp","nonceStr","package","signType","paySign"],r=0;r<n.length;r++)if(!c.call(t,n[r]))return void a.innerCallback("fail",a.error("invalid_credential","missing_field_"+n[r]));i.jsApiParameters=t,this.callpay()},callpay:function(){var e=this,t=l.getExtraModule("wx_jssdk");if(void 0!==t&&t.jssdkEnabled())t.callpay();else if("undefined"==typeof WeixinJSBridge){var n=function(){e.jsApiCall()};document.addEventListener?document.addEventListener("WeixinJSBridgeReady",n,!1):document.attachEvent&&(document.attachEvent("WeixinJSBridgeReady",n),document.attachEvent("onWeixinJSBridgeReady",n))}else this.jsApiCall()},jsApiCall:function(){c.call(i,"jsApiParameters")&&WeixinJSBridge.invoke("getBrandWCPayRequest",i.jsApiParameters,function(e){delete i.jsApiParameters,"get_brand_wcpay_request:ok"==e.err_msg?a.innerCallback("success"):"get_brand_wcpay_request:cancel"==e.err_msg?a.innerCallback("cancel"):a.innerCallback("fail",a.error("wx_result_fail",e.err_msg))})},runTestMode:function(e){if(confirm("模拟付款？")){var t="/charges/"+e.id;r.request(this.PINGPP_NOTIFY_URL_BASE+t+"?livemode=false","GET",null,function(e,t){if(t>=200&&t<400&&"success"==e)a.innerCallback("success");else{var n="http_code:"+t+";response:"+e;a.innerCallback("fail",a.error("testmode_notify_fail",n))}},function(){a.innerCallback("fail",a.error("network_err"))})}}}},"/pZk":function(e,t,n){var a=n("O/nt"),r={}.hasOwnProperty;e.exports={CMB_WALLET_URL:"https://netpay.cmbchina.com/netpayment/BaseHttp.dll?MB_EUserPay",handleCharge:function(e){var t=e.credential[e.channel],n=this.CMB_WALLET_URL;r.call(t,"ChannelUrl")&&(n=t.ChannelUrl,delete t.ChannelUrl),r.call(t,"channelVersion")&&delete t.channelVersion,a.formSubmit(n,"post",t)}}},"2Yrm":function(e,t,n){var a=n("O/nt"),r=n("qaAm"),i={}.hasOwnProperty;e.exports={YEEPAY_WAP_URL:"https://ok.yeepay.com/paymobile/api/pay/request",YEEPAY_WAP_TEST_URL:"http://mobiletest.yeepay.com/paymobile/api/pay/request",handleCharge:function(e){for(var t,n=e.channel,l=e.credential[n],c=["merchantaccount","encryptkey","data"],o=0;o<c.length;o++)if(!i.call(l,c[o]))return void r.innerCallback("fail",r.error("invalid_credential","missing_field_"+c[o]));t=i.call(l,"mode")&&"test"==l.mode?this.YEEPAY_WAP_TEST_URL:this.YEEPAY_WAP_URL,a.redirectTo(t+"?"+a.stringifyData(l,n,!0),e.channel)}}},"6ylr":function(e,t){e.exports={}},"8aS2":function(e,t,n){var a=n("O/nt"),r=n("xTW6"),i={}.hasOwnProperty;e.exports={ALIPAY_WAP_URL_OLD:"https://wappaygw.alipay.com/service/rest.htm",ALIPAY_WAP_URL:"https://mapi.alipay.com/gateway.do",handleCharge:function(e){var t=e.channel,n=e.credential[t],l=this.ALIPAY_WAP_URL;i.call(n,"req_data")?l=this.ALIPAY_WAP_URL_OLD:i.call(n,"channel_url")&&(l=n.channel_url),i.call(n,"_input_charset")||(i.call(n,"service")&&"alipay.wap.create.direct.pay.by.user"===n.service||i.call(n,"req_data"))&&(n._input_charset="utf-8");var c=l+"?"+a.stringifyData(n,t,!0),o=r.getExtraModule("ap");a.inWeixin()&&void 0!==o?o.pay(c):a.redirectTo(c,t)}}},"940V":function(e,t,n){var a=n("6ylr"),r=n("qaAm"),i={}.hasOwnProperty;e.exports={PINGPP_NOTIFY_URL_BASE:"https://notify.pingxx.com/notify",handleCharge:function(e){for(var t=e.credential[e.channel],n=["appId","timeStamp","nonceStr","package","signType","paySign"],l=0;l<n.length;l++)if(!i.call(t,n[l]))return void r.innerCallback("fail",r.error("invalid_credential","missing_field_"+n[l]));a.jsApiParameters=t,this.callpay()},wxLiteEnabled:function(){return"undefined"!=typeof wx&&wx.requestPayment},callpay:function(){if(this.wxLiteEnabled()){var e=a.jsApiParameters;delete e.appId,e.complete=function(e){"requestPayment:ok"===e.errMsg&&r.innerCallback("success"),"requestPayment:cancel"===e.errMsg&&r.innerCallback("cancel",r.error("用户取消支付")),"undefined"!==e.err_code&&"undefined"!==e.err_desc&&r.innerCallback("fail",r.error(e.err_desc,e))},wx.requestPayment(e)}else console.log("请在微信小程序中打开")},runTestMode:function(e){var t="/charges/"+e.id;wx.request({url:this.PINGPP_NOTIFY_URL_BASE+t+"?livemode=false",success:function(e){"success"==e.data?r.innerCallback("success"):r.innerCallback("fail",r.error("testmode_notify_fail"))},fail:function(){r.innerCallback("fail",r.error("network_err"))}})}}},"9RAi":function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=n("2sLL"),r=n("/kga"),i=n("gyMJ"),l=n("9q2/"),c=n.n(l),o=n("Xe5u"),s=(a.a,r.a,{components:{XButton:a.a,XDialog:r.a},data:function(){var e=JSON.parse(sessionStorage.getItem("selected_service"));console.log(e);var t={name:"",price:0,days:0,picture:"#"};return console.log(e,"selectedService"),e&&e.id&&(t.packages=e.packages[0],t.id=e.id,t.description=e.description,t.package_id=e.packages[0].id,t.name=e.name,t.price=e.packages[0].unit_price/100,t.days=e.packages[0].days,t.icon=e.icon,t.type=e.type),console.log(t,"支付页数据"),{payLabel:"支付",defaultNum:1,isPay:!0,show:!1,payTip:"",service:t}},watch:{defaultNum:function(e,t){null!==e&&(/^\d*$/.test(e)?/^0/.test(e)&&(this.defaultNum=0):this.defaultNum=0),this.isPay=this.defaultNum>0}},created:function(){Object(o.a)()},methods:{decrease:function(){this.defaultNum<1&&"number"==typeof this.defaultNum?this.defaultNum=0:this.defaultNum-=1},add:function(){this.defaultNum<8&&(this.defaultNum=Number(this.defaultNum)+1)},getLatest:function(){return i.a.get("/advisory/latest/").then(function(e){return e.data})},getOrders:function(){return i.a.post("/orders",{amount:100*this.service.price*this.defaultNum,channel:"wx_pub",currency:"cny",subject:this.service.name,body:this.service.description,package_id:this.service.packages.id,quantity:this.defaultNum,open_id:this.$route.query.openid}).then(function(e){return e.data})},onPay:function(){if(this.service.id)if(WeixinJSBridge){var e=this;this.getOrders().then(function(t){c.a.createPayment(t,function(t,n){console.log(t),console.log(n.msg),console.log(n.extra),"success"===t?0===e.service.type?e.$router.push("/my-doctor/"):1===e.service.type?e.getLatest().then(function(t){e.$router.push("/sketch-advice/matter-filling/"+t.id)}):3===e.service.type&&e.$router.push("/cbti"):"fail"===t?(e.show=!0,e.payTip="支付异常，请稍后重试"):"cancel"===t&&(e.show=!0,e.payTip="支付取消")})})}else this.$vux.toast.text("请在公众号中访问");else this.$vux.toast.text("请选择服务")}}}),d={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"pay-page"},[n("section",[n("div",{staticClass:"doc_service bg_color_white"},[n("a",{staticClass:"box_shadow"},[n("img",{staticClass:"service_img",attrs:{src:e.service.icon}})]),e._v(" "),n("div",{staticClass:"service-cont"},[n("h4",[e._v(e._s(e.service.name))]),e._v(" "),n("div",{staticClass:"service_price"},[n("span",[e._v("服务费用:")]),e._v(" "),n("span",{staticClass:"font_color_red"},[e._v(e._s(e.service.price))]),e._v(" "),0===e.service.type?n("span",[e._v("元/"+e._s(e.service.days)+"天")]):e._e(),e._v(" "),1===e.service.type?n("span",[e._v("元/次")]):e._e()])])])]),e._v(" "),n("section",{staticClass:"item bg_color_white",staticStyle:{padding:".28rem .4rem"}},[n("span",{staticStyle:{"line-height":".7rem"}},[e._v("购买数量")]),e._v(" "),n("div",{staticClass:"inline_block"},[n("span",{staticClass:"inline_block action action_span_dec",on:{click:function(t){e.decrease()}}}),e._v(" "),n("input",{directives:[{name:"model",rawName:"v-model",value:e.defaultNum,expression:"defaultNum"}],staticClass:"inline_block input",domProps:{value:e.defaultNum},on:{input:function(t){t.target.composing||(e.defaultNum=t.target.value)}}}),e._v(" "),n("span",{staticClass:"inline_block action action_span_add",on:{click:function(t){e.add()}}})])]),e._v(" "),n("section",{staticClass:"item bg_color_white",staticStyle:{"margin-top":"1px",padding:".4rem"}},[n("span",[e._v("支付金额")]),e._v(" "),n("div",{staticClass:"inline_block"},[n("span",{staticClass:"font_color_red",staticStyle:{"padding-right":"0.1rem"}},[e._v(e._s(e.service.price*e.defaultNum))]),e._v(" "),n("span",[e._v("元")])])]),e._v(" "),n("div",{staticClass:"button"},[n("button",{staticClass:"record_button",class:{disabled:!e.isPay},on:{click:e.onPay}},[e._v(e._s(e.payLabel))])]),e._v(" "),n("x-dialog",{staticClass:"dialog",model:{value:e.show,callback:function(t){e.show=t},expression:"show"}},[n("div",{staticClass:"img-box"},[n("svg-icon",{attrs:{"icon-class":"error"}}),e._v(" "),n("div",{staticClass:"dialog-tip"},[e._v(e._s(e.payTip))])],1),e._v(" "),n("div",{staticClass:"btn-wrapper"},[n("x-button",{nativeOn:{click:function(t){e.show=!1}}},[e._v("返回")])],1)])],1)},staticRenderFns:[]};var u=n("VU/8")(s,d,!1,function(e){n("yWQ+"),n("diKC")},"data-v-5d478de4",null);t.default=u.exports},"9q2/":function(e,t,n){var a=n("Tysu").v,r={}.hasOwnProperty,i=function(){n("UGUj").init()};i.prototype.version=a,e.exports=new i;var l=n("h2HD"),c=n("qaAm"),o=n("xTW6"),s=n("6ylr"),d=n("BbH1"),u=n("fVql");i.prototype.createPayment=function(e,t,n,a){if("function"==typeof t&&(c.userCallback=t),u.init(e),r.call(u,"id"))if(r.call(u,"channel")){r.call(u,"app")&&("string"==typeof u.app?s.app_id=u.app:"object"==typeof u.app&&"string"==typeof u.app.id&&(s.app_id=u.app.id)),d.report({type:s.type||"pure_sdk_click",channel:u.channel,ch_id:u.id});var i=u.channel;if(r.call(u,"credential"))if(u.credential)if(r.call(u.credential,i))if(r.call(u,"livemode")){var p=o.getChannelModule(i);if(void 0===p)return console.error('channel module "'+i+'" is undefined'),void c.innerCallback("fail",c.error("invalid_channel",'channel module "'+i+'" is undefined'));!1!==u.livemode?(void 0!==n&&(s.signature=n),"boolean"==typeof a&&(s.debug=a),p.handleCharge(u)):r.call(p,"runTestMode")?p.runTestMode(u):l.runTestMode(u)}else c.innerCallback("fail",c.error("invalid_charge","no_livemode_field"));else c.innerCallback("fail",c.error("invalid_credential","credential_is_incorrect"));else c.innerCallback("fail",c.error("invalid_credential","credential_is_undefined"));else c.innerCallback("fail",c.error("invalid_charge","no_credential"))}else c.innerCallback("fail",c.error("invalid_charge","no_channel"));else c.innerCallback("fail",c.error("invalid_charge","no_charge_id"))},i.prototype.setAPURL=function(e){s.APURL=e},i.prototype.setUrlReturnCallback=function(e,t){if("function"!=typeof e)throw"callback need to be a function";if(c.urlReturnCallback=e,void 0!==t){if(!Array.isArray(t))throw"channels need to be an array";c.urlReturnChannels=t}}},A6S8:function(e,t,n){var a=n("qaAm"),r=n("O/nt"),i=n("6ylr"),l=n("xTW6"),c={}.hasOwnProperty;e.exports={PINGPP_NOTIFY_URL_BASE:"https://notify.pingxx.com/notify",handleCharge:function(e){for(var t=e.credential[e.channel],n=["appId","timeStamp","nonceStr","package","signType","paySign"],r=0;r<n.length;r++)if(!c.call(t,n[r]))return void a.innerCallback("fail",a.error("invalid_credential","missing_field_"+n[r]));i.jsApiParameters=t,this.callpay()},callpay:function(){var e=this,t=l.getExtraModule("wx_jssdk");if(void 0!==t&&t.jssdkEnabled())t.callpay();else if("undefined"==typeof WeixinJSBridge){var n=function(){e.jsApiCall()};document.addEventListener?document.addEventListener("WeixinJSBridgeReady",n,!1):document.attachEvent&&(document.attachEvent("WeixinJSBridgeReady",n),document.attachEvent("onWeixinJSBridgeReady",n))}else this.jsApiCall()},jsApiCall:function(){c.call(i,"jsApiParameters")&&WeixinJSBridge.invoke("getBrandWCPayRequest",i.jsApiParameters,function(e){delete i.jsApiParameters,"get_brand_wcpay_request:ok"==e.err_msg?a.innerCallback("success"):"get_brand_wcpay_request:cancel"==e.err_msg?a.innerCallback("cancel"):a.innerCallback("fail",a.error("wx_result_fail",e.err_msg))})},runTestMode:function(e){if(confirm("模拟付款？")){var t="/charges/"+e.id;r.request(this.PINGPP_NOTIFY_URL_BASE+t+"?livemode=false","GET",null,function(e,t){if(t>=200&&t<400&&"success"==e)a.innerCallback("success");else{var n="http_code:"+t+";response:"+e;a.innerCallback("fail",a.error("testmode_notify_fail",n))}},function(){a.innerCallback("fail",a.error("network_err"))})}}}},BbH1:function(e,t,n){var a=n("O/nt"),r=n("6ylr"),i=n("vNiH"),l={seperator:"###",limit:1,report_url:"https://statistics.pingxx.com/one_stats",timeout:100},c=function(e,t){var n=new RegExp("(^|&)"+t+"=([^&]*)(&|$)","i"),a=e.substr(0).match(n);return null!==a?unescape(a[2]):null};l.store=function(e){if("undefined"!=typeof localStorage&&null!==localStorage){var t={};t.app_id=e.app_id||r.app_id||"app_not_defined",t.ch_id=e.ch_id||"",t.channel=e.channel||"",t.type=e.type||"",t.user_agent=navigator.userAgent,t.host=window.location.host,t.time=(new Date).getTime(),t.puid=r.puid;var n="app_id="+t.app_id+"&channel="+t.channel+"&ch_id="+t.ch_id+"&host="+t.host+"&time="+t.time+"&type="+t.type+"&user_agent="+t.user_agent+"&puid="+t.puid,a=n;null!==localStorage.getItem("PPP_ONE_STATS")&&0!==localStorage.getItem("PPP_ONE_STATS").length&&(a=localStorage.getItem("PPP_ONE_STATS")+this.seperator+n);try{localStorage.setItem("PPP_ONE_STATS",a)}catch(e){}}},l.send=function(){if("undefined"!=typeof localStorage&&null!==localStorage){var e=localStorage.getItem("PPP_ONE_STATS");if(!(null===e||e.split(this.seperator).length<this.limit))try{for(var t=[],n=e.split(this.seperator),r=i(n.join("&")),l=0;l<n.length;l++)t.push({app_id:c(n[l],"app_id"),channel:c(n[l],"channel"),ch_id:c(n[l],"ch_id"),host:c(n[l],"host"),time:c(n[l],"time"),type:c(n[l],"type"),user_agent:c(n[l],"user_agent"),puid:c(n[l],"puid")});a.request(this.report_url,"POST",t,function(e,t){200==t&&localStorage.removeItem("PPP_ONE_STATS")},void 0,{"X-Pingpp-Report-Token":r})}catch(e){}}},l.report=function(e){var t=this;t.store(e),setTimeout(function(){t.send()},t.timeout)},e.exports=l},GEQg:function(e,t,n){var a=n("tE86");e.exports={handleCharge:function(e){a.handleCharge(e)}}},HORF:function(e,t,n){var a=n("O/nt"),r=n("qaAm"),i={}.hasOwnProperty;e.exports={handleCharge:function(e){var t=e.channel,n=e.credential[t];i.call(n,"url")?a.redirectTo(n.url+"?"+a.stringifyData(n,t),t):r.innerCallback("fail",r.error("invalid_credential","missing_field:url"))}}},HUZE:function(e,t,n){var a=n("O/nt");e.exports={CP_B2B_URL:"https://payment.chinapay.com/CTITS/service/rest/page/nref/000000000017/0/0/0/0/0",handleCharge:function(e){var t=e.credential[e.channel];a.formSubmit(this.CP_B2B_URL,"post",t)}}},"O/nt":function(e,t,n){var a=n("qaAm"),r={}.hasOwnProperty,i=e.exports={stringifyData:function(e,t,n){void 0===n&&(n=!1);var a=[];for(var i in e)r.call(e,i)&&"function"!=typeof e[i]&&("bfb_wap"==t&&"url"==i||"yeepay_wap"==t&&"mode"==i||"channel_url"!=i&&a.push(i+"="+(n?encodeURIComponent(e[i]):e[i])));return a.join("&")},request:function(e,t,n,a,l,c){if("undefined"!=typeof XMLHttpRequest){var o=new XMLHttpRequest;if(void 0!==o.timeout&&(o.timeout=6e3),"GET"===(t=t.toUpperCase())&&"object"==typeof n&&n&&(e+="?"+i.stringifyData(n,"",!0)),o.open(t,e,!0),void 0!==c)for(var s in c)r.call(c,s)&&o.setRequestHeader(s,c[s]);"POST"===t?(o.setRequestHeader("Content-type","application/json; charset=utf-8"),o.send(JSON.stringify(n))):o.send(),void 0===a&&(a=function(){}),void 0===l&&(l=function(){}),o.onreadystatechange=function(){4==o.readyState&&a(o.responseText,o.status,o)},o.onerror=function(e){l(o,0,e)}}else console.log("Function XMLHttpRequest is undefined.")},formSubmit:function(e,t,n){if("undefined"!=typeof window){var a=document.createElement("form");for(var i in a.setAttribute("method",t),a.setAttribute("action",e),n)if(r.call(n,i)){var l=document.createElement("input");l.setAttribute("type","hidden"),l.setAttribute("name",i),l.setAttribute("value",n[i]),a.appendChild(l)}document.body.appendChild(a),a.submit()}else console.log("Not a browser, form submit url: "+e)},randomString:function(e){void 0===e&&(e=32);for(var t="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",n=t.length,a="",r=0;r<e;r++)a+=t.charAt(Math.floor(Math.random()*n));return a},redirectTo:function(e,t){a.shouldReturnUrlByCallback(t)?a.triggerUrlReturnCallback(null,e):"undefined"!=typeof window?window.location.href=e:console.log("Not a browser, redirect url: "+e)},inWeixin:function(){return"undefined"!=typeof navigator&&-1!==navigator.userAgent.toLowerCase().indexOf("micromessenger")},inAlipay:function(){return"undefined"!=typeof navigator&&-1!==navigator.userAgent.toLowerCase().indexOf("alipayclient")},documentReady:function(e){"undefined"!=typeof document?"loading"!=document.readyState?e():document.addEventListener("DOMContentLoaded",e):e()},loadUrlJs:function(e,t,n){var a=document.getElementsByTagName("head")[0],r=null;null==document.getElementById(e)?((r=document.createElement("script")).setAttribute("type","text/javascript"),r.setAttribute("src",t),r.setAttribute("id",e),r.async=!0,null!=n&&(r.onload=r.onreadystatechange=function(){if(r.ready)return!1;r.readyState&&"loaded"!=r.readyState&&"complete"!=r.readyState||(r.ready=!0,n())}),a.appendChild(r)):null!=n&&n()}}},RhKs:function(e,t,n){var a=n("tE86"),r=n("qaAm"),i=n("O/nt"),l={}.hasOwnProperty;e.exports={handleCharge:function(e){var t=e.extra;if(l.call(t,"pay_channel")){var n=t.pay_channel;"wx"!==n||i.inWeixin()?"alipay"!==n||i.inAlipay()?a.handleCharge(e):r.innerCallback("fail",r.error("Not in the Alipay browser")):r.innerCallback("fail",r.error("Not in the WeChat browser"))}else r.innerCallback("fail",r.error("invalid_charge","charge 格式不正确"))}}},Tysu:function(e,t){e.exports={v:"2.2.2"}},UGUj:function(e,t,n){var a=n("6ylr"),r=n("O/nt"),i=n("BbH1");e.exports={SRC_URL:"https://cookie.pingxx.com",init:function(){var e=this;r.documentReady(function(){try{e.initPuid()}catch(e){}})},initPuid:function(){if("undefined"!=typeof window&&"undefined"!=typeof localStorage&&null!==localStorage){var e=localStorage.getItem("pingpp_uid");if(null===e){e=r.randomString();try{localStorage.setItem("pingpp_uid",e)}catch(e){}}if(a.puid=e,!document.getElementById("p_analyse_iframe")){var t;try{t=document.createElement("iframe")}catch(e){t=document.createElement('<iframe name="ifr"></iframe>')}t.id="p_analyse_iframe",t.src=this.SRC_URL+"/?puid="+e,t.style.display="none",document.body.appendChild(t)}setTimeout(function(){i.send()},0)}}}},ZvHn:function(e,t,n){var a=n("O/nt");e.exports={handleCharge:function(e){var t=e.credential[e.channel];a.redirectTo(t)}}},bLUA:function(e,t,n){var a={}.hasOwnProperty,r=n("qaAm");e.exports={handleCharge:function(e){var t=e.credential[e.channel];a.call(t,"transaction_no")?this.tradePay(t.transaction_no):r.innerCallback("fail",r.error("invalid_credential","missing_field_transaction_no"))},ready:function(e){window.AlipayJSBridge?e&&e():document.addEventListener("AlipayJSBridgeReady",e,!1)},tradePay:function(e){this.ready(function(){AlipayJSBridge.call("tradePay",{tradeNO:e},function(e){"9000"==e.resultCode?r.innerCallback("success"):"6001"==e.resultCode?r.innerCallback("cancel",r.error(e.result)):r.innerCallback("fail",r.error(e.result))})})}}},diKC:function(e,t){},fVql:function(e,t,n){var a=n("qaAm"),r={}.hasOwnProperty;e.exports={id:null,or_id:null,channel:null,app:null,credential:{},extra:null,livemode:null,order_no:null,time_expire:null,init:function(e){var t;if("string"==typeof e)try{t=JSON.parse(e)}catch(e){return void a.innerCallback("fail",a.error("json_decode_fail",e))}else t=e;if(void 0!==t){if(r.call(t,"object")&&"order"==t.object){t.or_id=t.id,t.order_no=t.merchant_order_no;var n=t.charge_essentials;if(t.channel=n.channel,t.credential=n.credential,t.extra=n.extra,r.call(t,"charge")&&null!=t.charge)t.id=t.charge;else if(r.call(n,"id")&&null!=n.id)t.id=n.id;else if(r.call(t,"charges"))for(var i=0;i<t.charges.data.length;i++)if(t.charges.data[i].channel===n.channel){t.id=t.charges.data[i].id;break}}else r.call(t,"object")&&"recharge"==t.object&&(t=t.charge);for(var l in this)r.call(t,l)&&(this[l]=t[l]);return this}a.innerCallback("fail",a.error("json_decode_fail"))},clear:function(){for(var e in this)"function"!=typeof this[e]&&(this[e]=null)}}},gdg2:function(e,t,n){var a=n("O/nt");e.exports={UPACP_WAP_URL:"https://gateway.95516.com/gateway/api/frontTransReq.do",handleCharge:function(e){var t=e.credential[e.channel];a.formSubmit(this.UPACP_WAP_URL,"post",t)}}},h2HD:function(e,t,n){var a=n("O/nt"),r={}.hasOwnProperty;e.exports={PINGPP_MOCK_URL:"http://sissi.pingxx.com/mock.php",runTestMode:function(e){var t={ch_id:e.id,scheme:"http",channel:e.channel};r.call(e,"order_no")?t.order_no=e.order_no:r.call(e,"orderNo")&&(t.order_no=e.orderNo),r.call(e,"time_expire")?t.time_expire=e.time_expire:r.call(e,"timeExpire")&&(t.time_expire=e.timeExpire),r.call(e,"extra")&&(t.extra=encodeURIComponent(JSON.stringify(e.extra))),a.redirectTo(this.PINGPP_MOCK_URL+"?"+a.stringifyData(t),e.channel)}}},j1rR:function(e,t,n){var a=n("O/nt"),r=n("qaAm"),i={}.hasOwnProperty;e.exports={handleCharge:function(e){var t=e.credential[e.channel];"string"==typeof t?a.redirectTo(t,e.channel):"object"==typeof t&&i.call(t,"url")?a.redirectTo(t.url,e.channel):r.innerCallback("fail",r.error("invalid_credential","credential 格式不正确"))}}},jdPS:function(e,t,n){var a=n("tE86");e.exports={handleCharge:function(e){a.handleCharge(e)}}},mjOb:function(e,t,n){var a=n("O/nt"),r={}.hasOwnProperty;e.exports={ALIPAY_PC_DIRECT_URL:"https://mapi.alipay.com/gateway.do",handleCharge:function(e){var t=e.channel,n=e.credential[t],i=this.ALIPAY_PC_DIRECT_URL;r.call(n,"channel_url")&&(i=n.channel_url),r.call(n,"_input_charset")||r.call(n,"service")&&"create_direct_pay_by_user"===n.service&&(n._input_charset="utf-8");var l=a.stringifyData(n,t,!0);a.redirectTo(i+"?"+l,t)}}},qaAm:function(e,t,n){var a=n("fVql");e.exports={userCallback:void 0,urlReturnCallback:void 0,urlReturnChannels:["alipay_pc_direct"],innerCallback:function(e,t){"function"==typeof this.userCallback&&(void 0===t&&(t=this.error()),this.userCallback(e,t),this.userCallback=void 0,a.clear())},error:function(e,t){return{msg:e=void 0===e?"":e,extra:t=void 0===t?"":t}},triggerUrlReturnCallback:function(e,t){"function"==typeof this.urlReturnCallback&&this.urlReturnCallback(e,t)},shouldReturnUrlByCallback:function(e){return"function"==typeof this.urlReturnCallback&&-1!==this.urlReturnChannels.indexOf(e)}}},t6vo:function(e,t,n){var a,r,i=n("6ylr"),l={}.hasOwnProperty;r={PADCHAR:"=",ALPHA:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",makeDOMException:function(){try{return new DOMException(DOMException.INVALID_CHARACTER_ERR)}catch(t){var e=new Error("DOM Exception 5");return e.code=e.number=5,e.name=e.description="INVALID_CHARACTER_ERR",e.toString=function(){return"Error: "+e.name+": "+e.message},e}},getbyte64:function(e,t){var n=r.ALPHA.indexOf(e.charAt(t));if(-1===n)throw r.makeDOMException();return n},decode:function(e){e=""+e;var t,n,a,i=r.getbyte64,l=e.length;if(0===l)return e;if(l%4!=0)throw r.makeDOMException();t=0,e.charAt(l-1)===r.PADCHAR&&(t=1,e.charAt(l-2)===r.PADCHAR&&(t=2),l-=4);var c=[];for(n=0;n<l;n+=4)a=i(e,n)<<18|i(e,n+1)<<12|i(e,n+2)<<6|i(e,n+3),c.push(String.fromCharCode(a>>16,a>>8&255,255&a));switch(t){case 1:a=i(e,n)<<18|i(e,n+1)<<12|i(e,n+2)<<6,c.push(String.fromCharCode(a>>16,a>>8&255));break;case 2:a=i(e,n)<<18|i(e,n+1)<<12,c.push(String.fromCharCode(a>>16))}return c.join("")},getbyte:function(e,t){var n=e.charCodeAt(t);if(n>255)throw r.makeDOMException();return n},encode:function(e){if(1!==arguments.length)throw new SyntaxError("Not enough arguments");var t,n,a=r.PADCHAR,i=r.ALPHA,l=r.getbyte,c=[],o=(e=""+e).length-e.length%3;if(0===e.length)return e;for(t=0;t<o;t+=3)n=l(e,t)<<16|l(e,t+1)<<8|l(e,t+2),c.push(i.charAt(n>>18)),c.push(i.charAt(n>>12&63)),c.push(i.charAt(n>>6&63)),c.push(i.charAt(63&n));switch(e.length-o){case 1:n=l(e,t)<<16,c.push(i.charAt(n>>18)+i.charAt(n>>12&63)+a+a);break;case 2:n=l(e,t)<<16|l(e,t+1)<<8,c.push(i.charAt(n>>18)+i.charAt(n>>12&63)+i.charAt(n>>6&63)+a)}return c.join("")}},(a={}).url="pay.htm",a.pay=function(e){var t=encodeURIComponent(r.encode(e));l.call(i,"APURL")&&(a.url=i.APURL),location.href=a.url+"?goto="+t},a.decode=function(e){return r.decode(decodeURIComponent(e))},e.exports=a},t8o8:function(e,t,n){var a=n("qaAm"),r=n("O/nt"),i=n("6ylr"),l={}.hasOwnProperty;e.exports={SRC_URL:"https://open.mobile.qq.com/sdk/qqapi.js?_bid=152",ID:"mqq_api",handleCharge:function(e){var t=e.credential[e.channel];l.call(t,"token_id")?(i.tokenId=t.token_id,r.loadUrlJs(this.ID,this.SRC_URL,this.callpay)):a.innerCallback("fail",a.error("invalid_credential","missing_token_id"))},callpay:function(){if("undefined"!=typeof mqq){if(0==mqq.QQVersion)return a.innerCallback("fail",a.error("Not in the QQ client")),void delete i.tokenId;mqq.tenpay.pay({tokenId:i.tokenId},function(e){0==e.resultCode?a.innerCallback("success"):a.innerCallback("fail",a.error(e.retmsg))})}else a.innerCallback("fail",a.error("network_err"));delete i.tokenId}}},tE86:function(e,t,n){var a=n("O/nt"),r=n("qaAm"),i={}.hasOwnProperty;e.exports={handleCharge:function(e){var t,n=e.credential[e.channel];if("string"==typeof n)t=n;else{if(!i.call(n,"url"))return void r.innerCallback("fail",r.error("invalid_credential","credential format is incorrect"));t=n.url}a.redirectTo(t,e.channel)}}},tizn:function(e,t,n){var a=n("O/nt");e.exports={UPACP_PC_URL:"https://gateway.95516.com/gateway/api/frontTransReq.do",handleCharge:function(e){var t=e.credential[e.channel];a.formSubmit(this.UPACP_PC_URL,"post",t)}}},vNiH:function(e,t){!function(){function t(e,t){var n=(65535&e)+(65535&t);return(e>>16)+(t>>16)+(n>>16)<<16|65535&n}function n(e,n,a,r,i,l){return t((c=t(t(n,e),t(r,l)))<<(o=i)|c>>>32-o,a);var c,o}function a(e,t,a,r,i,l,c){return n(t&a|~t&r,e,t,i,l,c)}function r(e,t,a,r,i,l,c){return n(t&r|a&~r,e,t,i,l,c)}function i(e,t,a,r,i,l,c){return n(t^a^r,e,t,i,l,c)}function l(e,t,a,r,i,l,c){return n(a^(t|~r),e,t,i,l,c)}function c(e,n){var c,o,s,d,u;e[n>>5]|=128<<n%32,e[14+(n+64>>>9<<4)]=n;var p=1732584193,f=-271733879,h=-1732584194,_=271733878;for(c=0;c<e.length;c+=16)o=p,s=f,d=h,u=_,f=l(f=l(f=l(f=l(f=i(f=i(f=i(f=i(f=r(f=r(f=r(f=r(f=a(f=a(f=a(f=a(f,h=a(h,_=a(_,p=a(p,f,h,_,e[c],7,-680876936),f,h,e[c+1],12,-389564586),p,f,e[c+2],17,606105819),_,p,e[c+3],22,-1044525330),h=a(h,_=a(_,p=a(p,f,h,_,e[c+4],7,-176418897),f,h,e[c+5],12,1200080426),p,f,e[c+6],17,-1473231341),_,p,e[c+7],22,-45705983),h=a(h,_=a(_,p=a(p,f,h,_,e[c+8],7,1770035416),f,h,e[c+9],12,-1958414417),p,f,e[c+10],17,-42063),_,p,e[c+11],22,-1990404162),h=a(h,_=a(_,p=a(p,f,h,_,e[c+12],7,1804603682),f,h,e[c+13],12,-40341101),p,f,e[c+14],17,-1502002290),_,p,e[c+15],22,1236535329),h=r(h,_=r(_,p=r(p,f,h,_,e[c+1],5,-165796510),f,h,e[c+6],9,-1069501632),p,f,e[c+11],14,643717713),_,p,e[c],20,-373897302),h=r(h,_=r(_,p=r(p,f,h,_,e[c+5],5,-701558691),f,h,e[c+10],9,38016083),p,f,e[c+15],14,-660478335),_,p,e[c+4],20,-405537848),h=r(h,_=r(_,p=r(p,f,h,_,e[c+9],5,568446438),f,h,e[c+14],9,-1019803690),p,f,e[c+3],14,-187363961),_,p,e[c+8],20,1163531501),h=r(h,_=r(_,p=r(p,f,h,_,e[c+13],5,-1444681467),f,h,e[c+2],9,-51403784),p,f,e[c+7],14,1735328473),_,p,e[c+12],20,-1926607734),h=i(h,_=i(_,p=i(p,f,h,_,e[c+5],4,-378558),f,h,e[c+8],11,-2022574463),p,f,e[c+11],16,1839030562),_,p,e[c+14],23,-35309556),h=i(h,_=i(_,p=i(p,f,h,_,e[c+1],4,-1530992060),f,h,e[c+4],11,1272893353),p,f,e[c+7],16,-155497632),_,p,e[c+10],23,-1094730640),h=i(h,_=i(_,p=i(p,f,h,_,e[c+13],4,681279174),f,h,e[c],11,-358537222),p,f,e[c+3],16,-722521979),_,p,e[c+6],23,76029189),h=i(h,_=i(_,p=i(p,f,h,_,e[c+9],4,-640364487),f,h,e[c+12],11,-421815835),p,f,e[c+15],16,530742520),_,p,e[c+2],23,-995338651),h=l(h,_=l(_,p=l(p,f,h,_,e[c],6,-198630844),f,h,e[c+7],10,1126891415),p,f,e[c+14],15,-1416354905),_,p,e[c+5],21,-57434055),h=l(h,_=l(_,p=l(p,f,h,_,e[c+12],6,1700485571),f,h,e[c+3],10,-1894986606),p,f,e[c+10],15,-1051523),_,p,e[c+1],21,-2054922799),h=l(h,_=l(_,p=l(p,f,h,_,e[c+8],6,1873313359),f,h,e[c+15],10,-30611744),p,f,e[c+6],15,-1560198380),_,p,e[c+13],21,1309151649),h=l(h,_=l(_,p=l(p,f,h,_,e[c+4],6,-145523070),f,h,e[c+11],10,-1120210379),p,f,e[c+2],15,718787259),_,p,e[c+9],21,-343485551),p=t(p,o),f=t(f,s),h=t(h,d),_=t(_,u);return[p,f,h,_]}function o(e){var t,n="";for(t=0;t<32*e.length;t+=8)n+=String.fromCharCode(e[t>>5]>>>t%32&255);return n}function s(e){var t,n=[];for(n[(e.length>>2)-1]=void 0,t=0;t<n.length;t+=1)n[t]=0;for(t=0;t<8*e.length;t+=8)n[t>>5]|=(255&e.charCodeAt(t/8))<<t%32;return n}function d(e){var t,n,a="";for(n=0;n<e.length;n+=1)t=e.charCodeAt(n),a+="0123456789abcdef".charAt(t>>>4&15)+"0123456789abcdef".charAt(15&t);return a}function u(e){return unescape(encodeURIComponent(e))}function p(e){return function(e){return o(c(s(e),8*e.length))}(u(e))}function f(e,t){return function(e,t){var n,a,r=s(e),i=[],l=[];for(i[15]=l[15]=void 0,r.length>16&&(r=c(r,8*e.length)),n=0;n<16;n+=1)i[n]=909522486^r[n],l[n]=1549556828^r[n];return a=c(i.concat(s(t)),512+8*t.length),o(c(l.concat(a),640))}(u(e),u(t))}e.exports=function(e,t,n){return t?n?f(t,e):d(f(t,e)):n?p(e):d(p(e))}}()},voCn:function(e,t,n){var a=n("O/nt"),r=n("xTW6"),i={}.hasOwnProperty;e.exports={ALIPAY_WAP_URL_OLD:"https://wappaygw.alipay.com/service/rest.htm",ALIPAY_WAP_URL:"https://mapi.alipay.com/gateway.do",handleCharge:function(e){var t=e.channel,n=e.credential[t],l=this.ALIPAY_WAP_URL;i.call(n,"req_data")?l=this.ALIPAY_WAP_URL_OLD:i.call(n,"channel_url")&&(l=n.channel_url),i.call(n,"_input_charset")||(i.call(n,"service")&&"alipay.wap.create.direct.pay.by.user"===n.service||i.call(n,"req_data"))&&(n._input_charset="utf-8");var c=l+"?"+a.stringifyData(n,t,!0),o=r.getExtraModule("ap");a.inWeixin()&&void 0!==o?o.pay(c):a.redirectTo(c,t)}}},xTW6:function(e,t,n){var a={}.hasOwnProperty,r={};e.exports=r,r.channels={alipay_pc_direct:n("xz5l"),alipay_qr:n("bLUA"),alipay_wap:n("8aS2"),bfb_wap:n("HORF"),cb_alipay_pc_direct:n("mjOb"),cb_alipay_wap:n("voCn"),cb_wx_pub:n("+l/b"),cmb_wallet:n("/pZk"),cp_b2b:n("HUZE"),fqlpay_qr:n("GEQg"),fqlpay_wap:n("jdPS"),isv_wap:n("RhKs"),jdpay_wap:n("+QBg"),paypal:n("ZvHn"),qpay_pub:n("t8o8"),upacp_pc:n("tizn"),upacp_wap:n("gdg2"),wx_lite:n("940V"),wx_pub:n("A6S8"),wx_wap:n("j1rR"),yeepay_wap:n("2Yrm")},r.extras={ap:n("t6vo")},r.getChannelModule=function(e){if(a.call(r.channels,e))return r.channels[e]},r.getExtraModule=function(e){if(a.call(r.extras,e))return r.extras[e]}},xz5l:function(e,t,n){var a=n("O/nt"),r={}.hasOwnProperty;e.exports={ALIPAY_PC_DIRECT_URL:"https://mapi.alipay.com/gateway.do",handleCharge:function(e){var t=e.channel,n=e.credential[t],i=this.ALIPAY_PC_DIRECT_URL;r.call(n,"channel_url")&&(i=n.channel_url),r.call(n,"_input_charset")||r.call(n,"service")&&"create_direct_pay_by_user"===n.service&&(n._input_charset="utf-8");var l=a.stringifyData(n,t,!0);a.redirectTo(i+"?"+l,t)}}},"yWQ+":function(e,t){}});
//# sourceMappingURL=3.6d212ec2359ac66cc554.js.map