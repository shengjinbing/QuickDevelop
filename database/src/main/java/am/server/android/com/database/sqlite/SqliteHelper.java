package am.server.android.com.database.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import am.server.android.com.database.bean.UserBean;


/**
 * Created by lixiang
 * on 2018/12/24
 */
public class SqliteHelper {

    private MySQLiteOpenHelper mDbHelper;

    private SqliteHelper() {
    }

    private static SqliteHelper instance;

    public static SqliteHelper getInstance() {
        if (instance == null) {
            synchronized (SqliteHelper.class) {
                if (instance == null) {
                    instance = new SqliteHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 创建SQLiteOpenHelper子类对象
     *
     * @param context
     * @param database
     */
    public void initSqlite(Context context, String database) {
        if (mDbHelper == null) {
            mDbHelper = new MySQLiteOpenHelper(context, database);
        }
    }

    /**
     * 数据库实际上是没有被创建或者打开的，直到getWritableDatabase() 或者 getReadableDatabase()
     * 方法中的一个被调用时才会进行创建或者打开
     *
     * @return
     */
    public SQLiteDatabase getWritableDatabase() {
        //内部使用synchronized
        return mDbHelper.getWritableDatabase();
    }

    /**
     * @return
     */
    public SQLiteDatabase getReadbleDatabase() {
        //内部使用synchronized
        return mDbHelper.getReadableDatabase();
    }

    /**
     * 升级数据库
     * 创建SQLiteOpenHelper子类对象
     *
     * @param context
     * @param database
     * @param version
     */
    public void onUpgrade(Context context, String database, int version) {
        MySQLiteOpenHelper dbHelper_upgrade = new MySQLiteOpenHelper(context, database, version);
        dbHelper_upgrade.getWritableDatabase();
        // SQLiteDatabase  sqliteDatabase = dbHelper.getReadbleDatabase();
    }

    /**
     * 插入数据
     * @param userBean
     */
    public void insert(UserBean userBean) {
        SQLiteDatabase database = getWritableDatabase();
        // 创建ContentValues对象
        ContentValues values = new ContentValues();
        // 向该对象中插入键值对
        values.put("id", userBean.getId());
        values.put("name", userBean.getName());
        values.put("age",userBean.getAge());
        values.put("sex",userBean.getSex());
        values.put("height",userBean.getHeight());
        // 调用insert()方法将数据插入到数据库当中
         database.insert("user", null, values);
        //或者使用SQL语句
        //database.execSQL("insert into user (id,name) values (10,'carson')");
        database.close();
    }

    /**
     * 删除数据
     * @param id
     */
    public void delete(String id) {
        SQLiteDatabase database = getWritableDatabase();
        //删除数据
        database.delete("user", "id=?", new String[]{id});
        //关闭数据库
        database.close();
    }

    /**
     * 修改数据
     */
    public void modify(UserBean bean) {
        // 创建一个DatabaseHelper对象
        // 将数据库的版本升级为2
        // 传入版本号为2，大于旧版本（1），所以会调用onUpgrade()升级数据库
        SQLiteDatabase database = getWritableDatabase();
        // 创建一个ContentValues对象
        ContentValues values2 = new ContentValues();
        values2.put("name", bean.getName());
        // 调用update方法修改数据库
        database.update("user", values2, "id=?", new String[]{String.valueOf(bean.getId())});
        database.close();
    }


    /**
     * 根据id查询数据
     * @param id1
     * @return
     */
    public List<UserBean> queryWhereId(String id1) {
        List<UserBean> beanList = new ArrayList<>();
        SQLiteDatabase database = getReadbleDatabase();
        // 调用SQLiteDatabase对象的query方法进行查询
        // 返回一个Cursor对象：由数据库查询返回的结果集对象
        Cursor cursor = database.query("user", new String[]{"id",
                "name"}, "id=?", new String[]{id1}, null, null, null);

        //将光标移动到下一行，从而判断该结果集是否还有下一条数据
        //如果有则返回true，没有则返回false

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            //输出查询结果
            UserBean userBean = new UserBean();
            userBean.setId(id);
            userBean.setName(name);
            beanList.add(userBean);
        }
        //关闭数据库
        database.close();
        return beanList;
    }

    /**
     * 查询所有数据
     * @return
     */
    public List<UserBean> query() {
        List<UserBean> beanList = new ArrayList<>();
        SQLiteDatabase database = getReadbleDatabase();
        // 调用SQLiteDatabase对象的query方法进行查询
        // 返回一个Cursor对象：由数据库查询返回的结果集对象
        Cursor cursor = database.query("user", new String[]{"id",
                "name","age","sex","height"}, null, null, null, null, null);

        //将光标移动到下一行，从而判断该结果集是否还有下一条数据
        //如果有则返回true，没有则返回false

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String age = cursor.getString(cursor.getColumnIndex("age"));
            String sex = cursor.getString(cursor.getColumnIndex("sex"));
            int height = cursor.getInt(cursor.getColumnIndex("height"));
            //输出查询结果
            UserBean userBean = new UserBean();
            userBean.setId(id);
            userBean.setName(name);
            userBean.setAge(age);
            userBean.setSex(sex);
            userBean.setHeight(height);
            beanList.add(userBean);
        }
        //关闭数据库
        database.close();
        return beanList;
    }

    /**
     * 删除数据库
     *
     * @param name
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void deleteDataBase(File name){
        SQLiteDatabase database = getWritableDatabase();
        //删除名为test.db数据库
        SQLiteDatabase.deleteDatabase(name);
    }
}
