package am.server.android.com.database.activitys;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;

import am.server.android.com.database.BuildConfig;
import am.server.android.com.database.R;
import am.server.android.com.database.room.AppDatabase;
import am.server.android.com.database.room.User;
import am.server.android.com.database.room.UserViewModel;
import am.server.android.com.database.room.WordListAdapter;


public class RoomActivity extends AppCompatActivity implements View.OnClickListener {
    private Button instablish;
    private Button insert;
    private Button upgrade;
    private Button modify;
    private Button delete;
    private Button query;
    private Button delete_database;
    private EditText mEt;

    private AppDatabase mDb;
    private AppDatabase mDatabase;
    private UserViewModel mUserViewModel;
    private WordListAdapter mWordListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
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
        mWordListAdapter = new WordListAdapter(this);
        recyclerView.setAdapter(mWordListAdapter);
    }

    private void initData() {
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
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
        //监听数据库数据变化
        mUserViewModel.getAllWords().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                mWordListAdapter.setWords(users);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //点击创建数据库库
            case R.id.instablish:
                showDebugDBAddressLogToast(this);
                mDatabase = AppDatabase.getDatabase(this);
                break;

            //点击更新数据
            case R.id.upgrade:
                break;

            //点击插入数据到数据库
            case R.id.insert:
                String id = mEt.getText().toString();
                User user = new User();
                user.setUid(Integer.parseInt(id));
                user.setFirstName(id + "firstname");
                user.setLastName(id + "LastName");
                mUserViewModel.insert(user);

                break;

            //点击查询数据库
            case R.id.query:
                break;

            //点击修改数据
            case R.id.modify:
                String id1 = mEt.getText().toString();
                User user1 = new User();
                user1.setUid(Integer.parseInt(id1));
                user1.setFirstName(id1 + "first");
                user1.setLastName(id1 + "Last");
                mUserViewModel.update(user1);

                break;

            //点击删除数据
            case R.id.delete:
                String id2 = mEt.getText().toString();
                User user2 = new User();
                user2.setUid(Integer.parseInt(id2));
                mUserViewModel.delete(user2);
                break;

            //点击删除数据库
            case R.id.delete_database:
                break;

            default:
                break;

        }
    }

    public static void showDebugDBAddressLogToast(Context context) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Method getAddressLog = debugDB.getMethod("getAddressLog");
                Object value = getAddressLog.invoke(null);
                Toast.makeText(context, (String) value, Toast.LENGTH_LONG).show();
            } catch (Exception ignore) {

            }
        }
    }
}
