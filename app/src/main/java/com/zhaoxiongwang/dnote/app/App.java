package com.zhaoxiongwang.dnote.app;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by XhinLiang on 2017/5/13.
 * xhinliang@gmail.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "Q15JQz7PeMsl5MNRwGcvM8ep-gzGzoHsz", "ncxCwP65dHQ7X8W1S8mERLFc");
    }
}
