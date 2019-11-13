package com.modesty.quickdevelop.ui.activitys;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.base.BaseApplication;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

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
    @BindView(R.id.requestPatch)
    Button requestPatch;
    @BindView(R.id.requestConfig)
    Button requestConfig;
    @BindView(R.id.cleanPatch)
    Button cleanPatch;
    @BindView(R.id.killSelf)
    Button killSelf;
    @BindView(R.id.btn_test)
    Button btnTest;

    public static final String TAG = "LottieActivity";
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.iv_test)
    ImageView ivTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);
        ButterKnife.bind(this);
        lottie1.setImageResource(R.drawable.block_canary_icon);
        lottie1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!show) {
                    lottie1.setAnimation("lottie/shouye.json");
                    lottie1.playAnimation();
                } else {
                    lottie1.setImageResource(R.drawable.block_canary_icon);
                }
                show = !show;
            }
        });
        initTestAnimation();
        //lottie1.setAnimation("shouye.json");
        //lottie1.setRepeatCount(100);
        //lottie1.playAnimation();

        lottie2.setImageAssetsFolder("images");
        lottie2.setAnimation("lottie/hb.json");
        //lottie2.setRepeatCount(100);
        lottie2.playAnimation();
        lottie2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottie2.playAnimation();
            }
        });


        /*lottie3.setAnimation("lottie/faxian.json");
        lottie3.setRepeatCount(100);
        lottie3.playAnimation();*/
        play();


        lottie4.setAnimation("lottie/base64.json");
        lottie4.setRepeatCount(100);
        lottie4.playAnimation();

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence == null || charSequence.length() == 0) {
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < charSequence.length(); i++) {
                    if (i != 3 && i != 8 && charSequence.charAt(i) == ' ') {
                        continue;
                    } else {
                        stringBuilder.append(charSequence.charAt(i));
                        if ((stringBuilder.length() == 4 || stringBuilder.length() == 9)
                                && stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
                            stringBuilder.insert(stringBuilder.length() - 1, ' ');
                        }
                    }
                }
                if (!stringBuilder.toString().equals(charSequence.toString())) {
                    int index = start + 1;
                    if (stringBuilder.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    etInput.setText(stringBuilder.toString());
                    etInput.setSelection(index);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        initTinker();
    }

    private void initTestAnimation() {

    }

    private void initTinker() {
        //immediately 为 true, 每次强制访问服务器更新
        requestPatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinkerPatch.with().fetchPatchUpdate(true);
            }
        });


        //immediately 为 true, 每次强制访问服务器更新
        requestConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinkerPatch.with().fetchDynamicConfig(new ConfigRequestCallback() {

                    @Override
                    public void onSuccess(HashMap<String, String> configs) {
                        TinkerLog.w(TAG, "request config success, config:" + configs);
                    }

                    @Override
                    public void onFail(Exception e) {
                        TinkerLog.w(TAG, "request config failed, exception:" + e);
                    }
                }, true);
            }
        });

        cleanPatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinkerPatch.with().cleanAll();
            }
        });

        killSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
                Process.killProcess(Process.myPid());
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "我生效了");
                Toast.makeText(LottieActivity.this, "哈哈哈哈", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "I am on onResume hehehe");
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.e(TAG, "I am on onPause gogogo");
        super.onPause();
    }

    // 资源zip
    public final  File LOTTIE_FILES = new File(Environment.getExternalStorageDirectory()+"/lotties/lottie/lottie.zip");


    private void play(){
        // 动效图片资源
         final  File IMAGES_FILES = new File(getExternalCacheDir().getAbsolutePath()+"/lotties/lotties/images");
        // data.json路径
         final  File JSON_FILE = new File(getExternalCacheDir().getAbsolutePath()+"/lotties/lotties/hb.json");
        FileInputStream fis = null;
        if (JSON_FILE.exists()) {
            try {
                fis = new FileInputStream(JSON_FILE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (fis == null || !IMAGES_FILES.exists()) {
            Log.i("huangssh", "动画资源不存在");
            return;
        }
        final String absolutePath = IMAGES_FILES.getAbsolutePath();
        // 开启硬件加速
        lottie3.useHardwareAcceleration(true);
        // 设置动画文件夹代理类
        lottie3.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset asset) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inScaled = true;
                //opts.inDensity = UtilPhoneParam.densityDpi;
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(absolutePath + File.separator + asset.getFileName(), opts);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return bitmap;
            }
        });

        // 设置动画
        LottieComposition.Factory.fromInputStream(fis, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition composition) {
                lottie3.setComposition(composition);
                lottie3.setRepeatCount(100);
                lottie3.playAnimation();
            }
        });

    }

}
