package com.sumian.app.base;

/**
 * Created by jzz
 * on 2017/4/4.
 * <p>
 * desc:
 */

public interface Base2Presenter<T> {

    void attachView(T t);

    void detachView();

}
