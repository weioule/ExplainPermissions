package com.weioule.explainpermissions;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.weioule.explainpermissionsutil.Callback;
import com.weioule.explainpermissionsutil.ExplainBean;
import com.weioule.explainpermissionsutil.ExplainPermissionsUtil;

/**
 * Created by weioule
 * on 2023/11/05
 */
public class JavaMainActivity extends AppCompatActivity {

    private static FragmentActivity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        setTitle("Java类的使用");

        findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplainPermissionsUtil.Companion.requestPermissions(activity,
                        ExplainPermissionsUtil.Intercept.NORMAL,
                        new Callback<Boolean>() {
                            @Override
                            public void onCallback(Boolean granted) {
                                Toast.makeText(
                                        JavaMainActivity.this,
                                        granted ? "已授予权限" : "未授予权限",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        },
                        new ExplainBean(
                                "拨打电话权限",
                                "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;",
                                Manifest.permission.CALL_PHONE
                        ));
            }
        });

        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplainPermissionsUtil.Companion.requestPermissions(activity,
                        ExplainPermissionsUtil.Intercept.LOW,
                        new Callback<Boolean>() {
                            @Override
                            public void onCallback(Boolean granted) {
                                Toast.makeText(
                                        JavaMainActivity.this,
                                        granted ? "已全部授予权限" : "未全部授予权限",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        },
                        new ExplainBean(
                                "拨打电话权限",
                                "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;",
                                Manifest.permission.CALL_PHONE
                        ),
                        new ExplainBean(
                                "位置信息权限",
                                "我们想要访问你的位置，用于为您提供更好的服务哦;",
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        new ExplainBean(
                                "相机权限",
                                "我们想要相机权限，用于您在与客服小姐姐沟通时可以视频通话哦;",
                                Manifest.permission.CAMERA
                        )
                );
            }
        });

        findViewById(R.id.tv3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplainPermissionsUtil.Companion.requestPermissions(activity,
                        ExplainPermissionsUtil.Intercept.MEDIUM,
                        new Callback<Boolean>() {
                            @Override
                            public void onCallback(Boolean granted) {
                                Toast.makeText(
                                        JavaMainActivity.this,
                                        granted ? "已授予权限" : "未授予权限",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        },
                        new ExplainBean(
                                "位置信息权限",
                                "我们想要访问你的位置，用于为您提供更好的服务哦;",
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        ));
            }
        });
    }

}
