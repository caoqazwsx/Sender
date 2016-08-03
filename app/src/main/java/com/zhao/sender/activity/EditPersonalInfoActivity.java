package com.zhao.sender.activity;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.sender.R;
import com.zhao.sender.httpconnection.HttpCallbackListener;
import com.zhao.sender.model.SenderInfo;
import com.zhao.sender.presenter.MinePresenter;

import org.json.JSONArray;

import java.io.InputStream;

public class EditPersonalInfoActivity extends BaseActivity{
    private SenderInfo senderInfo;

    private TextView title;
    private Button back;

    private EditText name;
    private EditText telephone;
    private Button submit;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(senderInfo != null){
                        name.setText(senderInfo.getName());
                        telephone.setText(senderInfo.getTelephone());
                    }
                    break;
                case 2:
                    Toast.makeText(EditPersonalInfoActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case -1:
                    Toast.makeText(EditPersonalInfoActivity.this, "init error", Toast.LENGTH_SHORT).show();
                    break;

                case -2:
                    Toast.makeText(EditPersonalInfoActivity.this, "submit error", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_info);
        title = (TextView)findViewById(R.id.title_text);
        back = (Button)findViewById(R.id.title_back);
        name = (EditText)findViewById(R.id.editPersonalInfo_name);
        telephone = (EditText)findViewById(R.id.editPersonalInfo_telephone);
        submit = (Button)findViewById(R.id.editPersonalInfo_submit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkShopInput()){
                    MinePresenter mp = new MinePresenter();
                    mp.submitPersonalInfo(name.getText().toString(), telephone.getText().toString(), new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response){
                            if(response.equals("success")){
                                Message msg = new Message();
                                msg.what = 2;
                                handler.sendMessage(msg);
                            }else {
                                Message msg = new Message();
                                msg.what = -2;
                                handler.sendMessage(msg);
                            }
                        }

                        @Override
                        public void onFinish(InputStream in) {

                        }

                        @Override
                        public void onFinish(Bitmap bm) {

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("EditPernalInfo",e.toString());
                            Message msg = new Message();
                            msg.what = -1;
                            handler.sendMessage(msg);

                        }
                    });

                }
            }
        });

        init();
    }

    private void init(){
        MinePresenter mp = new MinePresenter();
        mp.getPersonalInfo(new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try{
                    JSONArray ja = new JSONArray(response);
                    if(ja.length() == 1){
                        senderInfo = new Gson().fromJson(ja.getString(0),SenderInfo.class);
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);

                    }else {
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                }catch (Exception e){
                    Log.d("EditPernalInfo",e.toString());
                    Message msg = new Message();
                    msg.what = -1;
                    handler.sendMessage(msg);

                }

            }

            @Override
            public void onFinish(InputStream in) {

            }

            @Override
            public void onFinish(Bitmap bm) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private boolean checkShopInput(){

        if(name.getText().toString().equals("")||telephone.getText().toString().equals("")){
            Toast.makeText(EditPersonalInfoActivity.this, "有必填信息为空，请检查", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
}
