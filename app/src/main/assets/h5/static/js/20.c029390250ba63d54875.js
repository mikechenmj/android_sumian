webpackJsonp([20],{"7c1E":function(e,t){},"9UC1":function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=s("rHil"),a=s("32ER"),r=s("1DHf"),n=s("to2R"),o=s("/kga"),l=s("63CM"),c=(o.a,o.a,l.a,{name:"text-dialog",components:{XDialog:o.a},comments:{XDialog:o.a},directives:{TransferDom:l.a},data:function(){return{show:!1}},props:["isShow","title"],methods:{close:function(){this.$emit("handleHide")}}}),u={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{directives:[{name:"transfer-dom",rawName:"v-transfer-dom"}]},[s("x-dialog",{staticClass:"text-dialog",model:{value:e.isShow,callback:function(t){e.isShow=t},expression:"isShow"}},[s("div",{staticClass:"title"},[e._v(e._s(e.title))]),e._v(" "),e._t("default"),e._v(" "),s("div",{staticClass:"close",on:{click:e.close}},[s("svg-icon",{staticClass:"icon-close",attrs:{"icon-class":"record_h5_ icon_delete"}})],1)],2)],1)},staticRenderFns:[]};var d=s("VU/8")(c,u,!1,function(e){s("7c1E")},null,null).exports,h=s("jNWb"),p=s("gyMJ"),v=(i.a,a.a,r.a,n.a,h.a,{name:"curriculum",components:{Group:i.a,CellBox:a.a,Cell:r.a,SportActionsheet:n.a,TextDialog:d,SleepDialog:h.a},data:function(){return{courses:[],currentCourse:{},currentChapter:{},show:!1,isLastWeekShow:!1,lastWeekSummary:"",player:null,videoOver:!1,playProgress:[],playCurrent:0,num:0,dialog:!1,exerciseIsFilled:!1,questionnairesDialog:!1}},mounted:function(){},methods:{initVideo:function(e,t){(e||t)&&(null!==this.player&&(this.player.dispose(),this.player=null),this.player=new Aliplayer({id:"J_prismPlayer",showBarTime:1e4,controlBarVisibility:"click",x5_orientation:"landscape",vid:e,playauth:t,skinLayout:[{name:"H5Loading",align:"cc"},{name:"controlBar",align:"blabs",children:[{name:"playButton",align:"tl"},{name:"progress",align:"tl"},{name:"fullScreenButton",align:"tr",x:10,y:12},{name:"timeDisplay",align:"tr"}]}],cover:"https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/cbti/banners/1/0a85cea9-e32e-482e-a578-3d4c7d9bcd49.jpg",autoplay:!1,preload:!1}),this.player.on("ready",this.handleReady),this.player.on("pause",this.pause),this.player.on("ended",this.ended),this.player.on("timeupdate",this.timeupdate),this.player.on("play",this.play),this.player.on("cancelFullScreen",this.cancelFullScreen))},play:function(e){this.seek()},doubleClick:function(){var e=this.player.getStatus();null!==this.player&&("playing"===e?this.player.pause():"pause"!==e&&"init"!==e||this.player.play())},playVideo:function(){this.player.play()},nextVideo:function(){var e=this,t=this.$route.query;this.videoOver=!1,this.getCourse(t.id,t.cid).then(function(s){e.courses.map(function(s,i,a){s.id===Number(t.cid)&&(a[i+1].is_lock?e.dialog=!0:(e.$router.replace({name:"curriculum",query:{id:e.currentChapter.id,cid:a[i+1].id}}),e.currentCourse=a[i+1],document.title=a[i+1].title,e.getPlayAuth(a[i+1].id)))})})},newArray:function(e){for(var t=[],s=0;s<e.length;s++){var i=parseInt(e[s],16);if((i=i.toString(2)).length<4)for(var a=0;a<=4-i.length+1;a++)i="0"+i;(i=i.split("")).map(function(e){t.push(Number(e))})}return console.log(t),t},cancelFullScreen:function(){},updataLogs:function(){var e="";console.log(this.playProgress,"updataLogs"),this.playProgress.map(function(t,s,i){if((s+1)%4==0){var a=i.slice(s-4+1,s+1).join("");e+=parseInt(a,2).toString(16)}});var t={video_progress:e.toUpperCase(),end_point:this.playCurrent};p.a.post("/cbti-course/"+this.currentCourse.id+"/logs",t)},ended:function(e){this.updataLogs(),this.currentCourse.exercise_is_filled?this.videoOver=!0:this.nextVideo()},pause:function(){this.updataLogs()},handleReady:function(e){console.log(e,"handleReady"),this.player.play()},timeupdate:function(e){if(0===this.playProgress.length){for(var t=parseInt(this.player.getDuration()),s="",i=0;i<parseInt(t/4);i++)s+="0";console.log(s),this.playProgress=this.newArray(s)}var a=parseInt(this.player.getCurrentTime());this.playProgress[a]=1,this.playCurrent=a,this.num+=1,100===this.num&&(this.updataLogs(),this.num=0)},showCourse:function(){this.show=!0},seek:function(){console.log("续播")},changeCourse:function(e){var t=this;e.is_lock?this.$vux.toast.text("完成上节课程后解锁"):(this.videoOver=!1,this.$router.replace({name:"curriculum",query:{id:this.currentChapter.id,cid:e.id}}),this.getCourse(this.currentChapter.id,e.id).then(function(s){t.playProgress=[],t.getPlayAuth(e.id)}))},goToExercise:function(){this.$router.push({name:"exercises",query:{id:this.currentCourse.id}})},lastWeekReview:function(){var e=this;return p.a.get("/cbti-courses/"+this.currentCourse.id).then(function(t){return e.lastWeekSummary=t.data.last_chapter_summary,e.isLastWeekShow=!0,t})},getPlayAuth:function(e){var t=this;return p.a.get("/cbti-courses/"+e).then(function(e){console.log(e.data.meta.exercise_is_filled,null!==e.data.meta.exercise),e.data.meta.is_pop_questionnaire,null!==e.data.meta.exercise?(e.data.meta.exercise_is_filled||(t.currentCourse.exercise_is_filled=!0),t.exerciseIsFilled=!0,t.currentCourse.cbti_course_id=e.data.meta.exercise.cbti_course_id):t.exerciseIsFilled=!1;var s=e.data;return t.initVideo(s.meta.video_id,s.meta.play_auth),s.meta.course_log&&(t.playProgress=t.newArray(s.meta.course_log.video_progress)),e}).catch(function(e){console.log(e)})},getCourse:function(e,t){var s=this;return p.a.get("/cbti-chapter/"+e+"/courses").then(function(e){return e.data.data.map(function(e){e.id===parseInt(t)&&(s.currentCourse=e,document.title=s.currentCourse.title)}),s.courses=e.data.data,s.currentChapter=e.data.meta.chapter,e})}},created:function(){var e=this,t=this.$route.query;this.getCourse(t.id,t.cid).then(function(t){e.getPlayAuth(e.$route.query.cid)})}}),m={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"curriculum"},[s("div",{ref:"video",staticClass:"video",staticStyle:{overflow:"hidden"}},[s("div",{staticClass:"prism-player",attrs:{id:"J_prismPlayer"},on:{dblclick:e.doubleClick}}),e._v(" "),e.videoOver?s("div",{staticClass:"video-over"},[s("div",{staticClass:"course-title"},[e._v("本节课程结束")]),e._v(" "),s("div",{staticClass:"course-txt"},[e._v("完成随堂练习，巩固所学知识")]),e._v(" "),s("div",{staticClass:"bnts"},[s("div",{staticClass:"reload",on:{click:e.nextVideo}},[e._v("继续观看")]),e._v(" "),s("router-link",{staticClass:"over-video",attrs:{to:{path:"/cbti/exercises?id="+e.currentCourse.cbti_course_id}}},[e._v("完成练习\n        ")])],1)]):e._e()]),e._v(" "),s("div",{staticClass:"curriculum-tab"},[e.exerciseIsFilled?s("router-link",{staticClass:"item",attrs:{to:{path:"/cbti/exercises",query:{id:e.currentCourse.id}}}},[s("svg-icon",{staticClass:"item-icon",attrs:{"icon-class":"cbti_icon_exercise"}}),e._v("\n      课程练习\n    ")],1):e._e(),e._v(" "),1!==e.currentChapter.index&&1===e.currentCourse.index?s("div",{staticClass:"item",on:{click:e.lastWeekReview}},[s("svg-icon",{staticClass:"item-icon",attrs:{"icon-class":"cbti_icon_review"}}),e._v("\n      上周回顾\n    ")],1):e._e()],1),e._v(" "),s("group",{staticClass:"summary"},[s("cell",{attrs:{title:"课程总结"}},[s("span",{staticClass:"curriculum-btn",on:{click:e.showCourse}},[e._v("课程表\n        "),s("svg-icon",{staticClass:"curriculum-icon",attrs:{"icon-class":"cbti_icon_list"}})],1)]),e._v(" "),s("cell-box",[e._v(e._s(e.currentCourse.summary))])],1),e._v(" "),s("sport-actionsheet",{attrs:{showCancel:!1,customMenus:!0},model:{value:e.show,callback:function(t){e.show=t},expression:"show"}},[s("div",{attrs:{slot:"header"},slot:"header"},[e._v("\n      课程列表\n    ")]),e._v(" "),s("div",{staticClass:"curriculum-lists",attrs:{slot:"menus"},slot:"menus"},e._l(e.courses,function(t){return s("div",{key:t.id,staticClass:"item",class:{slock:t.is_lock,ing:t.id===e.currentCourse.id},on:{click:function(s){e.changeCourse(t)}}},[e._v(e._s(t.title)+"\n      ")])}))]),e._v(" "),s("text-dialog",{attrs:{isShow:e.isLastWeekShow,title:"上周回顾"},on:{handleHide:function(t){e.isLastWeekShow=!1}}},[s("p",[e._v(e._s(e.lastWeekSummary))])]),e._v(" "),s("sleep-dialog",{attrs:{cancel:!1,title:"下节课程待开放",content:"完成本节课程学习后自动解锁下节内容"},on:{handleOk:function(t){e.dialog=!1}},model:{value:e.dialog,callback:function(t){e.dialog=t},expression:"dialog"}}),e._v(" "),s("sleep-dialog",{attrs:{submitText:"提交",cancel:!1},on:{handleOk:function(t){e.questionnairesDialog=!1}},model:{value:e.questionnairesDialog,callback:function(t){e.questionnairesDialog=t},expression:"questionnairesDialog"}},[s("div",{staticClass:"cbti-questionnaires",attrs:{slot:"content"},slot:"content"},[s("div",{staticClass:"question"},[e._v("1.上周睡眠限制执行起来感觉如何？")]),e._v(" "),s("div",{staticClass:"selection"},[s("span",[e._v("十分困难")]),e._v(" "),s("span",[e._v("十分困难")]),e._v(" "),s("span",[e._v("十分困难")]),e._v(" "),s("span",[e._v("十分困难")])])])])],1)},staticRenderFns:[]};var _=s("VU/8")(v,m,!1,function(e){s("eJst")},null,null);t.default=_.exports},eJst:function(e,t){}});
//# sourceMappingURL=20.c029390250ba63d54875.js.map