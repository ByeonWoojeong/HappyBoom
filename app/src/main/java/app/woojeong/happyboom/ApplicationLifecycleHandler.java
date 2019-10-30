package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.NotificationManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

public class ApplicationLifecycleHandler implements ActivityLifecycleCallbacks, ComponentCallbacks2 {

    Context context;

    private boolean isInBackground = false;

    public final boolean isInBackground() {
        return isInBackground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        context = activity.getBaseContext();
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isInBackground) {
            isInBackground = false;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(2554);

        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isInBackground = true;
        }
    }
}