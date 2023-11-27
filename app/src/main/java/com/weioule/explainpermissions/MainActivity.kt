package com.weioule.explainpermissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.weioule.explainpermissionsutil.Callback
import com.weioule.explainpermissionsutil.ExplainBean
import com.weioule.explainpermissionsutil.ExplainPermissionsUtil
import java.io.Serializable

/**
 * Author by weioule.
 * Date on 2022/11/05.
 */
class MainActivity : AppCompatActivity(),
    Serializable {//因为使用内部类或匿名内部类的形式传递的callback，内部类它持有外部类的引用，外部类也需要序列化，不然会传递不过去，所以我们的MainActivity也实现了Serializable接口

    companion object {
        @JvmStatic
        var activity: Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this

        title = "Kotlin类的使用"

        findViewById<TextView>(R.id.tv1).setOnClickListener {
            ExplainPermissionsUtil.requestPermission(
                this@MainActivity,
                ExplainBean(
                    Manifest.permission.CALL_PHONE,
                    "拨打电话权限",
                    "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;"
                ),
                object : Callback<Boolean> {
                    override fun onCallback(granted: Boolean) {
                        toCall()
                    }
                })
        }

        findViewById<TextView>(R.id.tv2).setOnClickListener {
            var permissionList = mutableListOf<ExplainBean>()
            permissionList.add(
                ExplainBean(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    "位置信息权限",
                    "我们想要访问你的位置，用于为您提供更好的服务哦;"
                )
            )
            permissionList.add(
                ExplainBean(
                    Manifest.permission.CAMERA,
                    "相机权限",
                    "我们想要相机权限，用于您在与客服小姐姐沟通时可以视频通话哦;"
                )
            )
            permissionList.add(
                ExplainBean(
                    Manifest.permission.CALL_PHONE,
                    "拨打电话权限",
                    "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;"
                )
            )

            //kotlin的内部类不会持有外部类的引用，这里可以直接用内部类的方式传递Callback对象
            ExplainPermissionsUtil.requestPermissions(
                this@MainActivity,
                permissionList,
                object : Callback<Boolean> {
                    override fun onCallback(granted: Boolean) {
                        toCall()
                    }
                }
            )
        }

        findViewById<TextView>(R.id.tv3).text = "跳转Java类的使用"
        findViewById<TextView>(R.id.tv3).setOnClickListener {
            startActivity(Intent(this@MainActivity, JavaMainActivity::class.java))
        }
    }

    private fun toCall() {
        val intent = Intent(Intent.ACTION_CALL)
        val data = Uri.parse("tel:18888888888")
        intent.data = data
        activity?.startActivity(intent)
    }
}
