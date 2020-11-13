package com.tuya.smart.android.demo.base.utils;

import android.app.Activity;
import android.content.Context;

import com.tuya.smart.android.camera.api.ITuyaHomeCamera;
import com.tuya.smart.android.camera.api.bean.CameraPushDataBean;
import com.tuya.smart.android.common.utils.L;
import com.tuya.smart.android.demo.R;
import com.tuya.smart.android.demo.base.app.Constant;
import com.tuya.smart.android.demo.login.activity.LoginActivity;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.ITuyaGetBeanCallback;


/**
 * Created by letian on 16/7/15.
 */
public class LoginHelper {
    private static final String TAG = "LoginHelper";
    private static ITuyaHomeCamera homeCamera;

    private static ITuyaGetBeanCallback<CameraPushDataBean> mTuyaGetBeanCallback = new ITuyaGetBeanCallback<CameraPushDataBean>() {
        @Override
        public void onResult(CameraPushDataBean o) {
            L.d(TAG, "onMqtt_43_Result on callback");
            L.d(TAG, "timestamp=" + o.getTimestamp());
            L.d(TAG, "devid=" + o.getDevId());
            L.d(TAG, "msgid=" + o.getEdata());
            L.d(TAG, "etype=" + o.getEtype());

        }
    };

    public static void afterLogin() {

        //there is the somethings that need to set.For example the lat and lon;
        //   TuyaSdk.setLatAndLong();
        homeCamera = TuyaHomeSdk.getCameraInstance();
        if (homeCamera != null) {
            homeCamera.registerCameraPushListener(mTuyaGetBeanCallback);
        }
    }

    private static void afterLogout() {
        L.d(TAG, "afterLogout unregister thread " + Thread.currentThread().getName());
        if (homeCamera != null) {
            homeCamera.unRegisterCameraPushListener(mTuyaGetBeanCallback);
        }
        homeCamera = null;
    }

    /**
     * 唤起重新登录
     *
     * @param context
     */
    public static void reLogin(Context context) {
        reLogin(context, true);
    }

    public static void reLogin(Context context, boolean tip) {
        onLogout(context);
        if (tip) {
            ToastUtil.shortToast(context, R.string.login_session_expired);
        }
        ActivityUtils.gotoActivity((Activity) context, LoginActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
    }

    private static void onLogout(Context context) {
        afterLogout();
        exit(context);
    }

    /**
     * 退出应用
     *
     * @param context
     */
    public static void exit(Context context) {
        Constant.finishActivity();
        TuyaHomeSdk.onDestroy();
    }
}
