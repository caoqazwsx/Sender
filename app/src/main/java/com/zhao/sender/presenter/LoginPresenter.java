package com.zhao.sender.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.zhao.sender.globalvariable.Globalvariable;
import com.zhao.sender.globalvariable.Utility;
import com.zhao.sender.httpconnection.HttpCallbackListener;
import com.zhao.sender.httpconnection.ServerResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;


/**
 * Created by zhao on 2016/4/16.
 */
public class LoginPresenter extends BasePresenter{

    public void loginCheck(String account,String password, final HttpCallbackListener listener){
        ServerResponse sr = new ServerResponse();
        String sql = "from User where account='" + account + "' and password='" + password + "' and role='sender'";
        sql = Utility.encode(sql);
        String address = Globalvariable.SERVER_ADDRESS+"type=select&sql="+sql;
        getList(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONArray ja = new JSONArray(response);
                    if(ja.length() == 1) listener.onFinish("success");
                    else listener.onFinish("error");
                }catch (Exception e){
                    listener.onError(e);
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
                listener.onError(e);
            }
        });
    }

    public void register(String account,String password, String name,String telephone,final HttpCallbackListener listener){

        final JSONObject user = new JSONObject();
        final JSONObject info = new JSONObject();     //前台数据存储
        JSONObject tabledata = new JSONObject();
        try {
            tabledata.put("account",account);
            tabledata.put("password",password);
            tabledata.put("role","sender");
            user.put("table","user");
            user.put("data",tabledata);
            tabledata =  new JSONObject();
            tabledata.put("senderAccount",account);
            tabledata.put("name",Utility.encode(name));
            tabledata.put("telephone",telephone);
            info.put("table","SenderInfo");
            info.put("data",tabledata);

        }catch (Exception e){
            Log.d("register",e.toString());
        }

     String address = Globalvariable.SERVER_ADDRESS+"type=register&user="+Utility.encode(user.toString())+
             "&info="+Utility.encode(info.toString());
        update(address,listener);

    }

}
