package am.server.android.com.database.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import am.server.android.com.database.R;
import am.server.android.com.database.adapters.GreendaoAdapter;
import am.server.android.com.database.greendao.DaoSession;
import am.server.android.com.database.greendao.utils.DaoManager;
import am.server.android.com.database.greendao.utils.StudentBean;


/**
 * 1.性能最大化，可能是Android平台上最快的ORM框架
 * 2.易于使用的API
 * 3.最小的内存开销
 * 4.依赖体积小
 * 5.支持数据库加密
 * 6.强大的社区支持
 */
public class GreenDaoActivity extends AppCompatActivity implements View.OnClickListener {
    private Button instablish;
    private Button insert;
    private Button upgrade;
    private Button modify;
    private Button delete;
    private Button query;
    private Button delete_database;
    private EditText mEt;

    private GreendaoAdapter mWordListAdapter;
    private DaoSession mDaoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_dao);
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
        mEt.setText("1");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWordListAdapter = new GreendaoAdapter(this);
        recyclerView.setAdapter(mWordListAdapter);
    }

    private void initData() {
        mDaoSession = DaoManager.getInstance().getDaoSession();
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
        int id = Integer.parseInt(mEt.getText().toString().trim());
        switch (v.getId()) {
            case R.id.instablish:
                //点击创建数据库库
                break;
            case R.id.upgrade:
                //点击更新数据
                upgrade();
                break;
            case R.id.insert:
                //点击插入数据到数据库
                insert(id);
                break;
            case R.id.query:
                //点击查询数据库
                query();
                break;
            case R.id.modify:
                //点击修改数据
                update(id);
                break;
            case R.id.delete:
                //点击删除数据
                delete(id);
                break;
            case R.id.delete_database:
                //点击删除数据库
                break;
            default:
                break;

        }
    }

    /**
     * 创建临时表–>删除原表–>创建新表–>复制临时表数据到新表并删除临时表；这样数据库表的更新就完成了
     * 更新数据库
     */
    private void upgrade() {

    }

    /**
     * //删除全部
     * 1.mUserDao.deleteAll();
     * 2.delete(T entity)：从数据库中删除给定的实体
     * 3.deleteByKey(K key)：从数据库中删除给定Key所对应的实体
     * 4.deleteInTx(T... entities)：使用事务操作删除数据库中给定的实体
     * 5.deleteInTx(<T> entities)：使用事务操作删除数据库中给定实体集合中的实体
     * 6.deleteByKeyInTx(K... keys)：使用事务操作删除数据库中给定的所有key所对应的实体
     * 7.deleteByKeyInTx(Iterable<K> keys)：使用事务操作删除数据库中给定的所有key所对应的实体
     *
     * @param id
     */
    private void delete(int id) {
       mDaoSession.getStudentBeanDao().deleteByKeyInTx((long)id);
        //mDaoSession.delete(new StudentBean( (long)id, id + "你好啊我被删除了", id));
    }

    /**
     * 1.update(T entity) ：更新给定的实体
     * <p>
     * 2.updateInTx(Iterable<T> entities) ：使用事务操作，更新给定的实体
     * <p>
     * 3.updateInTx(T... entities)：使用事务操作，更新给定的实体
     *
     * @param id
     */
    private void update(int id) {
        mDaoSession.update(new StudentBean((long) id, id + "你好啊我被更新了", id,"河南"));
    }

    /**
     * //查询全部
     * mUserDao.loadAll();
     * <p>
     * //根据主键获取对象，也就是通过id获取
     * mUserDao.load(Long key)
     * <p>
     * //根据行号查找数据
     * loadByRowId(long rowId)
     */
    private void query() {
        List<StudentBean> list = mDaoSession.queryBuilder(StudentBean.class).list();
        mWordListAdapter.setWords(list);
    }

    /**
     * 1.insert(User entity)：插入一条记录, 当指定主键在表中存在时会发生异常
     * 2.insertOrReplace(User entity) ：当指定主键在表中存在时会覆盖数据,有该数据时则更新
     * 3.save 类似于insertOrReplace，区别在于save会判断传入对象的key，有key的对象执行更新，
     * 无key的执行插入。当对象有key但并不在数据库时会执行失败.适用于保存本地列表。
     * 4.在确保插入数据有key时必须存在于数据库的情况下，适用save更高效。其他情况一律适用insertOrReplace
     *
     * @param id
     */
    private void insert(int id) {
        mDaoSession.insertOrReplace(new StudentBean((long) id, id + "张三", id,"河南"));
    }
}
