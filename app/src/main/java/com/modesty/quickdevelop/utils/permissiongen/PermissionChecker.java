package com.modesty.quickdevelop.utils.permissiongen;

import android.Manifest;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by congtaowang 2016/10/19.
 */

public class PermissionChecker {

    public static final class Permissions {

        public static final String[] STORAGE_WRITE_READ = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        public static final String[] CAMERA = {
                Manifest.permission.CAMERA
        };

        public static final String[] CAMERA_STORAGE = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        public static final String[] CAMERA_VIDEO_STORAGE = {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        public static final String[] LOCATION = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        public static final String[] PHONE = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
        public static final String[] RECORD_AUDIO = {Manifest.permission.RECORD_AUDIO};

        public static final String[] IMPORTANT_PERMISSION = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.RECORD_AUDIO
        };

        public static final String[] CONTACTS = {
                Manifest.permission.READ_CONTACTS
                /*   Manifest.permission.SEND_SMS*/
        };

        public static final String[] WINDOWS = {
                Manifest.permission.SYSTEM_ALERT_WINDOW
        };
    }


    /**
     * {@link android.support.v4.app.FragmentActivity#validateRequestPermissionsRequestCode(int)}
     */
    public static final class PermissionRequestCode {
        public static final int STORAGE_WRITE_READ = 0x01;
        public static final int CAMERA = 0x02;
        public static final int CAMERA_STORAGE_WRITE_READ = 0x03;
    }

    private final static Map<String, String> permissions = new HashMap<>();

    static {
        permissions.put(Manifest.permission.READ_EXTERNAL_STORAGE, "读取存储卡内容");
        permissions.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入文件到存储卡");
        permissions.put(Manifest.permission.CAMERA, "照相功能");
        permissions.put(Manifest.permission.READ_CALL_LOG, "读取通话日志");
        permissions.put(Manifest.permission.READ_PHONE_STATE, "电话状态");
        permissions.put(Manifest.permission.CALL_PHONE, "拨打电话");
        permissions.put(Manifest.permission.USE_SIP, "");
        permissions.put(Manifest.permission.PROCESS_OUTGOING_CALLS, "");
        permissions.put(Manifest.permission.ADD_VOICEMAIL, "");
        permissions.put(Manifest.permission.READ_CALENDAR, "日历");
        permissions.put(Manifest.permission.WRITE_CALENDAR, "修改日历");
        permissions.put(Manifest.permission.BODY_SENSORS, "传感器");
        permissions.put(Manifest.permission.ACCESS_FINE_LOCATION, "访问精确位置");
        permissions.put(Manifest.permission.ACCESS_COARSE_LOCATION, "访问大概位置");
        permissions.put(Manifest.permission.RECORD_AUDIO, "录音");
        permissions.put(Manifest.permission.READ_SMS, "读取短信息");
        permissions.put(Manifest.permission.RECEIVE_WAP_PUSH, "接收WAP推送");
        permissions.put(Manifest.permission.RECEIVE_MMS, "接收彩信");
        permissions.put(Manifest.permission.RECEIVE_SMS, "接收短信");
        permissions.put(Manifest.permission.SEND_SMS, "发送短信");
        permissions.put(Manifest.permission.SYSTEM_ALERT_WINDOW, "系统window");
    }

    static String getDenialPermissionDescribeInfo(List<String> denialPermissions) {
        if (denialPermissions == null || denialPermissions.size() == 0) {
            return null;
        }
        StringBuilder rationale = new StringBuilder();
        for (String denialPermission : denialPermissions) {
            if (permissions.containsKey(denialPermission)) {
                rationale.append(permissions.get(denialPermission)).append(",");
            }
        }
        if (!TextUtils.isEmpty(rationale.toString())) {
            rationale.deleteCharAt(rationale.length() - 1);
        }
        return rationale.toString();
    }

    static String getDenialPermission(String denialPermission) {
        if (permissions.containsKey(denialPermission)) {
            return permissions.get(denialPermission);
        }
        return "";
    }
}
