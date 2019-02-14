package com.modesty.quickdevelop.utils.permissiongen.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.modesty.quickdevelop.utils.permissiongen.PermissionFail;
import com.modesty.quickdevelop.utils.permissiongen.PermissionSuccess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by namee on 2015. 11. 18..
 */
final public class Utils {
    private Utils() {
    }

    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

//    private static boolean isEqualRequestCodeFromAnnotation(Method m, Class clazz, int requestCode) {
//        if (clazz.equals(PermissionFail.class)) {
//            return requestCode == m.getAnnotation(PermissionFail.class).requestCode();
//        } else if (clazz.equals(PermissionSuccess.class)) {
//            return requestCode == m.getAnnotation(PermissionSuccess.class).requestCode();
//        } else {
//            return false;
//        }
//    }

    private static boolean isEqualMethodFromAnnotation(Method m, Class clazz) {
        return clazz.equals(PermissionFail.class) || clazz.equals(PermissionSuccess.class);
    }

    public static <A extends Annotation> Method findMethod(Class clazz, Class<A> annotation) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                if (isEqualMethodFromAnnotation(method, annotation)) {
                    return method;
                }
            }
        }
        return null;
    }

    public static Activity getActivity(Object object) {
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof Activity) {
            return (Activity) object;
        }
        return null;
    }
}
