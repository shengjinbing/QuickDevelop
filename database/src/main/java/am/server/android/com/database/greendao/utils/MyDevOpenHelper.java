package am.server.android.com.database.greendao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import am.server.android.com.database.greendao.DaoMaster;
import am.server.android.com.database.greendao.StudentBeanDao;


/**
 * Created by lixiang
 * on 2018/12/24
 */
public class MyDevOpenHelper extends DaoMaster.DevOpenHelper {

    public MyDevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
       // super.onUpgrade(db, oldVersion, newVersion);
        //操作数据库的更新 有几个表升级都可以传入到下面
        MigrationHelper.getInstance().migrate(db, StudentBeanDao.class);
    }
}
