//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.paymob.acceptsdk;

import android.app.Activity;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

class CustomAlertShower {
    CustomAlertShower() {
    }

    static void showSweetAlertAndNotify(Activity activity, String title, String message, int style, String buttonTitle, OnSweetClickListener listener) {
        (new SweetAlertDialog(activity, style)).setTitleText(title).setContentText(message).setConfirmText(buttonTitle).setConfirmClickListener(listener).show();
    }

    static void showSweetAlert(Activity activity, String title, String message, int style, String buttonTitle) {
        (new SweetAlertDialog(activity, style)).setTitleText(title).setContentText(message).setConfirmText(buttonTitle).show();
    }
}
