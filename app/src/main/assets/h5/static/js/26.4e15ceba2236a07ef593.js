webpackJsonp([26],{WEDH:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s=a("lRwf"),r=a.n(s),i=a("+4yL"),n=a("gyMJ"),c=a("o0rJ"),o=a("mHYM"),d=(i.a,{data:function(){return{from:"",targetTags:[],showPicker:!1,dataPicker:{title:"",dataSource:{itemList:[],model:[],columns:1}},currentSelectIndex:null,currentSelectId:null,selectedData:[],env:Object(o.a)()}},components:{PickerSleepDoctor:i.a},methods:{selectTags:function(t,e){if(this.currentSelectId=t.target_id,""!==t.question)this.dataPicker.title=t.question,this.dataPicker.dataSource.itemList=t.range.concat("取消选择"),console.log("answer",t.range,[t.answer]),this.dataPicker.dataSource.model=[t.answer],console.log("targetdata",this.dataPicker),this.showPicker=!0,this.currentSelectIndex=e;else{if(this.targetTags[e].selected){this.targetTags[e].selected=!1;var a=this.selectedDataIndex(t.target_id);-1!==a&&this.selectedData.splice(a,1)}else this.targetTags[e].selected=!0,this.selectedData.push({target_id:t.target_id,answer:""});r.a.set(this.targetTags,e,this.targetTags[e])}},selectedDataIndex:function(t){for(var e=this.selectedData,a=-1,s=0;s<e.length;s++)e[s].target_id===t&&(a=s);return a},changePicker:function(t){var e=this,a=this.currentSelectIndex;"取消选择"===t.data[0]?(this.targetTags[a].answer="",this.targetTags[a].selected=!1):(this.targetTags[a].answer=t.data[0],this.targetTags[a].selected=!0),r.a.set(this.targetTags,a,this.targetTags[a]),this.selectedData.map(function(t,a){t.target_id===e.currentSelectId&&e.selectedData.splice(a,1)}),"取消选择"!==t.data[0]&&this.selectedData.push({target_id:this.currentSelectId,answer:this.targetTags[a].answer}),this.onPickerHide()},onPickerHide:function(){this.showPicker=!1},next:function(){var t=this;if(0===this.selectedData.length)return 2===this.env?void Object(c.a)(function(t){t.callHandler("showToast",{type:"text",message:"请填写完以上信息",duration:1e3})}):void this.$vux.toast.show({text:"请填写完以上信息",type:"text",position:"middle",width:"2.8rem"});n.a.post("/targets",this.selectedData).then(function(e){if(2===t.env&&Object(c.a)(function(a){a.callHandler("saveMyTarget",{code:"mine"===t.from?0:1,message:"保存成功",result:e.data})}),"newUser"===t.from)t.$router.push({name:"perfect-info-step1"});else if("mine"===t.from){if(2===t.env)return void Object(c.a)(function(t){t.callHandler("showToast",{type:"success",message:"保存成功",duration:1e3})});t.$vux.toast.show({text:"保存成功",type:"success",position:"middle",width:"2.4rem"}),t.$router.go(-1)}}).catch(function(e){2!==t.env?t.$vux.toast.show({text:e.error.user_message,type:"error",position:"middle",width:"2.4rem"}):Object(c.a)(function(t){t.callHandler("showToast",{type:"error",message:e.error.user_message,duration:1e3})})})},getTargetTags:function(){var t=this;n.a.get("target-tags").then(function(e){t.targetTags=e.data.data,t.targetTags.map(function(t){return t.selected=!1,t.answer="",t}),"mine"===t.from&&t.getMyTargets()})},getMyTargets:function(){var t=this;n.a.get("targets").then(function(e){for(var a=e.data.data,s=t.targetTags,i=0;i<a.length;i++){for(var n=0;n<s.length;n++)a[i].target_id===s[n].target_id&&(t.targetTags[n].selected=!0,t.targetTags[n].answer=a[i].answer,r.a.set(t.targetTags,n,t.targetTags[n]));t.selectedData.push({target_id:a[i].target_id,answer:a[i].answer})}})}},created:function(){this.from=this.$route.query.from,this.getTargetTags()}}),g={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"my-target-page",class:{appHeight:2===t.env}},[a("div",{staticClass:"title"},[t._v("请确立您的睡眠目标")]),t._v(" "),a("div",{staticClass:"description"},[t._v("睡眠医生陪您一起实现目标，改善睡眠")]),t._v(" "),a("div",{staticClass:"target-list"},t._l(t.targetTags,function(e,s){return a("div",{key:e.target_id,staticClass:"list-item",class:{selected:e.selected},on:{click:function(a){t.selectTags(e,s)}}},[t._v("\n      "+t._s(e.name)+"\n      "),""!==e.answer?a("span",[t._v(t._s(e.answer)+" "+t._s(0===s||2===s?"min":1===s?"次":3===s?"h":""))]):t._e()])})),t._v(" "),a("div",{staticClass:"next",on:{click:t.next}},[t._v(t._s("newUser"===t.from?"下一步":"保存"))]),t._v(" "),a("picker-sleep-doctor",{attrs:{title:t.dataPicker.title,submitText:"确定",isShow:t.showPicker,dataSource:t.dataPicker.dataSource},on:{"on-submit":t.changePicker,"on-hide":t.onPickerHide}})],1)},staticRenderFns:[]};var l=a("VU/8")(d,g,!1,function(t){a("htQi")},null,null);e.default=l.exports},htQi:function(t,e){}});
//# sourceMappingURL=26.4e15ceba2236a07ef593.js.map