package com.example.ar_ruler;

import com.blankj.utilcode.util.ToastUtils;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.ux.ArFragment;

public class MyArFragment extends ArFragment {
    @Override
    protected void handleSessionException( UnavailableException sessionException) {
        String message;
        if (sessionException instanceof UnavailableArcoreNotInstalledException) {
            message = "Please Install ARCore";
        } else if (sessionException instanceof UnavailableApkTooOldException) {
            message = "Please upgrade ARCore";
        } else if (sessionException instanceof UnavailableSdkTooOldException) {
            message = "Please upgrade the app";
        } else if (sessionException instanceof UnavailableDeviceNotCompatibleException) {
            message = "Your device does not support AR Core";
        } else {
            message = "AR Core Initialization Error";
        }
        ToastUtils.showLong(message, new Object[0]);
    }
}
