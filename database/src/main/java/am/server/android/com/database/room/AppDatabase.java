package am.server.android.com.database.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by ${lixiang} on 2018/6/26.
 * 升级的时候需要提升版本号
 *
 */
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE = "room_database";

    public abstract UserDao userDao();

    private static AppDatabase INSTANCE;


    /**
     *
     * 两种方式获取Database对象的区别:
     * Room.databaseBuilder()：生成Database对象，并且创建一个存在文件系统中的数据库。
     * Room.inMemoryDatabaseBuilder()：生成Database对象并且创建一个存在内存中的数据库。当应用退出的时候(应用进程关闭)数据库也消失。
     *
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE)
                            .addCallback(sRoomDatabaseCallback)
                            //.allowMainThreadQueries()
                            //.addMigrations(MIGRATION_1_2) //数据库升级
                            .build();

                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final UserDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.userDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //mDao.deleteAll();
            //g刚创建数据库可以进行一些操作，比如添加数据
            Log.d("BBBBB", "PopulateDbAsync");
            return null;
        }
    }

    @Override
    public void clearAllTables() {

    }

    /**
     * 数据库版本 1->2 user表格新增了age列
     */
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //进行数据库升级
            //database.execSQL("ALTER TABLE User ADD COLUMN age integer");
        }
    };
}
