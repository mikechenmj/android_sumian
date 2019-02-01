package com.sumian.sd.buz.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:  CBTI  练习
 */
data class Exercise(var id: Int,//练习 id
                    var cbti_course_id: Int,//练习所属课程id
                    var title: String,
                    var guide: Guide,
                    var type: Int,//练习类型 0：判断题 1：勾选题 2：量表题 3：跳转题
        //var data: Any,//type=0 判断题   type=1 选择题 注意：这是对象数组   type=2  量表 type=3 跳转题  后面2种类型都是对象  cry
                    var final_words: String,
                    var is_lock: Boolean,
                    var done: Boolean,
                    var type_string: String) {

    data class Guide(var title: String,
                     var description: String,
                     var button: String)

}