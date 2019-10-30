package app.woojeong.happyboom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.multidex.MultiDexApplication;

import com.kakao.auth.KakaoSDK;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GlobalApplication extends MultiDexApplication {

    private static GlobalApplication mInstance;
    private static volatile Activity currentActivity = null;
    public static ApplicationLifecycleHandler applicationLifecycleHandler;
    public static boolean getOtherLogin = false;

    public static Activity getCurrentActivity() {
        Log.d("TAG", "++ currentActivity : " + (currentActivity != null ? currentActivity.getClass().getSimpleName() : ""));
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    public static boolean setDarkMode(Activity activity, boolean darkMode) {
        String brand = Build.BRAND;
        boolean success = false;
        if (brand.contains("Meizu")) {
            success = setMeizuDarkMode(activity, darkMode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int lFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
            activity.getWindow().getDecorView().setSystemUiVisibility(darkMode ? (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
            success = true;
        }
        return success;
    }

    private static boolean setMeizuDarkMode(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * singleton
     * @return singleton
     */
    public static GlobalApplication getGlobalApplicationContext() {
        if (mInstance == null)
            throw new IllegalStateException("this application does not inherit GlobalApplication");
        return mInstance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mInstance = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        KakaoSDK.init(new KakaoSDKAdapter());
        applicationLifecycleHandler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(applicationLifecycleHandler);
        registerComponentCallbacks(applicationLifecycleHandler);
    }
}