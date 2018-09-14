package com.sumian.hw.device.pattern

import android.content.Context
import com.sumian.sd.app.AppManager

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/14 14:35
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SyncPatternManager {

    companion object {
        fun syncPatternInPossible(context: Context) {
            if (AppManager.getBlueManager().isBluePeripheralConnected) {
                SyncPatternService.start(context)
            }
        }
    }
}