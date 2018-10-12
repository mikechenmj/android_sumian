webpackJsonp([52],{"/M7B":function(e,t){},"5qST":function(e,t,i){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=i("2sLL"),s=i("lRwf"),l=i.n(s),a=i("aQR8"),o=i("gyMJ"),r=(n.a,{name:"item-input",components:{XButton:n.a},data:function(){return{typeEnum:{allergy:"allergy",surgery:"surgery",inheritance:"inheritance"},isInput:!0,ownerConstans:{reset:0,sys:1,add:2,diy:3},inputValue:"",allergicDataSource:{key:"allergy",title:"选择过敏药物",items:[{title:"暂无",style:"item-selected",owner:0,canDelete:!1},{title:"青霉素",style:"item",owner:1,canDelete:!1},{title:"头孢类抗生素",style:"item",owner:1,canDelete:!1},{title:"磺胺类药物",style:"item",owner:1,canDelete:!1},{title:"普鲁卡因",style:"item",owner:1,canDelete:!1},{title:"维生素B1",style:"item",owner:1,canDelete:!1},{title:"地卡因",style:"item",owner:1,canDelete:!1},{title:"破伤风抗毒素TAT",style:"item",owner:1,canDelete:!1},{title:"异烟肼",style:"item",owner:1,canDelete:!1},{title:"泛影葡胺",style:"item",owner:1,canDelete:!1},{title:"阿司匹林",style:"item",owner:1,canDelete:!1},{title:"对氨基水杨酸",style:"item",owner:1,canDelete:!1},{title:"保泰松",style:"item",owner:1,canDelete:!1},{title:"+ 其他",style:"item-add",owner:2,canDelete:!1}],placeHolder:"添加您的过敏药物"},operatingSource:{key:"surgery",title:"选择手术或外伤",items:[{title:"暂无",style:"item-selected",owner:0,canDelete:!1},{title:"脑颅手术",style:"item",owner:1,canDelete:!1},{title:"颈部手术",style:"item",owner:1,canDelete:!1},{title:"胸部手术",style:"item",owner:1,canDelete:!1},{title:"腹部手术",style:"item",owner:1,canDelete:!1},{title:"背部手术",style:"item",owner:1,canDelete:!1},{title:"四肢手术",style:"item",owner:1,canDelete:!1},{title:"骨折",style:"item",owner:1,canDelete:!1},{title:"头部外伤",style:"item",owner:1,canDelete:!1},{title:"烧伤",style:"item",owner:1,canDelete:!1},{title:"烫伤",style:"item",owner:1,canDelete:!1},{title:"皮肤软组织损伤",style:"item",owner:1,canDelete:!1},{title:"肌腱损伤",style:"item",owner:1,canDelete:!1},{title:"+ 其他",style:"item-add",owner:2,canDelete:!1}],placeHolder:"添加您的手术或外伤"},geneticSource:{key:"inheritance",title:"选择家族遗传病史",items:[{title:"暂无",style:"item-selected",owner:0,canDelete:!1},{title:"高血压",style:"item",owner:1,canDelete:!1},{title:"糖尿病",style:"item",owner:1,canDelete:!1},{title:"心脏病",style:"item",owner:1,canDelete:!1},{title:"脑梗",style:"item",owner:1,canDelete:!1},{title:"脑出血",style:"item",owner:1,canDelete:!1},{title:"癌症",style:"item",owner:1,canDelete:!1},{title:"哮喘",style:"item",owner:1,canDelete:!1},{title:"过敏性疾病",style:"item",owner:1,canDelete:!1},{title:"癫痫病",style:"item",owner:1,canDelete:!1},{title:"白癜风",style:"item",owner:1,canDelete:!1},{title:"近视",style:"item",owner:1,canDelete:!1},{title:"+ 其他",style:"item-add",owner:2,canDelete:!1}],placeHolder:"添加您的家族遗传病史"}}},computed:{addedNum:function(){for(var e=0,t=0;t<this.dataSource.items.length;t++){this.dataSource.items[t].owner===this.ownerConstans.diy&&(e+=1)}return e},type:function(){return this.$route.params.type},dataSource:function(){return this.type===this.typeEnum.allergy?this.allergicDataSource:this.type===this.typeEnum.inheritance?this.geneticSource:this.operatingSource}},methods:{inputFocus:function(){this.isInput=!1,l.a.nextTick(function(){document.body.scrollTop=document.body.scrollHeight})},inputBlur:function(){this.isInput=!0},resetItem:function(){for(var e=0;e<this.dataSource.items.length;e++){var t=this.dataSource.items[e];t.owner!==this.ownerConstans.add&&(t.style=0===t.owner?"item-selected":"item")}},selectedItemAtIndex:function(e,t){var i=this.dataSource.items[e];if(!(e>this.dataSource.items.length||i.owner!==this.ownerConstans.sys&&i.owner!==this.ownerConstans.diy)){var n="item";t&&(n="item-selected"),this.dataSource.items[e].style=n,this.adjustResetItem()}},adjustResetItem:function(){for(var e=[],t=!1,i=0;i<this.dataSource.items.length;i++){var n=this.dataSource.items[i];0===n.owner&&e.push(n),t=t||"item-selected"===n.style}for(var s=0;s<e.length;s++){e[s].style=t?"item":"item-selected"}},addDiyItem:function(e){if(this.addedNum>=10)this.$vux.toast.text("最多只能添加10个");else{var t=Math.max(this.dataSource.items.length-1,0);this.dataSource.items.splice(t,0,{title:e,style:"item-selected",owner:this.ownerConstans.diy,canDelete:!0}),this.adjustResetItem()}},deleteItemAtIndex:function(e){e>=this.dataSource.items.length||this.dataSource.items[e].owner===this.ownerConstans.diy&&this.dataSource.items.splice(e,1)},onItemClick:function(e){var t=this.dataSource.items[e],i=t.owner;i!==this.ownerConstans.add&&(i!==this.ownerConstans.reset?this.selectedItemAtIndex(e,"item"===t.style):this.resetItem())},onSaveClick:function(){var e=this,t=this.getSelectedText(),i={};i[this.dataSource.key]=t,console.log(i),this.$vux.loading.show(),o.a.patch("medical-record",i).then(function(){e.$router.go(-1),e.$vux.loading.hide()}).catch(function(t){e.$vux.loading.hide(),console.log(),e.$vux.toast.text(t.error.internal_message)})},onDeleteClick:function(e){this.deleteItemAtIndex(e)},onAddButtonClick:function(){console.log(this.inputValue);var e=this.inputValue;e.length<=0?this.$vux.toast.text("输入不能为空"):e.length>8?this.$vux.toast.text("输入长度不能大于8"):(this.addDiyItem(e),this.inputValue="")},getSelectedText:function(){for(var e=[],t=0;t<this.dataSource.items.length;t++){var i=this.dataSource.items[t];"item-selected"===i.style&&i.owner!==this.ownerConstans.reset&&e.push(i.title)}return 0===e.length&&e.push("暂无"),e},setSelectedText:function(e){for(var t=function(e,t){for(var i=0;i<e.length;i++)if(e[i]===t.title||e[i].title===t)return!0;return!1},i=0;i<this.dataSource.items.length;i++){var n=this.dataSource.items[i];n.owner===this.ownerConstans.reset&&(n.style=e.length<=0?"item":"item-selected"),n.owner!==this.ownerConstans.sys&&n.owner!==this.ownerConstans.diy||this.selectedItemAtIndex(i,t(e,n))}for(var s=0;s<e.length;s++)t(this.dataSource.items,e[s])||this.addDiyItem(e[s])},fetchMedicalRecord:function(){var e=this;o.a.get("/medical-record").then(function(t){console.log(t);var i=t.data[e.dataSource.key];i&&i.length>0&&e.setSelectedText(i)}).catch(function(e){console.log(e.error.internal_message)})}},mounted:function(){console.log(a.c.height)},created:function(){this.fetchMedicalRecord()}}),c={render:function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("div",{staticClass:"main",attrs:{id:"item-input"}},[i("div",{staticClass:"title"},[e._v(e._s(e.dataSource.title))]),e._v(" "),i("div",{staticClass:"line"}),e._v(" "),i("ul",{staticClass:"content"},[e._l(e.dataSource.items,function(t,n){return i("li",{key:n},[i("div",{staticClass:"item",class:t.style,on:{click:function(t){e.onItemClick(n)}}},[e._v(e._s(t.title)+"\n        "),2===t.owner?i("div",{ref:"inputRef",refInFor:!0,staticClass:"ry-input-container",class:{"op-input":e.isInput}},[i("input",{directives:[{name:"model",rawName:"v-model",value:e.inputValue,expression:"inputValue"}],staticClass:"ry-input",attrs:{type:"text",placeholder:e.dataSource.placeHolder},domProps:{value:e.inputValue},on:{focus:e.inputFocus,blur:e.inputBlur,input:function(t){t.target.composing||(e.inputValue=t.target.value)}}}),e._v(" "),i("div",{staticClass:"ry-button",on:{click:e.onAddButtonClick}},[e._v("添加")])]):e._e(),e._v(" "),t.canDelete?i("div",{staticClass:"delete-item",on:{click:function(t){t.stopPropagation(),e.onDeleteClick(n)}}},[i("svg-icon",{staticClass:"delete-item",attrs:{"icon-class":"record_h5_ icon_delete_2"}})],1):e._e()])])}),e._v(" "),i("li",{staticClass:"clear"})],2),e._v(" "),i("div",{staticClass:"btn-submit",on:{click:e.onSaveClick}},[e._v("保存")])])},staticRenderFns:[]};var u=i("VU/8")(r,c,!1,function(e){i("/M7B")},null,null);t.default=u.exports}});