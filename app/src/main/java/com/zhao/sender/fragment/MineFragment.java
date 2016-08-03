package com.zhao.sender.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.sender.R;
import com.zhao.sender.activity.EditPersonalInfoActivity;
import com.zhao.sender.activity.HelpActivity;
import com.zhao.sender.activity.LoginActivity;
import com.zhao.sender.globalvariable.Globalvariable;


public class MineFragment extends Fragment {
    private View rootView;

    private TextView account;
    private Button logout;
    private Button login;

    private LinearLayout info;

    private LinearLayout help;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null){
            View view = inflater.inflate(R.layout.fragment_mine, container, false);
            account = (TextView)view.findViewById(R.id.mine_account);
            logout = (Button)view.findViewById(R.id.mine_logout);
            login = (Button)view.findViewById(R.id.mine_login);
            info = (LinearLayout)view.findViewById(R.id.mine_info);
            help = (LinearLayout)view.findViewById(R.id.mine_help);

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Globalvariable.LOGIN_STATE) {
                        Intent it = new Intent(getActivity(),EditPersonalInfoActivity.class);
                        getActivity().startActivity(it);
                    }else{
                        Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), HelpActivity.class);
                    getActivity().startActivity(it);
                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Globalvariable.LOGIN_STATE = false;
                    Globalvariable.ACCOUNT = null;
                    checkLoginState();
                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getContext(), LoginActivity.class);
                    getActivity().startActivity(it);
                }
            });
            rootView=view;
        }
        else{
            Log.d("mineFragment", "rootview != null");
            // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup)rootView.getParent();
            if(parent!=null){
                Log.d("mineFragment","parent != null");
                parent.removeView(rootView);
            }
        }
        checkLoginState();
        return rootView;
    }

    public boolean checkLoginState(){
        if(Globalvariable.LOGIN_STATE){
            account.setText(Globalvariable.ACCOUNT);
            logout.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            return true;
        }
        else {
            account.setText("未登录");
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            Intent it = new Intent(getContext(),LoginActivity.class);
            startActivity(it);
            getActivity().finish();
            return false;
            // logout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        if(!isHidden()) {
           checkLoginState();
        }
        super.onResume();
    }
}
