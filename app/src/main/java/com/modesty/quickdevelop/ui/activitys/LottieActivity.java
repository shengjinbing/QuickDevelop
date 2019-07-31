package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.modesty.quickdevelop.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LottieActivity extends AppCompatActivity {

    @BindView(R.id.lottie1)
    LottieAnimationView lottie1;
    @BindView(R.id.lottie2)
    LottieAnimationView lottie2;
    @BindView(R.id.lottie3)
    LottieAnimationView lottie3;
    @BindView(R.id.lottie4)
    LottieAnimationView lottie4;
    boolean show = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);
        ButterKnife.bind(this);
        lottie1.setImageResource(R.drawable.block_canary_icon);
        lottie1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!show){
                    lottie1.setAnimation("lottie/shouye.json");
                    lottie1.playAnimation();
                }else {
                    lottie1.setImageResource(R.drawable.block_canary_icon);
                }
                show = !show;
            }
        });
        //lottie1.setAnimation("shouye.json");
        //lottie1.setRepeatCount(100);
        //lottie1.playAnimation();

        lottie2.setAnimation("lottie/chujie.json");
        //lottie2.setRepeatCount(100);
        lottie2.playAnimation();
        lottie2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie2.playAnimation();
            }
        });


        lottie3.setAnimation("lottie/faxian.json");
        lottie3.setRepeatCount(100);
        lottie3.playAnimation();


        lottie4.setAnimation("lottie/zhanghu.json");
        lottie4.setRepeatCount(100);
        lottie4.playAnimation();
    }
}
