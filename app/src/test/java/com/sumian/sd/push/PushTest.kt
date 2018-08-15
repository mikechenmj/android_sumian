package com.sumian.sd.push

import android.os.Bundle
import com.sumian.sd.notification.push.PushData
import org.junit.Test
import java.net.URLDecoder

@Suppress("CanBeVal")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 10:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class PushTest {

    @Test
    fun test() {
        val bundle = Bundle()
        bundle.putString("action", "push")
        bundle.putString("alert", "this is a message")
        bundle.putString("scheme", "http://www.google.com")
        val pushData = PushData.create(bundle)
        System.out.println(pushData)
    }

    @Test
    fun test2() {
        var pushData: PushData? = PushData(null)
        System.out.println(pushData)
        pushData = null
        val action = pushData?.action ?: "default_action"
        System.out.println(action)
    }

    /**

    "scheme": "sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527782400%26notification_id%3Db9c52b25-de47-483f-bee4-0921a3b1d47b%26user_id%3D2102",
    "scheme": "sleepdoctor%3A%2F%2Fscale-distributions%3Fid%3D623%26notification_id%3Db95e5e60-6d8c-4354-a1ae-444fc9eabc93%26user_id%3D2102",
    "scheme": "sleepdoctor%3A%2F%2Fonline-reports%3Fid%3D4%26url%3Dhttps%3A%2F%2Fsleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com%2Fdoctors%2Fonline_report%2F1%2F10544995-4294-4e31-87bf-742df412bacc.pdf%26notification_id%3D7448f61c-b95a-44fa-8f03-d294b9f24145%26user_id%3D2102",

    sleepdoctor://diaries?date=1527782400&notification_id=b9c52b25-de47-483f-bee4-0921a3b1d47b&user_id=2102
    sleepdoctor://scale-distributions?id=623&notification_id=b95e5e60-6d8c-4354-a1ae-444fc9eabc93&user_id=2102
    sleepdoctor://online-reports?id=4&url=https://sleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com/doctors/online_report/1/10544995-4294-4e31-87bf-742df412bacc.pdf&notification_id=7448f61c-b95a-44fa-8f03-d294b9f24145&user_id=2102
     */
    @Test
    fun test3() {
        var url_1 = "sleepdoctor%3A%2F%2Fdiaries%3Fdate%3D1527782400%26notification_id%3Db9c52b25-de47-483f-bee4-0921a3b1d47b%26user_id%3D2102"
        var url_2 = "sleepdoctor%3A%2F%2Fscale-distributions%3Fid%3D623%26notification_id%3Db95e5e60-6d8c-4354-a1ae-444fc9eabc93%26user_id%3D2102"
        var url_3 = "sleepdoctor%3A%2F%2Fonline-reports%3Fid%3D4%26url%3Dhttps%3A%2F%2Fsleep-doctor-imm-dev.oss-cn-shanghai.aliyuncs.com%2Fdoctors%2Fonline_report%2F1%2F10544995-4294-4e31-87bf-742df412bacc.pdf%26notification_id%3D7448f61c-b95a-44fa-8f03-d294b9f24145%26user_id%3D2102"
        var urls = arrayOf(url_1, url_2, url_3)
        for (url in urls) {
            System.out.println(URLDecoder.decode(url, "UTF-8"))
//            val uri: Uri = Uri.parse(url)
//            System.out.println(uri.getQueryParameter("notification_id"))
        }

    }
}