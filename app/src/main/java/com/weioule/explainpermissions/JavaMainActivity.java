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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by weioule
 * on 2023/11/05
 */
public class JavaMainActivity extends AppCompatActivity implements Serializable {

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
                ExplainPermissionsUtil.Companion.requestPermission(JavaMainActivity.this, new ExplainBean(
                        Manifest.permission.CALL_PHONE,
                        "拨打电话权限",
                        "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;"
                ), callback);

                /**
                 //上面的代码使用callback内部类对象是可以的，但是这里是在OnClickListener接口里面，直接用new Callback()匿名内部类会报错，传过去的callback它会持有OnClickListener外部类的引用，而系统的OnClickListener接口是没有实现序列化的，在ExplainPermissionsUtil里面进行数据传递的时候会错。
                 ExplainPermissionsUtil.Companion.requestPermission(JavaMainActivity.this, new ExplainBean(
                 Manifest.permission.CALL_PHONE,
                 "拨打电话权限",
                 "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;"
                 ), new Callback<Boolean>() {
                @Override public void onCallback(Boolean granted) {
                toCall();
                }
                });*/
            }
        });

        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //或者抽到外部的方法里，这样就不会持有View.OnClickListener的引用了
                requestPermission();
            }
        });

        //在自定义并实现序列化的点击接口回调里使用匿名内部类的方式传递Callback也是可以的
        findViewById(R.id.tv3).setOnClickListener(new MyOnClickListener() {
            @Override
            public void onClick(View var1) {
                ExplainPermissionsUtil.Companion.requestPermission(activity, new ExplainBean(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        "位置信息权限",
                        "我们想要访问你的位置，用于为您提供更好的服务哦;"
                ), new Callback<Boolean>() {
                    @Override
                    public void onCallback(Boolean granted) {
                        Toast.makeText(activity, "定位权限获取到了", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void requestPermission() {
        ArrayList<ExplainBean> list = new ArrayList<>();
        list.add(
                new ExplainBean(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        "位置信息权限",
                        "我们想要访问你的位置，用于为您提供更好的服务哦;"
                )
        );
        list.add(
                new ExplainBean(
                        Manifest.permission.CAMERA,
                        "相机权限",
                        "我们想要相机权限，用于您在与客服小姐姐沟通时可以视频通话哦;"
                )
        );
        list.add(
                new ExplainBean(
                        Manifest.permission.CALL_PHONE,
                        "拨打电话权限",
                        "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;"
                )
        );

        ExplainPermissionsUtil.Companion.requestPermissions(activity, list, new Callback<Boolean>() {
            @Override
            public void onCallback(Boolean granted) {
                toCall();
            }
        });
    }

    private Callback callback = new Callback<Boolean>() {
        @Override
        public void onCallback(Boolean granted) {
            toCall();
        }
    };

    private void toCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:18888888888");
        intent.setData(data);
        activity.startActivity(intent);
    }

    public interface MyOnClickListener extends View.OnClickListener, Serializable {
        void onClick(View var1);
    }

}
