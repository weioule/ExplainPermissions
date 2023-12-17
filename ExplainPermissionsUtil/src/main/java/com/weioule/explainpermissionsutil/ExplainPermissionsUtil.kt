package com.weioule.explainpermissionsutil

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.view.Gravity
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
import kotlin.system.exitProcess

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
    private lateinit var intercept: Intercept

    //用于展示未授予的权限列表数据
    private var explainList: MutableList<ExplainBean> = mutableListOf()

    //原始的列表数据，因为跳转系统设置页面可以授予也可以取消权限，用于返回页面时同步更新所有权限状态
    private var primitiveExplainList: MutableList<ExplainBean> = mutableListOf()

    companion object {
        private var lastClickTime: Long = 0
        private const val MIN_CLICK_DELAY_TIME = 800
        private const val INTERCEPT = "intercept"
        private const val EXPLAIN_LIST = "explain_list"
        private var callback: Callback<Boolean>? = null

        @JvmOverloads
        fun requestPermission(
            activity: FragmentActivity,
            intercept: Intercept,
            bean: ExplainBean,
            callback: Callback<Boolean>?
        ) {
            var permissionList = mutableListOf<ExplainBean>()
            permissionList.add(bean)
            requestPermissions(activity, intercept, permissionList, callback)
        }

        @JvmOverloads
        fun requestPermissions(
            activity: FragmentActivity,
            intercept: Intercept,
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

                val intent = Intent(activity, ExplainPermissionsUtil::class.java)
                val bundle = Bundle()
                bundle.putSerializable(EXPLAIN_LIST, permissionList as Serializable?)
                bundle.putSerializable(INTERCEPT, intercept as Serializable?)
                this.callback = callback

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
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val vis = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = option or vis
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        //设置透明导航栏
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (window.attributes.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION === 0) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            }
        }
        val decorView = window.decorView
        val vis = decorView.systemUiVisibility
        val option =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = vis or option


        intercept = intent.getSerializableExtra(INTERCEPT) as Intercept
        primitiveExplainList = intent.getSerializableExtra(EXPLAIN_LIST) as MutableList<ExplainBean>

        //处理权限字符串数组与展示的使用说明列表
        if (hasAllPermission())
            return

        //申请权限
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)

        if (intercept != Intercept.NORMAL) {
            setContentView(R.layout.permissions_explain_activity)

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
            //判断是否所有的权限都已经授予了
            val unGranted = ArrayList<Int>()
            var shouldShow: Boolean? = null
            for ((position, grant) in grantResults.withIndex()) {
                if (grant == PackageManager.PERMISSION_DENIED) {
                    if (intercept == Intercept.NORMAL) {
                        //不需要弹框级别的在这里就直接返回了
                        Companion.callback?.onCallback(false)
                        toFinish()
                        return
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        shouldShow = shouldShowRequestPermissionRationale(permissions[position])
                    }

                    unGranted.add(position)
                }
            }

            if (unGranted.size == 0) {
                //已全部授予权限
                Companion.callback?.onCallback(true)
                toFinish()
            } else if (intercept == Intercept.LOW && (shouldShow == null || shouldShow == true)) {
                //LOW级别，未设置不再提示，就不弹框
                toFinish()
            } else {
                var firstAppend = true
                var sb = StringBuffer("您未授予：\"")
                label1@ for (i in unGranted) {
                    for (explainBean in explainList) {
                        //没有授予的权限
                        if (explainBean.permissions.contains(permissions[i])) {
                            if (firstAppend) {
                                firstAppend = false
                                sb.append(explainBean.name)
                            } else if (!sb.contains(explainBean.name)) {
                                sb.append("、")
                                sb.append(explainBean.name)
                            }

                            continue@label1
                        }
                    }
                }

                sb.append("\"，去设置页面打开相应权限")
                showMsgDialog(sb.toString())
            }
        }
    }

    private fun hasAllPermission(): Boolean {
        explainList.clear()
        val permissionsList = mutableListOf<String>()
        label1@ for (explainBean in primitiveExplainList) {
            for (permission in explainBean.permissions) {
                //检验权限是否已授予
                val permissionCheck =
                    ContextCompat.checkSelfPermission(this, permission)
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    explainList.add(explainBean)
                    permissionsList.addAll(explainBean.permissions)
                    continue@label1
                }
            }
        }

        if (explainList.isEmpty()) {
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
        var dialog = builder.setPositiveButton("好的") { dialogInterface, _ ->
            //引导用户到设置中去开启权限
            PermissionManagementUtil.goToSetting(this)
            overridePendingTransition(0, 0)
            dialogInterface.dismiss()
            toSetting = true
        }.setNegativeButton("取消") { dialogInterface, _ ->
            if (intercept == Intercept.HIGH) {
                try {
                    Process.killProcess(Process.myPid())
                    exitProcess(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                dialogInterface.dismiss()
                toFinish()
            }
        }.show()

        //调整蒙版的灰度
        dialog.window?.attributes?.dimAmount = 0f

        dialog.setCancelable(false)

        //解决部分红米手机不居中或显示不全的问题
        //放在show()之后，不然有些属性是没有效果的，比如height和width
        dialog.window?.run {
            attributes = attributes.apply {
                width = (windowManager.defaultDisplay.width * 0.95).toInt()
                gravity = Gravity.CENTER
            }
        }
    }


    /**
     * 拦权限截级别
     * HIGH         必须授予权限，不允许返回，否则将不允许使用App（拒绝后弹提示框，点击取消会杀死程序）
     * MEDIUM       必须授予权限才能往下执行，但允许返回（拒绝后弹提示框，可以点取消）
     * LOW          可以不授予权限（拒绝并不再提醒后才弹提示框，可以点取消）
     * NORMAL       不展示权限说明（拒绝后不弹提示框）
     */
    enum class Intercept : Serializable {
        HIGH, MEDIUM, LOW, NORMAL
    }

}
