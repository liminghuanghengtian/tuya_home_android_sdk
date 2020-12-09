package com.tuya.smart.android.demo.base.presenter;


import com.tuya.smart.android.demo.base.bean.RgbBean;

/**
 * Created by letian on 15/12/10.
 */
public interface LampOperationFactory {
    void showOperationView();

    void hideOperationView();

    void updateOperationView(RgbBean rgbBean);


}
