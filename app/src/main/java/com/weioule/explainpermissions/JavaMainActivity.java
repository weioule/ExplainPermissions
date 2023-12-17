package com.weioule.explainpermissions;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.weioule.explainpermissionsutil.Callback;
import com.weioule.explainpermissionsutil.ExplainBean;
import com.weioule.explainpermissionsutil.ExplainPermissionsUtil;

import java.util.ArrayList;

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
                ExplainPermissionsUtil.Companion.requestPermission(JavaMainActivity.this, ExplainPermissionsUtil.Intercept.NORMAL, new ExplainBean(
                        "拨打电话权限",
                        "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;",
                        Manifest.permission.CALL_PHONE
                ), new Callback<Boolean>() {
                    @Override
                    public void onCallback(Boolean granted) {
                        if (granted)
                            toCall();
                    }
                });
            }
        });

        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ExplainBean> list = new ArrayList<>();
                list.add(
                        new ExplainBean(
                                "位置信息权限",
                                "我们想要访问你的位置，用于为您提供更好的服务哦;",
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                );
                list.add(
                        new ExplainBean(
                                "相机权限",
                                "我们想要相机权限，用于您在与客服小姐姐沟通时可以视频通话哦;",
                                Manifest.permission.CAMERA
                        )
                );
                list.add(
                        new ExplainBean(
                                "拨打电话权限",
                                "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;",
                                Manifest.permission.CALL_PHONE
                        )
                );

                ExplainPermissionsUtil.Companion.requestPermissions(activity, ExplainPermissionsUtil.Intercept.LOW, list, new Callback<Boolean>() {
                    @Override
                    public void onCallback(Boolean granted) {
                        if (granted)
                            toCall();
                    }
                });
            }
        });

        findViewById(R.id.tv3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplainPermissionsUtil.Companion.requestPermission(activity, ExplainPermissionsUtil.Intercept.MEDIUM, new ExplainBean(
                        "位置信息权限",
                        "我们想要访问你的位置，用于为您提供更好的服务哦;",
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                ), new Callback<Boolean>() {
                    @Override
                    public void onCallback(Boolean granted) {
                        Toast.makeText(activity, "定位权限获取到了", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void toCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:18888888888");
        intent.setData(data);
        activity.startActivity(intent);
    }

}
