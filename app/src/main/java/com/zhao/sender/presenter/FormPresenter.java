package com.zhao.sender.presenter;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.zhao.sender.globalvariable.Globalvariable;
import com.zhao.sender.globalvariable.Utility;
import com.zhao.sender.httpconnection.HttpCallbackListener;
import com.zhao.sender.model.CartFoodItem;
import com.zhao.sender.model.Form;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhao on 2016/5/1.
 */
public class FormPresenter extends BasePresenter {




    public void getNewFormList(String account, final HttpCallbackListener listener) {
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql=" +
                Utility.encode("from Form where formState='WaitArrived'and senderAccount='"+account+"' order by submitTime desc");
        getList(address, listener);
    }


    public void changeSendStateWithArrived(String senderAccount,long formId,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=update&sql=" +
                Utility.encode("update Form set sendState='Arrived' where id="+formId);
        update(address,listener);
    }

    public ArrayList<Form> sortFormWithDistance(ArrayList<Form> forms, Context context){
        Location location = new LocationPresenter().getCurrentLocation(context);
        ArrayList<Integer> distances = new ArrayList<Integer>();
        ArrayList<Form> formsSortWithDistance = new ArrayList<Form>();
        for(int i = 0; i < forms.size(); i++){
            String[] formLocation = forms.get(i).getAddressLocation().split(",");
            distances.add(i,Utility.getP2PDistance(location.getLongitude(),location.getLatitude(),
                    Double.parseDouble(formLocation[0]),Double.parseDouble(formLocation[1])));
            formsSortWithDistance.add(forms.get(i));
        }

        for(int i = 0; i < formsSortWithDistance.size() ; i++) {
            for (int j = i + 1; j < formsSortWithDistance.size(); j++) {
                if (distances.get(j) < distances.get(i)) {
                    int temdt = distances.get(i);
                    Form temform = formsSortWithDistance.get(i);
                    distances.set(i,distances.get(j));
                    distances.set(j,temdt);
                    formsSortWithDistance.set(i,formsSortWithDistance.get(j));
                    formsSortWithDistance.set(j,temform);
                }
            }
        }

        return formsSortWithDistance;

    }

    public int countDistance(Form form, Context context){
        String[] shopLocation = form.getAddressLocation().split(",");
        LocationPresenter lp = new LocationPresenter();
        Location location = lp.getCurrentLocation(context);
        int res = Utility.getP2PDistance(location.getLongitude(),location.getLatitude(),Double.parseDouble(shopLocation[0]),Double.parseDouble(shopLocation[1]));
        return res;
    }

    public ArrayList<CartFoodItem> getFoodList(String formFood){
        ArrayList<CartFoodItem> cartFoodItems = new ArrayList<CartFoodItem>();
        try {
            JSONArray ja = new JSONArray(formFood);
            for(int i =0 ; i<ja.length();i++) {
                JSONObject jb = ja.getJSONObject(i);
                cartFoodItems.add(new CartFoodItem(jb.getInt("foodId"), jb.getString("foodName"), jb.getInt("foodNum"),
                        jb.getDouble("foodPrice"), jb.getDouble("foodTotalPrice")));
            }
        }catch (Exception e){
            Log.d("FormInfoActivty",e.toString());
        }
        return cartFoodItems;
    }


}

