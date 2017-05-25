package com.example.hjj.readcardtools;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yc.stdrfid.hardware.ext.StdReader;
import com.yc.stdrfid.hardware.ext.UsbHidRaw;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity{


    @BindView(R.id.textView1)
    public TextView textView1;
    @BindView(R.id.txtDevPath)
    public EditText txtDevPath;
    @BindView(R.id.btnOpenDev)
    public Button btnOpenDev;
    @BindView(R.id.btnBeep)
    public Button btnBeep;
    @BindView(R.id.txtM1Cid)
    public EditText txtM1Cid;
    @BindView(R.id.btnM1Select)
    public Button btnM1Select;
    @BindView(R.id.txtM1BlockIndex)
    public EditText txtM1BlockIndex;
    @BindView(R.id.txtM1PWD1)
    public EditText txtM1PWD1;
    @BindView(R.id.txtM1PWD2)
    public EditText txtM1PWD2;
    @BindView(R.id.btnM1Auth1)
    public Button btnM1Auth1;
    @BindView(R.id.btnM1Auth2)
    public Button btnM1Auth2;
    @BindView(R.id.txtM1S0)
    public EditText txtM1S0;
    @BindView(R.id.txtM1S1)
    public EditText txtM1S1;
    @BindView(R.id.btnM1Read)
    public Button btnM1Read;
    @BindView(R.id.btnM1Write)
    public Button btnM1Write;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Globals.rfReader = new StdReader(new UsbHidRaw(this));
    }

    @OnClick({R.id.btnOpenDev, R.id.btnBeep, R.id.btnM1Select, R.id.btnM1Auth1, R.id.btnM1Auth2, R.id.btnM1Read, R.id.btnM1Write})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnOpenDev://打开设备，这个必须先执行
                //进行打开设备的操作，默认是卡座0
                int r = Globals.rfReader.openDev(txtDevPath.getText().toString());
                if (r != 0) {
                    new AlertDialog.Builder(MainActivity.this).setTitle("出错了").setMessage("打开设备失败" + " r=" + String.valueOf(r))
                            .setPositiveButton("确定", null).show();
                    return;
                }
                new AlertDialog.Builder(MainActivity.this).setTitle("提交").setMessage("打开成功")
                        .setPositiveButton("确定", null).show();
                break;
            case R.id.btnBeep:
                if (!Globals.rfReader.isOpened()) {
                    new AlertDialog.Builder(MainActivity.this).setTitle("出错了").setMessage("打开设备失败")
                            .setPositiveButton("确定", null).show();
                    return;
                }
                Globals.rfReader.beep(2, 10);
                break;
            case R.id.btnM1Select:
                int i = Globals.rfReader.rfCard(0x26);
                if (i != 0) {
                    txtM1Cid.setText("选卡失败" + "R=" + String.valueOf(i));
                    return;
                }
                txtM1Cid.setText("卡号" + Globals.rfReader.getLastAck().substring(0, 8) + "," + Globals.rfReader.getLastAck().substring(8, 10));
                break;
            case R.id.btnM1Auth1:
                //根据卡的索引数字进行读取,A认证
                int bIdx = Integer.valueOf(txtM1BlockIndex.getText().toString());
                int ra = Globals.rfReader.rfAuthentication(0, bIdx, txtM1PWD1.getText().toString());
                if (ra != 0) {
                    txtM1S1.setText("认证失败" + " A,R=" + String.valueOf(ra) + ra);
                    return;
                }
                txtM1S1.setText("认证成功" + " A,R=" + String.valueOf(ra) + ra);
                break;
            case R.id.btnM1Auth2:
                int bIdxB = Integer.valueOf(txtM1BlockIndex.getText().toString());
                int rb = Globals.rfReader.rfAuthentication(0, bIdxB, txtM1PWD2.getText().toString());
                if (rb != 0) {
                    txtM1S1.setText("认证失败" + " B,R=" + String.valueOf(rb) + rb);
                    return;
                }
                txtM1S1.setText("认证成功" + " B,R=" + String.valueOf(rb) + rb);
                break;
            case R.id.btnM1Read:
                //读取数据
                int bIdxR = Integer.valueOf(txtM1BlockIndex.getText().toString());
                int rr = Globals.rfReader.rfM1Read(bIdxR);
                if (rr != 0) {
                    txtM1S1.setText("读取失败" + " R=" + String.valueOf(rr) + rr);
                }
                txtM1S0.setText(Globals.rfReader.getLastAck());
                txtM1S1.setText("");
                break;
            case R.id.btnM1Write:
                //写入数据
                int bIdxW = Integer.valueOf(txtM1BlockIndex.getText().toString());
                if ((bIdxW + 1) % 4 == 0) {
                    txtM1S1.setText(String.valueOf(bIdxW) + "块有密码信息最好不要乱写");
                    return;
                }
                int rw = Globals.rfReader.rfM1Write(bIdxW, txtM1S0.getText().toString());
                if (rw != 0) {
                    txtM1S1.setText("写入失败" + " R=" + String.valueOf(rw));
                    return;
                }
                txtM1S1.setText("写入成功");
                break;
        }
    }
}
