package com.kuaishoupackaging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaishoupackaging.util.SharedPreferencesUitl;

public class SettingAct extends Activity implements View.OnClickListener {


    private SharedPreferencesUitl preferencesUitl;
    /**
     * 服务端IP
     */
    private TextView mTextView1;
    /**
     * 192.168.1.155
     */
    private EditText mEditText1Ip;
    /**
     * 端口号
     */
    private TextView mTextView2Duankou;
    /**
     * 8888
     */
    private EditText mEditText2Duankou;
    /**
     * 保存
     */
    private Button mButton1Save;
    /**
     * 体积校正
     */
    private Button mBtnVolumeSetting;
    /**
     * 条码设置
     */
    private Button mBtnBarcodeSetting;
    /**
     * 120
     */
    private EditText mEditTextHert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        initView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        preferencesUitl = SharedPreferencesUitl.getInstance(this, "decoeBar");
    }


    private void initView() {
        mEditText1Ip = findViewById(R.id.editText1_ip);
        mEditText2Duankou = findViewById(R.id.editText2_duankou);
        mButton1Save = findViewById(R.id.button1_save);
        mButton1Save.setOnClickListener(this);
        mButton1Save.setClickable(false);
        mBtnVolumeSetting = findViewById(R.id.btn_volume_setting);
        mBtnVolumeSetting.setOnClickListener(this);
        mBtnVolumeSetting.setClickable(false);
        mBtnBarcodeSetting = findViewById(R.id.btn_barcode_setting);
        mBtnBarcodeSetting.setOnClickListener(this);
        mTextView1 = findViewById(R.id.textView1);
        mEditText1Ip = findViewById(R.id.editText1_ip);
        mEditText2Duankou = findViewById(R.id.editText2_duankou);
        mEditTextHert = findViewById(R.id.editText_hert);
        mEditTextHert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.button1_save:
                String ip = mEditText1Ip.getText().toString();
                String duankou = mEditText2Duankou.getText().toString();
                int heart = 1;
                if (mEditTextHert.getText().toString().equals("")) {
                    heart = 1;
                } else {
                    heart = Integer.parseInt(mEditTextHert.getText().toString());
                }
                preferencesUitl.write("ip", ip);
                preferencesUitl.write("duankou", duankou);
                preferencesUitl.write("heart", heart);
                Toast.makeText(this, R.string.set_toast, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_volume_setting:
                Intent intent = new Intent(this, VolumeSetingAct.class);
                startActivity(intent);

                break;
            case R.id.btn_barcode_setting:
                Intent intent1 = new Intent(this, DbShowAct.class);
                startActivity(intent1);
                break;
            case R.id.editText_hert:
                break;
        }
    }
}
