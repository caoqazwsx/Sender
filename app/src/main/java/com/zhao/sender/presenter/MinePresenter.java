package com.zhao.sender.presenter;

import android.util.Log;


import com.zhao.sender.globalvariable.Globalvariable;
import com.zhao.sender.globalvariable.Utility;
import com.zhao.sender.httpconnection.HttpCallbackListener;

import org.json.JSONObject;

/**
 * Created by zhao on 2016/5/9.
 */
public class MinePresenter extends BasePresenter {

    public void getPersonalInfo(final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS+"type=select&sql="+
                Utility.encode("from SenderInfo where senderAccount='" + Globalvariable.ACCOUNT+"'");
        getList(address,listener);
    }


    public void submitPersonalInfo(String name,String telephone,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS+"type=update&sql="+
                Utility.encode("update SenderInfo set name='"+Utility.encode(name)+"',telephone='"+telephone+"' where senderAccount='"
                +Globalvariable.ACCOUNT+"'");
        update(address,listener);
    }


}
