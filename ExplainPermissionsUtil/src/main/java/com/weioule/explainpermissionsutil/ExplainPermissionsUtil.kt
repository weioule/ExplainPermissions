package com.weioule.explainpermissionsutil

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
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
import java.util.*

/**
 * Author by weioule.
 * Date on 2022/10/15.
 */
class ExplainPermissionsUtil : AppCompatActivity() {

    private var delay: Long = 0
    private var toSetting = false
    private var callback: Callback<*>? = null
    private lateinit var permissions: Array<String?>
    private var refusedDialog: TitleMsgDialog? = null
    private var refusedAgainDialog: TitleMsgDialog? = null
    private var explainList: List<ExplainBean>? = null

    companion object {
        private const val MIN_CLICK_DELAY_TIME = 1000
        private var lastClickTime: Long = 0
        private const val PERMISSION_REQUEST = 18118
        private const val DELAY = "delay"
        private const val CALLBACK_KEY = "callback_key"
        private const val EXPLAIN_LIST = "explain_list"
        private val callbackMap: MutableMap<Long, Callback<*>> = HashMap()

        @JvmOverloads
        fun requestPermissionsNoDoubleClick(
            activity: FragmentActivity,
            bean: ExplainBean,
            callback: Callback<Boolean?>,
            delay: Long = 200
        ) {
            val list = ArrayList<ExplainBean>()
            list.add(bean)
            requestPermissionsNoDoubleClick(activity, list, callback, delay)
        }

        @JvmOverloads
        fun requestPermissionsNoDoubleClick(
            activity: FragmentActivity,
            list: List<ExplainBean>?,
            callback: Callback<Boolean?>,
            delay: Long = 200
        ) {
            //??????????????????
            val currentTime = Calendar.getInstance().timeInMillis
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime
                requestPermissions(activity, list, callback, delay)
            }
        }

        @JvmOverloads
        fun requestPermissions(
            activity: FragmentActivity,
            bean: ExplainBean,
            callback: Callback<Boolean?>,
            delay: Long = 200
        ) {
            val list = ArrayList<ExplainBean>()
            list.add(bean)
            requestPermissions(activity, list, callback, delay)
        }

