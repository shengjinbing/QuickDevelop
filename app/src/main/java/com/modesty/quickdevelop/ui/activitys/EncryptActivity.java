package com.modesty.quickdevelop.ui.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.modesty.quickdevelop.R;
import com.modesty.quickdevelop.decrypt.AESUtil;
import com.modesty.quickdevelop.decrypt.Base64Decoder;
import com.modesty.quickdevelop.decrypt.Base64Encoder;
import com.modesty.quickdevelop.decrypt.Base64Util;
import com.modesty.quickdevelop.decrypt.DESUtil;
import com.modesty.quickdevelop.decrypt.HMACSHA1;
import com.modesty.quickdevelop.decrypt.MD5Util;
import com.modesty.quickdevelop.decrypt.RSAUtil;
import com.modesty.quickdevelop.decrypt.RSAUtil1;
import com.modesty.quickdevelop.decrypt.SHAUtil;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * https://blog.csdn.net/qq_22771739/article/details/84261165
 * 字符集
 *
 */
public class EncryptActivity extends AppCompatActivity {


    @BindView(R.id.et_accept)
    EditText mEtAccept;
    @BindView(R.id.tv_encrypt)
    TextView mTvEncrypt;
    @BindView(R.id.tv_decryption)
    TextView mTvDecryption;
    @BindView(R.id.tv_encrypt_length)
    TextView mTvEncryptLength;
    @BindView(R.id.tv_decryption_length)
    TextView mTvDecryptionLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.md5, R.id.sha1, R.id.hmac, R.id.aes, R.id.des, R.id.des3, R.id.rsa, R.id.dsa,
            R.id.ecc, R.id.decryptionaes, R.id.decryptiondes, R.id.decryptiondes3,
            R.id.decryptionrsa, R.id.decryptiondsa, R.id.decryptionecc,R.id.base,
            R.id.decryptionbase})
    public void onViewClicked(View view) {
        //show();
        String content = mEtAccept.getText().toString().trim();
        String encryptContent = mTvEncrypt.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Map<String, Object> map = null;
        try {
            map = RSAUtil.genKeyPair("dasdasdas");
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (view.getId()) {
            case R.id.base:
                showEncryptResult(Base64Util.encodeToString(content));
                break;
            case R.id.md5:
                showEncryptResult(MD5Util.getMessageDigest(content.getBytes()));
                break;
            case R.id.sha1:
                showEncryptResult(SHAUtil.SHAEncode(content));
                break;
            case R.id.hmac:
                try {
                    showEncryptResult(HMACSHA1.hmacSha1Encrypt(content, "HmacSHA1"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.aes:
                showEncryptResult(AESUtil.encrypt(content, "123456"));
                break;
            case R.id.des:
                try {
                    showEncryptResult(DESUtil.encrypt(content, "123456789qwe"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.des3:
                break;
            case R.id.rsa:
                try {
                   /* content = "hyx_912_74571852356eb1dd4902f66c9c7ad103e8285d010d27488b";
                    String privateKey = RSAUtil.getPrivateKey(map);
                    byte[] bytes = RSAUtil.encryptByPrivateKey(content.getBytes(),privateKey);
                    showEncryptResult(RSAUtil.encode(bytes));

                    String publicKey = RSAUtil.getPublicKey(map);
                    byte[] byte1 = RSAUtil.decryptByPublicKey(bytes, publicKey);
                    showDecryptResult(RSAUtil.encode(byte1));*/
                    rsaEncryptPublicKey(content);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dsa:
                //用作签名，速度快比RSA
                break;
            case R.id.ecc:
                break;
            case R.id.decryptionaes:
                showDecryptResult(AESUtil.decrypt(encryptContent, "123456"));
                break;
            case R.id.decryptiondes:
                try {
                    showDecryptResult(DESUtil.decrypt(encryptContent, "123456789qwe"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.decryptiondes3:
                break;
            case R.id.decryptionrsa:
                try {
                    String publicKey = RSAUtil.getPublicKey(map);
                    byte[] bytes = RSAUtil.decryptByPublicKey(RSAUtil.decode(encryptContent), publicKey);
                    showDecryptResult(RSAUtil.encode(bytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.decryptiondsa:
                break;
            case R.id.decryptionecc:
                break;
            case R.id.decryptionbase:
                showDecryptResult(Base64Util.decode(encryptContent));
                break;
            default:
                break;
        }
    }

    public void showEncryptResult(String result) {
        mTvEncrypt.setText(result);
        mTvEncryptLength.setText("加密后的长度=" + result.length());
    }

    public void showDecryptResult(String result) {
        mTvDecryption.setText(result);
        mTvDecryptionLength.setText("解密后的长度=" + result.length());
    }

    /**
     *公钥加密私钥解密，，私钥加密公钥也能解密，，但是公私钥不能对调，根据私钥可以算出公钥。
     *如何防止伪造？
     * 数字签名
     *
     * 数字签名？
     * 用私钥签名数据证明这数据是我的。
     *
     * @param content  要转为二进制数据
     * @throws Exception
     */
    private void rsaEncryptPublicKey(String content) throws Exception {
        KeyPair keyPair = RSAUtil1.generateRSAKeyPair(RSAUtil1.DEFAULT_KEY_SIZE);
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //公钥加密
        long start = System.currentTimeMillis();
        byte[] encryptBytes = RSAUtil1.encryptByPublicKeyForSpilt(content.getBytes(), publicKey.getEncoded());
        long end = System.currentTimeMillis();
        Log.e("MainActivity", "公钥加密耗时 cost time---->" + (end - start));
        String encryStr = Base64Encoder.encode(encryptBytes);
        //showEncryptResult(encryStr);
        Log.e("MainActivity", "加密后json数据 --1-->" + encryStr);
        Log.e("MainActivity", "加密后json数据长度 --1-->" + encryStr.length());
        //私钥解密
        start = System.currentTimeMillis();
        byte[] decryptBytes = RSAUtil1.decryptByPrivateKeyForSpilt(Base64Decoder.decodeToBytes(encryStr), privateKey.getEncoded());
        String decryStr = new String(decryptBytes);
        end = System.currentTimeMillis();
        Log.e("MainActivity", "私钥解密耗时 cost time---->" + (end - start));
        Log.e("MainActivity", "解密后json数据 --1-->" + decryStr);
        //showDecryptResult(decryStr);

      //私钥加密
        start=System.currentTimeMillis();
        encryptBytes=    RSAUtil1.encryptByPrivateKeyForSpilt(content.getBytes(),privateKey.getEncoded());
        end=System.currentTimeMillis();
        Log.e("MainActivity","私钥加密密耗时 cost time---->"+(end-start));
        encryStr=Base64Encoder.encode(encryptBytes);
        Log.e("MainActivity","加密后json数据 --2-->"+encryStr);
        Log.e("MainActivity","加密后json数据长度 --2-->"+encryStr.length());
        showEncryptResult(encryStr);
        //公钥解密
        start=System.currentTimeMillis();
        decryptBytes=  RSAUtil1.decryptByPublicKeyForSpilt(Base64Decoder.decodeToBytes(encryStr),publicKey.getEncoded());
        decryStr=new String(decryptBytes);
        end=System.currentTimeMillis();
        Log.e("MainActivity","公钥解密耗时 cost time---->"+(end-start));
        Log.e("MainActivity","解密后json数据 --2-->"+decryStr);
        showDecryptResult(decryStr);


    }

    private void rsaEncryptPrivateKey(String content) {

    }

    public void show() {
        try {
            String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKB8GunT65dDzvV4" + "VD6UA9+lGEjIgEtyEVtrp3rEhBRmvOZ1sromkybrAF4ByodHh1BmgBLdImMqMzH2"
                    + "vgwc3ioOqiaODqHNPpqa/jeSrdNE/hJSKQqPXi+qVaIg6tOi84GnirHOrwkVxR45" + "kQgj4lH7qnIaMhooaIModIsDTGs7AgMBAAECgYEAg/Jlwlhtu9mRgDslsKnLoYZA"
                    + "uB65dM5dPPf/JC4MliV+LFEa2Hg8xmOy0pfQZ3dE5rLPnDLaQgQBQZQn3xehBE/N" + "2YdzLEH1Dpw1eOJY30Qf/Rp6jUaTwY5gQCxSDt24CXpDjzo09dvaR4uHhRNZX1KB"
                    + "XNco+PiM7ujFaSrhuBkCQQDQC3d2OhQB4vAaxaPzwqQv6lAFWCR8Osy5jyY/KlVF" + "kd/VzXp2uWACgFm6UXmUwyLfrSpFl013E5SjOsdgpXYNAkEAxXoqed1TYAHHS63N"
                    + "oIQlMz/ygHiMtkFeoD8HgKYw5TzYCpqlM++2O1VcbTLjQtnwctIe3B3xF7eOZ1Si" + "53KcZwJAdPaNYhWC3BCnJpYI9+ls/1c/R9HnKUSxhn05Zne5WxSJAB22hPrxRFa+"
                    + "m2Zk8ULH33LuehN3RMPoY+CO6QH9HQJBAK9+JrtP7iU2z2a42TEZ3nlSDe8PsnTR" + "WQdtm/w/NNqznIan8cJa+AZ4kH/WplIlneJcSuJwlW3vSNUZSQAIQWcCQHBsB41q"
                    + "WyyPcRBjMCR6YO4Iih/07kZJDAqHrdnhea+aNF+MPuShqIGlcDEbdeS1XxUp8gSm" + "diXEh5aJvpTfSEY=";

            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgfBrp0+uXQ871eFQ+lAPfpRhI" + "yIBLchFba6d6xIQUZrzmdbK6JpMm6wBeAcqHR4dQZoAS3SJjKjMx9r4MHN4qDqom"
                    + "jg6hzT6amv43kq3TRP4SUikKj14vqlWiIOrTovOBp4qxzq8JFcUeOZEII+JR+6py" + "GjIaKGiDKHSLA0xrOwIDAQAB";

            System.out.println("privateKey=" + privateKey);
            System.err.println("私钥加密——公钥解密");
            String source = "辽宁省老大塑料袋那算了都拿到拉萨";
            System.out.println("原文字：\r\n" + source);
            byte[] data = source.getBytes();
            // byte[] encodedData = RSAUtil.encryptByPrivateKey(data, privateKey);
            byte[] encodedData = RSAUtil.encryptByPublicKey(data, publicKey);
            String enBaseDate = RSAUtil.encode(encodedData);
            System.out.println("加密后：\r\n" + enBaseDate);
            // byte[] decryptByPublicKey = RSAUtil.decryptByPublicKey(encodedData, publicKey);
            byte[] decryptByPublicKey = RSAUtil.decryptByPrivateKey(encodedData, privateKey);
            System.out.println("解密后：\r\n" + new String(decryptByPublicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
