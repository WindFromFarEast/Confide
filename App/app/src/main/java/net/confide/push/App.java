package net.confide.push;

import com.igexin.sdk.PushManager;

import net.confide.common.app.Application;
import net.confide.factory.Factory;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Factory
        Factory.setup();
        //初始化推送框架
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        //
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
    }
}