        /**
         * @param list     ???????????????????????????
         * @param callback ????????????????????????
         * @param delay    ??????????????????????????????????????????
         */
        @JvmOverloads
        fun requestPermissions(
            activity: FragmentActivity,
            list: List<ExplainBean>?,
            callback: Callback<Boolean?>,
            delay: Long = 200
        ) {
            val intent = Intent(activity, ExplainPermissionsUtil::class.java)
            val bundle = Bundle()
            val currentTimeMillis = System.currentTimeMillis()
            callbackMap[currentTimeMillis] = callback
            bundle.putLong(DELAY, delay)
            bundle.putLong(CALLBACK_KEY, currentTimeMillis)
            bundle.putSerializable(EXPLAIN_LIST, list as Serializable?)
            intent.putExtras(bundle)
            activity.startActivity(intent)

            //?????????????????????????????????????????????
            activity.overridePendingTransition(0, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //?????????????????????
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

        setContentView(R.layout.reqest_permissions_activity)
        val contentView = findViewById<ViewGroup>(R.id.content)

        val callbackKey = intent.getLongExtra(CALLBACK_KEY, 0)
        callback = callbackMap[callbackKey]
        callbackMap.remove(callbackKey)
        delay = intent.getLongExtra(DELAY, 200)
        explainList = intent.getSerializableExtra(EXPLAIN_LIST) as List<ExplainBean>?
        permissions = arrayOfNulls(explainList!!.size)
        for (i in explainList!!.indices) {
            permissions[i] = explainList!![i].permission
        }

        //????????????????????????
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)

        //????????????????????????????????????????????????????????????????????????finish??????????????????????????????????????????????????????????????????????????????
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread { //????????????????????????????????????
                    val inflate = LayoutInflater.from(this@ExplainPermissionsUtil)
                        .inflate(R.layout.permissions_explain_dialog, null, false)
                    inflate.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    contentView.addView(inflate)
                    val recyclerView: RecyclerView = inflate.findViewById(R.id.recycler_view)
                    recyclerView.layoutManager = LinearLayoutManager(this@ExplainPermissionsUtil)
                    recyclerView.adapter = object : BaseQuickAdapter<ExplainBean?, BaseViewHolder>(
                        R.layout.item_explain_list,
                        explainList?.toMutableList()
                    ) {
                        override fun convert(holder: BaseViewHolder, item: ExplainBean?) {
                            holder.setText(R.id.tv_title, item?.title)
                            holder.setText(R.id.tv_content, item?.content)
                        }
                    }

                    val divider = RecyclerViewDivider.Builder(this@ExplainPermissionsUtil)
                        .setStyle(RecyclerViewDivider.Style.Companion.END)
                        .setColor(0x00000000)
                        .setSize(15f)
                        .build()
                    recyclerView.addItemDecoration(divider!!)

                    //?????????????????????????????????????????????
                    contentView.setBackgroundColor(-0x1)
                }
            }
        }

        val timer = Timer()
        timer.schedule(task, delay)
    }

    override fun onResume() {
        super.onResume()
        //?????????????????????????????????????????????
        if (toSetting && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toSetting = false
            var hasPermission = true
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission!!
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    hasPermission = false
                    break
                }
            }
            if (hasPermission) if (null != callback) callback!!.onCallback(true as Nothing) else ActivityCompat.requestPermissions(
                this,
                permissions,
                PERMISSION_REQUEST
            )
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //?????? grantResults.length == 0 ????????????????????????????????????????????????
        if (requestCode == PERMISSION_REQUEST && grantResults.isNotEmpty()) {
            var isAllGranted = true

            //?????????????????????????????????????????????
            for (grant in grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false
                }
            }
            if (isAllGranted) {
                //?????????????????????
                if (null != callback) callback!!.onCallback(true as Nothing)
                toFinish()
            } else {
                var shouldShow = true
                for (permission in permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            permission
                        )
                    ) {
                        shouldShow = false
                        break
                    }
                }

                //????????????????????????
                if (shouldShow) {
                    if (null == refusedDialog || !refusedDialog!!.isShowing) {
                        refusedDialog =
                            TitleMsgDialog(this, TitleMsgDialog.Companion.TYPE_YES_OR_NO,
                                object : Callback<Boolean> {
                                    override fun onCallback(granted: Boolean) {
                                        if (!granted) {
                                            toFinish()
                                        } else {
                                            ActivityCompat.requestPermissions(
                                                this@ExplainPermissionsUtil,
                                                permissions,
                                                PERMISSION_REQUEST
                                            )
                                        }
                                    }
                                })
                        refusedDialog!!.show()
                        refusedDialog!!.setText(null, "?????????" + getPermissionsName(grantResults))
                        refusedDialog!!.setRightButtonText("??????")
                        refusedDialog!!.setCancelable(false)
                    }
                } else if (null == refusedAgainDialog || !refusedAgainDialog!!.isShowing) {
                    //???????????????????????????????????????
                    refusedAgainDialog = TitleMsgDialog(
                        this,
                        TitleMsgDialog.Companion.TYPE_YES_OR_NO,
                        object : Callback<Boolean> {
                            override fun onCallback(granted: Boolean) {
                                if (!granted) {
                                    toFinish()
                                } else {
                                    //??????????????????????????????
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                                    intent.data = Uri.parse("package:$packageName")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                    startActivity(intent)
                                    toSetting = true
                                }
                            }
                        })
                    refusedAgainDialog!!.show()
                    refusedAgainDialog!!.setText(
                        null,
                        "???????????????????????????????????????????????????????????????" + getPermissionsName(grantResults)
                    )
                    refusedAgainDialog!!.setRightButtonText("??????")
                    refusedAgainDialog!!.setCancelable(false)
                }
            }
        }
    }

    private fun toFinish() {
        finish()
        //?????????????????????????????????????????????
        overridePendingTransition(0, 0)
    }

    private fun getPermissionsName(grantResults: IntArray): String {
        var str = "????????????"
        if (null == explainList || explainList!!.isEmpty()) {
            return str
        }
        val sb = StringBuffer()
        for (i in grantResults.indices) {
            //?????????????????????
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                val bean = explainList!![i]
                if (null != bean) {
                    val title = bean.title
                    if (!TextUtils.isEmpty(title)) {
                        try {
                            //??????????????????????????????????????????????????????????????????
                            val split = title!!.split("????????????".toRegex()).toTypedArray()
                            if (null != split && split.isNotEmpty()) {
                                sb.append(split[0])
                                sb.append("???")
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
        if (sb.isNotEmpty()) str = sb.substring(0, sb.length - 1)
        return str
    }
}
