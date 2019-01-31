package com.sumian.sd.buz.account.achievement

/**
 * Created by jzz
 *
 * on 2019/1/24
 *
 * desc:
 */
class H5Style {

    companion object {
        internal const val prefix ="<style>" +
                "p.p1 {margin: 0.0px 0.0px 0.0px 0.0px; font: 14.0px PingFangSC-Regular; color: #878D99}\n" +
                "span.a0 {font-family: PingFangSC-Regular; font-weight: normal; font-style: normal; font-size: 14.00px}\n" +
                "span.a1 {font-family: PingFangSC-Medium; font-weight: bold; font-style: normal; font-size: 16.00px; color: #A66F00}\n" +
                "</style>\n" +
                "<p class=\\\"p1\\\">"

        internal const val suffix = "</p>\n"
    }

}

fun formatHtml(content: String): String {
    return H5Style.prefix + content + H5Style.suffix
}