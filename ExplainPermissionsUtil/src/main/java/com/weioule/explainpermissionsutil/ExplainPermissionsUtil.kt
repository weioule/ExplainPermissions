package com.weioule.explainpermissionsutil

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.io.Serializable
import java.util.Calendar

/**
 * Author by weioule.
 * Date on 2022/10/15.
 */
class ExplainPermissionsUtil : AppCompatActivity() {

    private val PERMISSION_REQUEST = 18118
    private var toSetting = false

    //需要申请的权限字符串数组
    private lateinit var permissions: Array<String>
    private lateinit var recyclerView: RecyclerView
    private var callback: Callback<Boolean>? = null

    //用于展示未授予的权限列表数据
    private var explainList: MutableList<ExplainBean> = mutableListOf()

    //原始的列表数据，因为跳转系统设置页面可以授予也可以取消权限，用于返回页面时同步更新所有权限状态
    private var primitiveExplainList: MutableList<ExplainBean> = mutableListOf()

    companion object {
        private var lastClickTime: Long = 0
        private const val MIN_CLICK_DELAY_TIME = 800
        private const val CALLBACK = "callback"
        private const val EXPLAIN_LIST = "explain_list"

        @JvmOverloads
        fun requestPermission(
            activity: FragmentActivity,
            bean: ExplainBean,
            callback: Callback<Boolean>?
        ) {
            var permissionList = mutableListOf<ExplainBean>()
            permissionList.add(bean)
            requestPermissions(activity, permissionList, callback)
        }

        @JvmOverloads
        fun requestPermissions(
            activity: FragmentActivity,
            permissionList: MutableList<ExplainBean>,
            callback: Callback<Boolean>?
        ) {
            //过滤重复点击
            val currentTime = Calendar.getInstance().timeInMillis
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime

                if (permissionList.isNullOrEmpty()) {
                    throw RuntimeException("The permission list cannot be empty.")
                }

                var hasAllPermission = true
                for (explainBean in permissionList) {
                    val permissionCheck =
                        ContextCompat.checkSelfPermission(activity, explainBean.permission)
                    if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                        hasAllPermission = false
                        break
                    }
                }

                //检验权限是否已授予
                if (hasAllPermission) {
                    callback?.onCallback(true)
                    return
                }

                val intent = Intent(activity, ExplainPermissionsUtil::class.java)
                val bundle = Bundle()
                //这里需要注意：callback需要序列化，在外部使用匿名内部类的形式传进来的callback，因为它持有外部类的引用，外部类也需要序列化，不然会传递不过去，所以我们的MainActivity也实现了Serializable接口
                bundle.putSerializable(CALLBACK, callback as Serializable?)
                bundle.putSerializable(EXPLAIN_LIST, permissionList as Serializable?)

                intent.putExtras(bundle)
                activity.startActivity(intent)

                //取消页面启动动画，提升用户体验
                activity.overridePendingTransition(0, 0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.permissions_explain_activity)

        callback = intent.getSerializableExtra(CALLBACK) as Callback<Boolean>?
        primitiveExplainList = intent.getSerializableExtra(EXPLAIN_LIST) as MutableList<ExplainBean>

        //处理获取权限字符串数组与展示的使用说明列表
        hasAllPermission()

        //申请权限
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this@ExplainPermissionsUtil)
        recyclerView.adapter = object : BaseQuickAdapter<ExplainBean, BaseViewHolder>(
            R.layout.item_explain_list,
            explainList.toMutableList()
        ) {
            override fun convert(holder: BaseViewHolder, item: ExplainBean) {
                holder.setText(R.id.tv_title, item.name + "使用说明:")
                holder.setText(R.id.tv_content, item.explain)
            }
        }

        val divider = RecyclerViewDivider.Builder(this@ExplainPermissionsUtil)
            .setStyle(RecyclerViewDivider.Style.Companion.END)
            .setColor(0x00000000)
            .setSize(15f)
            .build()
        recyclerView.addItemDecoration(divider)
    }

    override fun onResume() {
        overridePendingTransition(0, 0)
        super.onResume()
        //跳转设置页面回来校验是否已授权
        if (toSetting && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toSetting = false

            if (!hasAllPermission()) {
                //同步刷新未授予的权限使用说明
                (recyclerView.adapter as BaseQuickAdapter<ExplainBean, BaseViewHolder>).setList(
                    explainList
                )

                //重新申请委授予的权限
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //过滤 grantResults.length == 0 是只弹出授权框，未选择点击的情况
        if (requestCode == PERMISSION_REQUEST && grantResults.isNotEmpty()) {
            var firstAppend = true
            var sb = StringBuffer("您已拒绝：\"")
            for ((i, permission) in permissions.withIndex()) {
                //没有授予的权限
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (!firstAppend)
                        sb.append("、")
                    else
                        firstAppend = false

                    for (explainBean in explainList) {
                        if (permission == explainBean.permission) {
                            sb.append(explainBean.name)
                            break
                        }
                    }
                }
            }

            if (firstAppend) {
                //已全部授予权限
                callback?.onCallback(true)
                toFinish()
            } else {
                sb.append("\"，去设置页面打开相应权限")
                showMsgDialog(sb.toString())
            }
        }
    }

    private fun hasAllPermission(): Boolean {
        val permissionsList = mutableListOf<String>()
        val explainList = mutableListOf<ExplainBean>()
        explainList.addAll(primitiveExplainList)
        if (explainList.isNotEmpty()) {
            for (i in explainList.size - 1 downTo 0) {
                //检验权限是否已授予
                val permissionCheck =
                    ContextCompat.checkSelfPermission(this, explainList[i].permission)
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    explainList.removeAt(i)
                } else {
                    permissionsList.add(explainList[i].permission)
                }
            }
        }

        if (permissionsList.isEmpty()) {
            callback?.onCallback(true)
            toFinish()
            return true
        }

        this.explainList = explainList
        this.permissions = permissionsList.toTypedArray()

        return false
    }

    private fun toFinish() {
        finish()
        //取消页面切换动画，提升用户体验
        overridePendingTransition(0, 0)
    }

    private fun showMsgDialog(msg: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("温馨提示")
        builder.setMessage(msg)
        builder.setPositiveButton("好的") { dialogInterface, _ ->
            //引导用户到设置中去开启权限
            PermissionManagementUtil.goToSetting(this)
            overridePendingTransition(0, 0)
            dialogInterface.dismiss()
            toSetting = true
        }.setNegativeButton("取消") { dialogInterface, _ ->
            dialogInterface.dismiss()
            toFinish()
        }.show()
            .setCanceledOnTouchOutside(false)
    }
}
