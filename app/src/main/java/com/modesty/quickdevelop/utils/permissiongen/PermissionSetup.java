package com.modesty.quickdevelop.utils.permissiongen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.Fragment;


/**
 * Created by congtaowang on 2018/3/23.
 */

public class PermissionSetup {
    public static final int PERMISSION_SETTINGS = 10000;
    public static final int PERMISSION_REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST_CODE_RECORD = 101;
    public static final int PERMISSION_REQUEST_CODE_CAMERA = 102;
    public static final int PERMISSION_FUNC_CALL_PHONE_CODE = 103;//FuncCallPhone bridge


    private static void requestPermissions(Object object, int requestCode, String... permission) {
        if (object == null) {
            //Logger.e("object is null ");
            return;
        }
        if (object instanceof Fragment) {
            PermissionGen.with(((Fragment) object)).addRequestCode(requestCode)
                    .permissions(permission)
                    .request();

        } else if (object instanceof Activity) {
            PermissionGen.with(((Activity) object))
                    .addRequestCode(requestCode)
                    .permissions(permission)
                    .request();
        } else {
            throw new IllegalArgumentException("PermissionGen don't support object but fragment and activity.");
        }
    }


    /**
     * @param object fragment or activity
     */
    public static void requestPermission(Object object, int requestCode, String... permission) {

        requestPermissions(object, requestCode, permission);
    }

    /**
     * @param object fragment or activity
     */
    public static void requestPermission(Object object, String... permission) {

        requestPermissions(object, PERMISSION_REQUEST_CODE, permission);
    }

    private static class PermissionIntent {

        private final Intent intent;
        private PermissionIntent nextIntent;

        public PermissionIntent(Intent intent, PermissionIntent nextIntent) {
            this.intent = intent;
            this.nextIntent = nextIntent;
        }

        public PermissionIntent getNextIntent() {
            return nextIntent;
        }

        public void setNextIntent(PermissionIntent nextIntent) {
            this.nextIntent = nextIntent;
        }

        public void open(Context context) {
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                if (getNextIntent() != null) {
                    getNextIntent().open(context);
                }
            }
        }

    }


/*    public static void open(Context context) {
        final PermissionIntent settings = new PermissionIntent(settings(), null);
        final PermissionIntent appInfoPage = new PermissionIntent(appInfoPage(), settings);
        final PermissionIntent qihoo360 = new PermissionIntent(qihoo360(), appInfoPage);
        final PermissionIntent letv = new PermissionIntent(letv(), qihoo360);
        final PermissionIntent lg = new PermissionIntent(lg(), letv);
        final PermissionIntent oppo = new PermissionIntent(oppo(), lg);
        final PermissionIntent sony = new PermissionIntent(sony(), oppo);
        final PermissionIntent miui = new PermissionIntent(miui(), sony);
        final PermissionIntent meizu = new PermissionIntent(meizu(), miui);
        final PermissionIntent huawei = new PermissionIntent(huawei(), meizu);
        huawei.open(context);
    }*/

    /*private static Intent huawei() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent meizu() {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        return intent;
    }

    private static Intent miui() {
        String rom = Build.MODEL;
        Intent intent = null;
        if ("MI 5".equals(rom)) {
            Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        } else if ("MI 6".equals(rom) || "MI 7".equals(rom)) {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
        }
        return intent;
    }

    private static Intent sony() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent oppo() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent lg() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent letv() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent qihoo360() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent appInfoPage() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        return localIntent;
    }*/

    private static Intent settings() {
        return new Intent(Settings.ACTION_SETTINGS);
    }
}
