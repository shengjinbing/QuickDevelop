package com.modesty.quickdevelop.utils.permissiongen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.modesty.quickdevelop.base.BaseApplication;
import com.modesty.quickdevelop.utils.permissiongen.internal.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by namee on 2015. 11. 17..
 */
public class PermissionGen {
    private String[] mPermissions;
    private int mRequestCode;
    private Object object;

    private PermissionGen(Object object) {
        this.object = object;
    }

    public static PermissionGen with(Activity activity) {
        return new PermissionGen(activity);
    }

    public static PermissionGen with(Fragment fragment) {
        return new PermissionGen(fragment);
    }

    public PermissionGen permissions(String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    public PermissionGen addRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    public void request() {
        if (checkPermission(BaseApplication.getAppContext().getApplicationContext(), mPermissions)) {
            doExecuteSuccess(object, mRequestCode);
            return;
        }
        requestPermissions(object, mRequestCode, mPermissions);
    }

    public static boolean checkPermission(Context context, String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        boolean flag = true;
        for (String permission : permissions) {
            if (!checkPermission(context, permission)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断是否拥有权限
     */
    public static boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                // Logger.e("no %s permissionResult", permission);
                return false;
            } else {
                //Logger.e("have %s permissionResult", permission);
            }
        }
        return true;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static void requestPermissions(Object object, int requestCode, String[] permissions) {
        if (!Utils.isOverMarshmallow()) {
            doExecuteSuccess(object, requestCode);
            return;
        }
        List<String> deniedPermissions = Utils.findDeniedPermissions(Utils.getActivity(object), permissions);

        if (deniedPermissions.size() > 0) {
            if (object instanceof Activity) {
                ((Activity) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else if (object instanceof Fragment) {
                ((Fragment) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else {
                throw new IllegalArgumentException(object.getClass().getName() + " is not supported");
            }

        } else {
            doExecuteSuccess(object, requestCode);
        }
    }


    private static void doExecuteSuccess(Object activity, int requestCode) {
        if (activity == null) {
            return;
        }
        Method executeMethod = Utils.findMethod(activity.getClass(), PermissionSuccess.class);
        executeMethod(activity, executeMethod, requestCode);

    }

    private static void doExecuteFail(Object activity, int requestCode, List<String> deniedInfo) {
        if (activity == null) {
            return;
        }
        Method executeMethod = Utils.findMethod(activity.getClass(), PermissionFail.class);
        executeMethod(activity, executeMethod, requestCode, deniedInfo);
    }

    private static void executeMethod(Object activity, Method executeMethod, Object... arguments) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) {
                    executeMethod.setAccessible(true);
                }
                executeMethod.invoke(activity, arguments);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onRequestPermissionsResult(final Object obj, final int requestCode, String[] permissions,
                                                  int[] grantResults) {
        Activity activity = null;
        if (obj instanceof Activity) {
            activity = (Activity) obj;
        }
        if (obj instanceof Fragment) {
            activity = ((Fragment) obj).getActivity();
        }
        if (activity != null) {
            StringBuilder permissionInfo = new StringBuilder();
            boolean isShowRequest = true;
            final List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                        isShowRequest = false;
                        permissionInfo.append("未获取").append(PermissionChecker.getDenialPermission(permissions[i])).append("权限\n");
                    }
                }
            }
            if (deniedPermissions.size() > 0) {
                //如果用户点击了不再提示，出现弹框让用户手动授权
                if (!isShowRequest) {
                    //检测到有用户设置永远禁止权限申请后，弹框给用户引导
                   //showPermissionDialog(activity, permissionInfo.toString(), obj, requestCode, deniedPermissions);

                } else {
                    doExecuteFail(obj, requestCode, deniedPermissions);
                }
            } else {
                doExecuteSuccess(obj, requestCode);
            }
        }
    }

 /*   private static void showPermissionDialog(Activity activity, String permissionInfo, Object obj, int requestCode, List<String> deniedPermissions) {
        CustomDialog customDialog = new CustomDialog(activity);
        customDialog.setTitle("权限提示");
        customDialog.setCancelable(false);
        customDialog.setContent(permissionInfo);
        customDialog.setOnOkClickListener("去设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, PERMISSION_SETTINGS);
            }
        });
        customDialog.setOnCancelListener("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExecuteFail(obj, requestCode, deniedPermissions);
            }
        });
        customDialog.show();
    }*/
}
