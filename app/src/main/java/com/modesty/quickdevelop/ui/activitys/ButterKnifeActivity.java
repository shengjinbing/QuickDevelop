package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.modesty.quickdevelop.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ButterKnifeActivity extends AppCompatActivity {

    @BindView(R.id.bk_tv)
    TextView mBkTv;
    @BindView(R.id.bk_btn)
    Button mBkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_butter_knife);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.bk_tv, R.id.bk_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bk_tv:
                mBkTv.setText("哈哈哈，我变了");
                break;
            case R.id.bk_btn:
                Toast.makeText(getApplicationContext(),"再点我",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
