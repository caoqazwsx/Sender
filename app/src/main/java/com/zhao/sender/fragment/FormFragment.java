package com.zhao.sender.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.sender.R;
import com.zhao.sender.custom.DownUpListView;
import com.zhao.sender.globalvariable.Globalvariable;
import com.zhao.sender.httpconnection.HttpCallbackListener;
import com.zhao.sender.model.Form;
import com.zhao.sender.model.FormItemAdapter;
import com.zhao.sender.presenter.FormPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class FormFragment extends Fragment {

    private ArrayList<Form> formItems;
    private  ArrayList<Form> formItemsSignArrived;

    private View rootView;
    private DownUpListView listView;
    private TextView title;
    private ProgressBar progressBar;
    private FormItemAdapter formItemAdapter;
    private RadioGroup radioGroup_sort;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    listView.onRefreshComplete();
                    formItemAdapter = new FormItemAdapter(getContext(), R.layout.listview_item_new_form, formItems);
                    progressBar.setVisibility(View.GONE);
                    listView.setAdapter(formItemAdapter);
                    listView.setDividerHeight(10);
                    // Log.d("FormFragment", "success init");
                    break;
                case -1:
                    listView.onRefreshComplete();
                    Toast.makeText(getContext(), "请检查你的网络", Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(getContext(), "无订单", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_form, container, false);
            progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
            listView = (DownUpListView)rootView.findViewById(R.id.list_newforms);
            radioGroup_sort = (RadioGroup)rootView.findViewById(R.id.form_sort);
            progressBar.setVisibility(View.VISIBLE);
            formItems = new ArrayList<Form>();
            title = (TextView)rootView.findViewById(R.id.title);
            listView.setonRefreshListener(new DownUpListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    init();
                }
            });
            radioGroup_sort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == R.id.form_sort_time){
                        formItemAdapter = new FormItemAdapter(getContext(), R.layout.listview_item_new_form, formItems);
                        listView.setAdapter(formItemAdapter);
                    }else if(checkedId == R.id.form_sort_distance){
                        FormPresenter fp = new FormPresenter();
                        ArrayList<Form> formsSortWithDistance = new ArrayList<Form>();
                        ArrayList<Form> tem = fp.sortFormWithDistance(formItems,getContext());
                        for(int i = 0; i < tem.size(); i ++){
                            formsSortWithDistance.add(tem.get(i));
                        }
                        tem = fp.sortFormWithDistance(formItemsSignArrived,getContext());
                        for(int i = 0; i < tem.size(); i ++){
                            formsSortWithDistance.add(tem.get(i));
                        }
                        formItemAdapter = new FormItemAdapter(getContext(), R.layout.listview_item_new_form, fp.sortFormWithDistance(formsSortWithDistance,getContext()));
                        listView.setAdapter(formItemAdapter);

                    }
                }
            });
            if(Globalvariable.LOGIN_STATE)  init();
            else  Toast.makeText(getContext(), "未登录", Toast.LENGTH_SHORT).show();
        }else{
            ViewGroup parent = (ViewGroup)rootView.getParent();
            if(parent != null){
               parent.removeView(rootView);
            }
        }
        return rootView;
    }

    public void init() {
        formItems.clear();
        listView.setAdapter(null);
        FormPresenter fp = new FormPresenter();//通过服务器端获取订单信息
        fp.getNewFormList(Globalvariable.ACCOUNT,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (response != null) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        formItemsSignArrived = new  ArrayList<Form>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jb = ja.getJSONObject(i);
                            jb.put("formFood",jb.getString("formFood"));
                            Form form = new Gson().fromJson(jb.toString(),Form.class);
                            if(form.getSendState().equals("ON")) {
                                formItems.add(form);
                            }else if(form.getSendState().equals("Arrived")){
                               formItemsSignArrived.add(form);
                            }
                        }
                        for(int i=0;i<formItemsSignArrived.size();i++){
                            formItems.add(formItemsSignArrived.get(i));
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);

                    } catch (Exception e) {
                        Log.d("FormFragment", e.toString());
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                } else {
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
                Log.d("FormFragment", e.toString());
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }




}
