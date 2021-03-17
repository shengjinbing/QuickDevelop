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
    public void
    modify(UserBean bean) {
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

   /*
   微信WCDB进化之路 - 开源与开始
   https://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=2649286603&idx=1&sn=d243dd27f2c6614631241cd00570e853&chksm=8334c349b4434a5fd81809d656bfad6072f075d098cb5663a85823e94fc2363edd28758ab882&mpshare=1&scene=1&srcid=0609GLAeaGGmI4zCHTc2U9ZX#rd
   微信ANDROID客户端-会话速度提升70%的背后
https://mp.weixin.qq.com/s?__biz=MzAwNDY1ODY2OQ==&mid=207548094&idx=1&sn=1a277620bc28349368b68ed98fbefebe&scene=21#wechat_redirect



   Cursor 实现优化

    Android 框架查询数据库使用的是 Cursor 接口，调用 SQLiteDatabase.query(...) 会返回一个Cursor 对象，之后就可以
    使用 Cursor 遍历结果集了。Android SDK SQLite Cursor 的实现是分配一个固定 2MB 大小的缓冲区，称作 Cursor Window，
    用于存放查询结果集。

    查询时，先分配Cursor Window，然后执行 SQL 获取结果集填充之，直到 Cursor Window 放满或者遍历完结果集，之后将 Cursor
    返回给调用者。

    假如 Cursor 遍历到缓冲区以外的行，Cursor 会丢弃之前缓冲区的所有内容，重新查询，跳过前面的行，重新选定一个开始位置填充
    Cursor Window 直到缓冲区再次填满或遍历完结果集。

    这样的实现能保证大部分情况正常工作，在很多情况下却不是最优实现。微信对 DB 操作最多的场景是获取 Cursor 直接遍历获取数据
    后关闭，获取到的数据，一般是生成对应的实体对象（通过 ORM 或者自行从 Cursor 转换）后放到 List 或 Map 等容器里返回，
    或用于显示，或用于其他逻辑。

    在这种场景下，先将数据保存到 Cursor Window 后再取出，中间要经历两次内存拷贝和转换（SQLite → CursorWindow → Java），
    这是完全没有必要的。另外，由于 Cursor Window 是定长的，对于较小的结果集，需要无故分配 2MB 内存，对于大结果集，如果 2MB
    不足以放下，遍历到途中还会引发 Cursor 重查询，这个消耗就相当大了。

    Cursor Window，其实也是在 JNI 层通过 SQLite 库的 Statement 填充的，Statement 这里可以理解为一个轻量但只能往前
    遍历，没有缓存的 Cursor。这个不就跟我们的场景一致吗？何不直接使用底层的 Statement 呢？我们对 Statement 做了简单
    的封装，暴露了 Cursor 接口， SQLiteDirectCursor 就诞生了，它直接操作底层 SQLite 获取数据，只能执行往前迭代的操作，
    但这完全满足需要。

    图片这样，在大部分不需要将 Cursor 传递出去的场景，能很好的解决 Cursor 的额外消耗，特别是结果集大于 2MB 的场合。*/
}
