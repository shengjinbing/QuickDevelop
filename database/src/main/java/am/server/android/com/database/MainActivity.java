package am.server.android.com.database;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import am.server.android.com.database.activitys.GreenDaoActivity;
import am.server.android.com.database.activitys.RoomActivity;
import am.server.android.com.database.activitys.SqliteActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sqlite(View view) {
        startActivity(new Intent(this, SqliteActivity.class));
    }

    public void room(View view) {
        startActivity(new Intent(this, RoomActivity.class));
    }

    public void greenDao(View view) {
        startActivity(new Intent(this, GreenDaoActivity.class));
    }
}