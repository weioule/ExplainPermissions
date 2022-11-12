package com.weioule.explainpermissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.weioule.explainpermissionsutil.Callback
import com.weioule.explainpermissionsutil.ExplainBean
import com.weioule.explainpermissionsutil.ExplainPermissionsUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Author by weioule.
 * Date on 2022/11/05.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv1 -> {
                val list: ArrayList<ExplainBean> = ArrayList<ExplainBean>()
                list.add(
                    ExplainBean(
                        "拨打电话权限使用说明:",
                        "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;",
                        Manifest.permission.CALL_PHONE
                    )
                )
                list.add(
                    ExplainBean(
                        "位置信息权限使用说明:",
                        "我们想要访问你的位置，用于为您提供更好的服务哦;",
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
                ExplainPermissionsUtil.requestPermissionsNoDoubleClick(
                    this@MainActivity,
                    list,
                    object : Callback<Boolean?> {
                        override fun onCallback(granted: Boolean?) {
                            val intent = Intent(Intent.ACTION_CALL)
                            val data = Uri.parse("tel:18818181188")
                            intent.data = data
                            startActivity(intent)
                        }
                    })
            }
            R.id.tv2 -> {
                ExplainPermissionsUtil.requestPermissionsNoDoubleClick(
                    this@MainActivity,
                    ExplainBean(
                        "拨打电话权限使用说明:",
                        "我们想要拨打电话权限，用于您给客服小姐姐拨打电话哦;",
                        Manifest.permission.CALL_PHONE
                    ), object : Callback<Boolean?> {
                        override fun onCallback(granted: Boolean?) {
                            val intent = Intent(Intent.ACTION_CALL)
                            val data = Uri.parse("tel:18818181188")
                            intent.data = data
                            startActivity(intent)
                        }
                    })
            }
        }
    }
}