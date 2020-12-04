package am.server.android.com.database.activitys;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import am.server.android.com.database.R;
import am.server.android.com.database.adapters.CusAdapter;
import am.server.android.com.database.bean.UserBean;
import am.server.android.com.database.sqlite.SqliteHelper;


/**
 *1.数据库的范式
 *    1、第一范式 属性的原子性
 *    所谓的第一范式就是数据库中的每一列都是不可分割的基本数据项，同一列中不能有多个值，即实体中的某个属性不能有
 *    多个值或者不能有重复的属性，如果出现重复的属性则需要重新构建实体，新的实体由重复的属性构成。
 *    第二范式 属性完全依赖于主键
 *    2、第二范式是在第一范式的基础上建立起来的，即满足第二范式必须先满足第一范式，第二范式要求数据库的每个实例或行
 *    必须可以被唯一的区分，即表中要有一列属性可以将实体完全区分，这个属性就是主键，即每一个属性完全依赖于主键，
 *    在员工管理中，员工可以通过员工编号进行唯一区分,
 *    完全依赖概念：即非主属性不能依赖于主键的部分属性，必须依赖于主键的所有属性
 *    3、第三范式
 *    满足第三范式必须先满足第二范式，第三范式要求一个数据库表中不包含已在其他表中已包含的非主关键字信息， 例如
 *    存在一个课程表，课程表中有课程号(Cno),课程名(Cname),学分(Ccredit)，那么在学生信息表中就没必要再把课
 *    程名，学分再存储到学生表中，这样会造成数据的冗余， 第三范式就是属性不依赖与其他非主属性，也就是说，如果存
 *    在非主属性对于码的传递函数依赖，则不符合第三范式
 *    https://blog.csdn.net/qq_43079376/article/details/93647335
 * 2.数据库中事务的特性
 *   1、原子性(Atomicity)：事务中的全部操作在数据库中是不可分割的，要么全部完成，要么全部不执行。
 *   2、一致性(Consistency)：几个并行执行的事务，其执行结果必须与按某一顺序 串行执行的结果相一致。
 *   3、隔离性(Isolation)：事务的执行不受其他事务的干扰，事务执行的中间结果对其他事务必须是透明的。
 *   4、持久性(Durability):对于任意已提交事务，系统必须保证该事务对数据库的改变不被丢失，即使数据库出现故障。
 *   事务的ACID特性是由关系数据库系统(DBMS)来实现的，DBMS采用日志来保证事务的原子性、一致性和持久性。日志记录了
 *   事务对数据库所作的更新，如果某个事务在执行过程中发生错误，就可以根据日志撤销事务对数据库已做的更新，使得数据库
 *   回滚到执行事务前的初始状态。对于事务的隔离性，DBMS是采用锁机制来实现的。当多个事务同时更新数据库中相同的数据
 *   时，只允许持有锁的事务能更新该数据，其他事务必须等待，直到前一个事务释放了锁，其他事务才有机会更新该数据。
 */
public class SqliteActivity extends AppCompatActivity implements View.OnClickListener {
    private Button instablish;
    private Button insert;
    private Button upgrade;
    private Button modify;
    private Button delete;
    private Button query;
    private Button delete_database;
    private EditText mEt;

    private CusAdapter mWordListAdapter;
    private SqliteHelper mSqliteHelper;

    private static final String DATABASE = "sqlite_database";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        //绑定按钮
        instablish = (Button) findViewById(R.id.instablish);
        insert = (Button) findViewById(R.id.insert);
        upgrade = (Button) findViewById(R.id.upgrade);
        modify = (Button) findViewById(R.id.modify);
        delete = (Button) findViewById(R.id.delete);
        query = (Button) findViewById(R.id.query);
        delete_database = (Button) findViewById(R.id.delete_database);
        mEt = (EditText) findViewById(R.id.et);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWordListAdapter = new CusAdapter(this);
        recyclerView.setAdapter(mWordListAdapter);
    }

    private void initData() {
    }

    private void initListener() {
        //设置监听器
        instablish.setOnClickListener(this);
        insert.setOnClickListener(this);
        upgrade.setOnClickListener(this);
        modify.setOnClickListener(this);
        delete.setOnClickListener(this);
        query.setOnClickListener(this);
        delete_database.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String id = mEt.getText().toString().trim();
        switch (v.getId()) {
            case R.id.instablish:
                //点击创建数据库库
                mSqliteHelper = SqliteHelper.getInstance();
                mSqliteHelper.initSqlite(this,DATABASE);
                break;
            case R.id.upgrade:
                //点击更新数据
                mSqliteHelper.onUpgrade(this, DATABASE, 2);
                break;
            case R.id.insert:
                //点击插入数据到数据库
                UserBean userBean = new UserBean();
                userBean.setId(Integer.parseInt(id));
                userBean.setName(id + "李四");
                userBean.setSex("男");
                userBean.setAge("12");
                userBean.setHeight(180);
                mSqliteHelper.insert(userBean);
                break;
            case R.id.query:
                //点击查询数据库
                mWordListAdapter.setWords(mSqliteHelper.query());
                break;
            case R.id.modify:
                //点击修改数据
                UserBean modify = new UserBean();
                modify.setId(Integer.parseInt(id));
                modify.setName(id + "王五");
                mSqliteHelper.modify(modify);
                break;
            case R.id.delete:
                //点击删除数据
                mSqliteHelper.delete(id);
                break;
            case R.id.delete_database:
                //点击删除数据库
                SQLiteDatabase database = mSqliteHelper.getWritableDatabase();
                String path = database.getPath();
                mSqliteHelper.deleteDataBase(new File(path));
                break;
            default:
                break;

        }
    }
}
