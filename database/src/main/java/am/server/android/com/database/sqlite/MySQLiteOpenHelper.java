package am.server.android.com.database.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 * 文件锁没有线程锁类似pthread_cond_signal的通知机制。当一个进程的数据库操作结束时，无法通过锁来第一时间通知到其他
 * 进程进行重试。因此只能退而求其次，通过多次休眠来进行尝试。
 *
 * 1.多进程并发
 *   默认支持多进程并发，它通过文件锁来控制多进程并发。SQLite锁力度并没有很细，它针对整个DB文件，内部有5个状态
     UNLOCKED	数据库上没有锁。数据库既不可读也不可写。任何内部缓存的数据都被认为是可疑的，并且在使用之前需要对数据
                库文件进行验证。其他进程可以读取或写入数据库，因为它们自己的锁定状态允许。这是默认状态。

     SHARED	可以读取数据库但不写入数据库。任意数量的进程可以同时保存SHARED锁，因此可以有许多同时读取器。但是，当一个
            或多个SHARED锁处于活动状态时，不允许其他线程或进程写入数据库文件。

     RESERVED	RESERVED锁意味着进程计划在将来的某个时刻写入数据库文件，但它当前只是从文件中读取。虽然多个SHARED
               锁可以与单个RESERVED锁共存，但一次只能激活一个RESERVED锁。RESERVED与PENDING的不同之处在于，
               当存在RESERVED锁时，可以获取新的SHARED锁。

     PENDING	PENDING锁意味着持有锁的进程想要尽快写入数据库，并且只是等待所有当前的SHARED锁清除，以便它可以获得
                EXCLUSIVE锁。如果PENDING锁处于活动状态，则不允许对数据库使用新的SHARED锁，但允许继续使用现有的SHARED锁。

     EXCLUSIVE	为了写入数据库文件，需要一个EXCLUSIVE锁。文件上只允许一个EXCLUSIVE锁，并且不允许任何其他类型的
                锁与EXCLUSIVE锁共存。为了最大化并发性，SQLite可以最大限度地减少EXCLUSIVE锁定所需的时间。
 * 2.多线程并发
 *   SQLite支持三种不同的线程模式：
 *   (1)单线程。在此模式下，所有互斥锁都被禁用，并且SQLite一次不能在多个线程中使用。
 *   (2)多线程。在这种模式下，只要在两个或多个线程中不同时使用单个数据库连接，SQLite就可以被多个线程安全地使用。
 *   (3)序列化。在序列化模式下，SQLite可以被多个线程安全地使用而没有任何限制。
 *   跟多进程一样，为了实现简单，SQLite锁的粒度也是文件级别的，并没有实现表级别的甚至行级别的锁。
 *   同一个句柄同一时间只有一个线程在操作，这个时候需要我们打开连接池Connection Pool
 *
 *
 *   事务的属性
 * 事务（Transaction）具有以下四个标准属性，通常根据首字母缩写为 ACID：
 * 原子性（Atomicity）：确保工作单位内的所有操作都成功完成，否则，事务会在出现故障时终止，之前的操作也会回滚到以前的状态。
 * 一致性（Consistency)：确保数据库在成功提交的事务上正确地改变状态。
 * 隔离性（Isolation）：使事务操作相互独立和透明。
 * 持久性（Durability）：确保已提交事务的结果或效果在系统发生故障的情况下仍然存在。
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String TAG = "MYSQLITE_LOG";

    //数据库版本号
    private static Integer Version = 1;


    public MySQLiteOpenHelper(Context context, String name) {
        this(context, name, Version);
    }
    //参数说明
    //context:上下文对象
    //name:数据库名称
    //param:factory
    //version:当前数据库的版本，值必须是整数并且是递增的状态

    public MySQLiteOpenHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }


    //在SQLiteOpenHelper的子类当中，必须有该构造函数
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        //必须通过super调用父类当中的构造函数
        super(context, name, factory, version, new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase dbObj) {
            }
        });
    }

    /**
     * 当数据库创建的时候被调用
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("创建数据库和表");
        //db.disableWriteAheadLogging();
        //启用WAL模式，读写并发不会互相阻塞，但是写不能并发，多个写操作会引起SQLiteDatabaseLockedException
        db.enableWriteAheadLogging();
        //设置页缓存的大小位4kb来提升整体的性能
        db.setPageSize(4096);

        //创建了数据库并创建一个叫records的表
        //SQLite数据创建支持的数据类型： 整型数据，字符串类型，日期类型，二进制的数据类型
        String sql = "create table user(id int primary key,name varchar(200),age varchar(200)," +
                "sex varchar(200),height int)";
        //execSQL用于执行SQL语句
        //完成数据库的创建
        db.execSQL(sql);
        db.execSQL("create index index_name on user(name)");
        //数据库实际上是没有被创建或者打开的，直到getWritableDatabase() 或者 getReadableDatabase() 方法中的一个被调用时才会进行创建或者打开


    }

    /**
     * 数据库升级时调用
     * 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade（）方法
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "newVersion=" + newVersion + "oldVersion=" + oldVersion);
        db.execSQL("ALTER TABLE user ADD COLUMN age INTEGER");
    }


}
