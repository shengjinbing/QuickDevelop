package com.sensorsdata.analytics.android.app;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.btn_onclick);
        button.setOnClickListener(v -> Toast.makeText(getApplicationContext(),"hahah",Toast.LENGTH_LONG).show());
    }
}
