package com.zhao.sender.presenter;

import com.zhao.sender.globalvariable.Globalvariable;
import com.zhao.sender.globalvariable.Utility;
import com.zhao.sender.httpconnection.HttpCallbackListener;

/**
 * Created by zhao on 2016/6/4.
 */
public class MapPresenter extends BasePresenter{

    public void getLocation(int addressId, final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql="+ Utility.encode("from DeliveryAddress where id="+addressId);
        getList(address,listener);
    }
}
