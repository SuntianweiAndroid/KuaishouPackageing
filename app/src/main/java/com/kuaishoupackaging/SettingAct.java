package com.kuaishoupackaging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaishoupackaging.tcpip.Client;
import com.kuaishoupackaging.util.SettingUtils;
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
    private SettingUtils settingUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        initView();
        preferencesUitl = SharedPreferencesUitl.getInstance(this, "decoeBar");
//        settingUtils = new SettingUtils(SettingAct.this, huoniScan.getHuoniHsmDecoder(), preferencesUitl);
    }


    private void initView() {
        mEditText1Ip = (EditText) findViewById(R.id.editText1_ip);
        mEditText2Duankou = (EditText) findViewById(R.id.editText2_duankou);
        mButton1Save = (Button) findViewById(R.id.button1_save);
        mButton1Save.setOnClickListener(this);
        mBtnVolumeSetting = (Button) findViewById(R.id.btn_volume_setting);
        mBtnVolumeSetting.setOnClickListener(this);
        mBtnBarcodeSetting = (Button) findViewById(R.id.btn_barcode_setting);
        mBtnBarcodeSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.button1_save:
                String ip = mEditText1Ip.getText().toString();
                String duankou = mEditText2Duankou.getText().toString();
                preferencesUitl.write("ip", ip);
                preferencesUitl.write("duankou", duankou);
                Toast.makeText(this, "保存设置成功！！！", Toast.LENGTH_SHORT).show();
                Intent intents = new Intent(this, Client.class);
                startActivity(intents);
                break;
            case R.id.btn_volume_setting:
                Intent intent = new Intent(this, SetingVolumeAct.class);
                startActivity(intent);
                break;
            case R.id.btn_barcode_setting:
//                settingUtils.dialogMoreChoice();
                break;
        }
    }
}
