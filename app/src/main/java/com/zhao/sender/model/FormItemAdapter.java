package com.zhao.sender.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.sender.R;
import com.zhao.sender.activity.MainActivity;

import com.zhao.sender.activity.MapActivity;
import com.zhao.sender.fragment.FormFragment;
import com.zhao.sender.globalvariable.Globalvariable;
import com.zhao.sender.globalvariable.Utility;
import com.zhao.sender.httpconnection.HttpCallbackListener;
import com.zhao.sender.presenter.FormPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by zhao on 2016/5/1.
 */
public class FormItemAdapter extends ArrayAdapter<Form> {

    private int resourceId;
    private Viewhandler viewhandler;
    private ArrayList<View> views = new ArrayList<View>();


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MainActivity ma = (MainActivity)getContext();
                    FormFragment ff = (FormFragment)ma.getSupportFragmentManager().findFragmentByTag("form");
                    ff.init();
                    break;
                case -1:
                    Toast.makeText(getContext(),"标记失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public FormItemAdapter(Context context, int textViewResourceId,
                           ArrayList<Form> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

       final Form formItem = getItem(position);
        View view;
        if (views.size() <= position) {

            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            views.add(position, view);
            viewhandler = new Viewhandler();
            viewhandler.newforms_item_formId = (TextView) view.findViewById(R.id.newforms_item_formId);
            viewhandler.newforms_item_state = (TextView)view.findViewById(R.id.newforms_item_state);
            viewhandler.newforms_item_address = (TextView)view.findViewById(R.id.newforms_item_address) ;
            viewhandler.newforms_item_telephone = (TextView)view.findViewById(R.id.newforms_item_telephone);
            viewhandler.newforms_item_time = (TextView)view.findViewById(R.id.newforms_item_time);
            viewhandler.newforms_item_total_price = (TextView)view.findViewById(R.id.newforms_item_total_price);
            viewhandler.newforms_item_list_food = (ListView) view.findViewById(R.id.newforms_item_list_food);
            viewhandler.newforms_item_signArrived = (Button)view.findViewById(R.id.newforms_item_signArrived);
            viewhandler.newforms_item_distance = (TextView)view.findViewById(R.id.newforms_item_distance);
            init(formItem,position);
            view.setTag(viewhandler);
        } else {

            view = views.get(position);
            viewhandler = (Viewhandler) view.getTag();
        }
        return view;
    }

    private void init(final Form formItem,int position){
        final FormPresenter fp = new FormPresenter();
        viewhandler.newforms_item_formId.setText(formItem.getId()+"");
        viewhandler.newforms_item_state.setText( parseFormState(formItem.getFormState()));
        viewhandler.newforms_item_address.setText("地址："+formItem.getFormAddress());
        viewhandler.newforms_item_distance.setText(fp.countDistance(formItem,getContext())+"m");
        viewhandler.newforms_item_telephone.setText("电话："+formItem.getTelephone());
        viewhandler.newforms_item_time.setText(formItem.getSubmitTime());
        ArrayList<CartFoodItem> cartFoodItems = getFoodList(formItem.getFormFood());
        FormFoodItemAdapter formFoodItemAdapter = new  FormFoodItemAdapter(getContext(), R.layout.listview_item_form_food, cartFoodItems);
        viewhandler.newforms_item_list_food.setAdapter(formFoodItemAdapter);
        Utility.setListViewHeightBasedOnChildren( viewhandler.newforms_item_list_food);
        viewhandler.newforms_item_total_price.setText("￥"+formItem.getPayPrice());
        viewhandler.newforms_item_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), MapActivity.class);
                it.putExtra("addressLocation",formItem.getAddressLocation());
                getContext().startActivity(it);
            }
        });
        viewhandler.newforms_item_signArrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fp.changeSendStateWithArrived(Globalvariable.ACCOUNT, formItem.getId(), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        if(response.equals("success")){
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }else {
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
                        Log.d("FormItemAdapter",e.toString());
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);

                    }
                });
            }
        });
        if(formItem.getSendState().equals("Arrived")){
            viewhandler.newforms_item_signArrived.setBackgroundResource(R.color.NULL);
            viewhandler.newforms_item_signArrived.setClickable(false);
            viewhandler.newforms_item_signArrived.setText("已标记送达");
        }
    }



    private void checkFormState(Form formItem){

    }

    private ArrayList<CartFoodItem> getFoodList(String formFood){
        ArrayList<CartFoodItem> cartFoodItems = new ArrayList<CartFoodItem>();
        try {
            JSONArray ja = new JSONArray(formFood);
            for(int i =0 ; i<ja.length();i++) {
                JSONObject jb = ja.getJSONObject(i);
                cartFoodItems.add(new CartFoodItem(jb.getInt("foodId"), jb.getString("foodName"), jb.getInt("foodNum"),
                        jb.getDouble("foodPrice"), jb.getDouble("foodTotalPrice")));
            }
        }catch (Exception e){
            Log.d("FormItemAdapter",e.toString());
        }
        return cartFoodItems;
    }

    private String parseFormState(String state){

        if(state.equals(Globalvariable.WAIT_ACCEPT)) return "待接单";
        else if(state.equals(Globalvariable.WAIT_PAY)) return "待支付";
        else if(state.equals(Globalvariable.FINISH)) return "订单完成";
        else if(state.equals(Globalvariable.CANCEL)) return "已取消";
        else if(state.equals(Globalvariable.WAIT_ARRIVED)) return "待送达";
        else if(state.equals(Globalvariable.WAIT_COMMENT)) return "待评价";

        return "";
    }

    class Viewhandler {
        TextView newforms_item_formId;
        TextView newforms_item_address;
        TextView newforms_item_telephone;
        TextView newforms_item_distance;
        TextView newforms_item_time;
        TextView newforms_item_state;
        TextView newforms_item_total_price;
        ListView newforms_item_list_food;
        Button newforms_item_signArrived;

    }

}
