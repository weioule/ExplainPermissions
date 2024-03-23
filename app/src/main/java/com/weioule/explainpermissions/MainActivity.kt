package com.weioule.explainpermissions

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.weioule.explainpermissionsutil.Callback
import com.weioule.explainpermissionsutil.ExplainBean
import com.weioule.explainpermissionsutil.ExplainPermissionsUtil

/**
 * Author by weioule.
 * Date on 2022/11/05.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Kotlin类的使用"

        findViewById<TextView>(R.id.tv1).setOnClickListener {
            ExplainPermissionsUtil.requestPermissions(
                this@MainActivity,
                ExplainPermissionsUtil.Intercept.LOW,
                object : Callback<Boolean> {
                    override fun onCallback(granted: Boolean) {
                        Toast.makeText(
                            this@MainActivity,
                            if (granted) "已授予权限" else "未授予权限",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                ExplainBean(
                    "拨打电话权限",
                    "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;",
                    Manifest.permission.CALL_PHONE
                )
            )
        }

        findViewById<TextView>(R.id.tv2).setOnClickListener {
            ExplainPermissionsUtil.requestPermissions(
                this@MainActivity,
                ExplainPermissionsUtil.Intercept.MEDIUM,
                object : Callback<Boolean> {
                    override fun onCallback(granted: Boolean) {
                        Toast.makeText(
                            this@MainActivity,
                            if (granted) "已全部授予权限" else "未全部授予权限",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                ExplainBean(
                    "拨打电话权限",
                    "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;",
                    Manifest.permission.CALL_PHONE
                ),
                ExplainBean(
                    "位置信息权限",
                    "我们想要访问你的位置，用于为您提供更好的服务哦;",
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                ExplainBean(
                    "相机权限",
                    "我们想要相机权限，用于您在与客服小姐姐沟通时可以视频通话哦;",
                    Manifest.permission.CAMERA
                )
            )
        }

        findViewById<TextView>(R.id.tv3).text = "跳转Java类的使用"
        findViewById<TextView>(R.id.tv3).setOnClickListener {
            startActivity(Intent(this@MainActivity, JavaMainActivity::class.java))
        }
    }

}
