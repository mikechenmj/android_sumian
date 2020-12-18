// IMusicServiceAidl.aidl
package com.sumian.sd;

import com.sumian.sd.ICallbackAidl;
import com.sumian.sd.common.h5.music.bean.MusicInfo;

interface IMusicServiceAidl {

    void startMusic();

    void pauseMusic();

    void switchMusic(String path);

    void stopMusic();

    void seekMusic(int msec, int status);

    MusicInfo getMusicInfo();

    oneway void setCallback(ICallbackAidl callback);
}
