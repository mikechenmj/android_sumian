webpackJsonp([50],{"37GB":function(t,i){},VlgL:function(t,i,e){"use strict";Object.defineProperty(i,"__esModule",{value:!0});var o=e("rHil"),s=e("32ER"),a=e("1DHf"),n=e("ALGc"),r=e("2sLL"),l=e("+4yL"),c=e("gyMJ"),u=e("aQR8"),d=(o.a,s.a,a.a,n.a,r.a,l.a,{name:"bookings-inde",components:{Group:o.a,CellBox:s.a,Cell:a.a,XTextarea:n.a,XButton:r.a,PickerSleepDoctor:l.a},data:function(){for(var t=[],i=0;i<28;i++){var e=this.$moment().add(i,"d");"6"!==e.format("d")&&"0"!==e.format("d")&&t.push(e)}var o=[],s=[];return t.map(function(t,i){0===i?s.push("今天"):s.push(t.format("MM.DD"))}),s.map(function(t){o.push({value:t+"",name:t+""});for(var i=19;i<=22;i++){o.push({value:t+i+"",name:i+"",parent:t+""});for(var e=0;e<=59;e++)o.push({value:t+"-"+i+"-"+e,name:e+"",parent:t+i+""})}}),{add:"",consulting_question:"",isShowPicker:!1,id:null,planStartAt:"请选择",booking:null,bookingStatus:u.b,dataSource:{itemList:o,model:[],columns:3}}},computed:{appointment:{get:function(){if(console.log(this.booking),this.booking){var t=this.booking.package.servicePackage;return t.service_length+u.e[t.service_length_unit]}return"暂无数据"}}},methods:{selectTime:function(){this.isShowPicker=!0},submitBooking:function(){var t=this;"请选择"!==this.planStartAt?""!==this.consulting_question?""!==this.add?c.a.patch("bookings/"+this.id,{plan_start_at:this.$moment(this.planStartAt,"YYYY.MM.DD HH:mm").unix(),consulting_question:this.consulting_question,add:this.add}).then(function(i){t.$router.go(0)}).catch(function(i){console.log(i),i.error?t.$vux.toast.show({text:i.error.user_message,width:"3rem",type:"text",position:"middle"}):t.$vux.toast.show({text:i.message,width:"3rem",type:"text",position:"middle"})}):this.$vux.toast.show({text:"补充说明是必填项，请先填写",width:"3rem",type:"text",position:"middle"}):this.$vux.toast.show({text:"咨询问题是必填项，请先填写",width:"3rem",type:"text",position:"middle"}):this.$vux.toast.show({text:"未填写时间",width:"3rem",type:"text",position:"middle"})},changePicker:function(t){var i=this.$moment().format("YYYY.");i+=t.data[2].replace("今天",this.$moment().format("MM.DD")),console.log(i),this.planStartAt=this.$moment(i,"YYYY.MM.DD-HH-mm").format("YYYY.MM.DD HH:mm"),console.log(this.planStartAt),this.isShowPicker=!1},onPickerHide:function(t){this.isShowPicker=!1,console.log("onPickerHide",t)},getBookings:function(){var t=this;c.a.get("/bookings/"+this.id,{params:{include:"package.servicePackage"}}).then(function(i){t.booking=i.data,t.booking.plan_start_at&&(t.planStartAt=t.$moment.unix(t.booking.plan_start_at).format("YYYY.MM.DD HH:mm"))})}},created:function(){this.id=this.$route.params.id,this.getBookings()}}),m={render:function(){var t=this,i=t.$createElement,e=t._self._c||i;return t.booking?e("div",{attrs:{id:"bookings-index"}},[7===t.booking.status&&0===t.booking.plan_start_at?e("div",{staticClass:"no-data"},[e("div",{staticClass:"banner"},[t._v("服务已取消")]),t._v(" "),e("svg-icon",{staticClass:"no-data-icon",attrs:{"icon-class":"emptystate_img_cancellation"}}),t._v(" "),e("p",{staticClass:"title"},[t._v("相关服务已取消")]),t._v(" "),e("p",{staticClass:"decs"},[t._v("如有疑问，请咨询客服")])],1):e("div",[9!==t.booking.status?e("group",[e("cell",{attrs:{title:"订单状态"}},[e("div",{class:{"blue-font":0===t.booking.status}},[t._v(t._s(t.bookingStatus[t.booking.status]))])])],1):t._e(),t._v(" "),e("group",[9===t.booking.status?e("cell",{staticClass:"plan-start-at",attrs:{title:"预约时间","is-link":""},nativeOn:{click:function(i){return t.selectTime(i)}}},[e("div",[t._v(t._s(t.planStartAt))])]):e("cell",{attrs:{title:"预约时间"}},[e("div",[t._v("\n          "+t._s(t.planStartAt)+"\n        ")])]),t._v(" "),e("cell",{attrs:{title:"预约时长"}},[e("div",[t._v("\n          "+t._s(t.appointment)+"\n        ")])])],1),t._v(" "),9===t.booking.status?e("group",[e("cell",{attrs:{title:"咨询问题"}}),t._v(" "),e("x-textarea",{staticClass:"txt-questions",attrs:{max:21,min:10,autosize:"",placeholder:"一句话描述你想要咨询的问题（20字以内）"},model:{value:t.consulting_question,callback:function(i){t.consulting_question=i},expression:"consulting_question"}})],1):t._e(),t._v(" "),9===t.booking.status?e("group",[e("cell",{attrs:{title:"补充说明"}}),t._v(" "),e("x-textarea",{attrs:{max:400,min:10,autosize:"",placeholder:"请详细描述您希望解决的问题：1.描述您的睡前行为；2.描述症状；3.药物相关问题等（400字以内）"},model:{value:t.add,callback:function(i){t.add=i},expression:"add"}})],1):t._e(),t._v(" "),9!==t.booking.status?e("group",[e("cell",{attrs:{title:"咨询问题"}}),t._v(" "),9!==t.booking.status?e("cell-box",{staticClass:"txt-default"},[e("div",{domProps:{innerHTML:t._s(t.booking.consulting_question)}})]):t._e(),t._v(" "),e("cell",{attrs:{title:"补充说明"}}),t._v(" "),9!==t.booking.status?e("cell-box",{staticClass:"txt-default"},[e("div",{domProps:{innerHTML:t._s(t.booking.add)}})]):t._e()],1):t._e(),t._v(" "),9===t.booking.status?e("x-button",{attrs:{type:"primary"},nativeOn:{click:function(i){return t.submitBooking(i)}}},[t._v("确认")]):t._e(),t._v(" "),e("picker-sleep-doctor",{attrs:{title:"预约时间",submitText:"确定",dataSource:t.dataSource},on:{"on-submit":t.changePicker,"on-hide":t.onPickerHide},model:{value:t.isShowPicker,callback:function(i){t.isShowPicker=i},expression:"isShowPicker"}})],1)]):t._e()},staticRenderFns:[]};var p=e("VU/8")(d,m,!1,function(t){e("37GB")},null,null);i.default=p.exports}});