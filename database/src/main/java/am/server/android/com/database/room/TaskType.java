package am.server.android.com.database.room;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ${lixiang} on 2018/6/27.
 */
@IntDef({
        TaskType.INSERT,
        TaskType.DELETE,
        TaskType.UPDATA
})
@Retention(RetentionPolicy.SOURCE)
public @interface TaskType {
    int INSERT = 1;
    int DELETE = 2;
    int UPDATA = 3;
}
