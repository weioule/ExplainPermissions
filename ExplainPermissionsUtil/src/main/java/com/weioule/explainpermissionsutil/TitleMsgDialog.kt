package com.weioule.explainpermissionsutil

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView

/**
 * Author by weioule.
 * Date on 2022/10/16.
 */
class TitleMsgDialog : Dialog, View.OnClickListener {

    private var width = 0
    private var type: Int = 2
    private var line: View? = null
    private var tvMsg: TextView? = null
    private var tvTitle: TextView? = null
    private var btnLeft: TextView? = null
    private var btnRight: TextView? = null
    private var callback: Callback<Boolean>? = null

    companion object {
        const val TYPE_YES_OR_NO = 2
        const val TYPE_CONFIRM = 1
    }

    constructor(context: Context?, type: Int, cb: Callback<Boolean>?) : super(
        context!!, R.style.dialog
    ) {
        this.type = type
        callback = cb
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.title_msg_dialig)
        initView()
        initSize()
        initType()
        addListener()
    }

    private fun initSize() {
        val dialogWindow = this.window
        val displayMetrics = DisplayMetrics()
        dialogWindow!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val p = dialogWindow.attributes
        val mConfiguration = this.context.resources.configuration
        val ori = mConfiguration.orientation
        if (ori == 2) {
            p.width = (width.toDouble() * 0.35).toInt()
        } else if (ori == 1) {
            p.width = (width.toDouble() * 0.9).toInt()
        }
        this.width = p.width
        dialogWindow.attributes = p
    }

    private fun initView() {
        tvTitle = findViewById(R.id.tv_title)
        tvMsg = findViewById(R.id.tv_msg)
        btnLeft = findViewById(R.id.btn_left)
        btnRight = findViewById(R.id.btn_right)
        line = findViewById(R.id.line)
    }

    private fun initType() {
        when (type) {
            TYPE_YES_OR_NO -> {
                btnLeft!!.visibility = View.VISIBLE
                btnRight!!.visibility = View.VISIBLE
                line!!.visibility = View.VISIBLE
            }
            TYPE_CONFIRM -> {
                btnLeft!!.visibility = View.GONE
                line!!.visibility = View.GONE
                btnRight!!.visibility = View.VISIBLE
            }
        }
    }

    private fun addListener() {
        btnLeft!!.setOnClickListener(this)
        btnRight!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_left)
            doCallback(false)
        else if (v.id == R.id.btn_right)
            doCallback(true)

        dismiss()
    }

    private fun doCallback(result: Boolean) {
        if (callback != null) {
            callback!!.onCallback(result)
        }
    }

    fun setTitleTextSize(size: Float) {
        tvTitle!!.textSize = size
    }

    fun setTitleTextColor(color: Int) {
        tvTitle!!.setTextColor(color)
    }

    fun setMessage(msgRes: Int) {
        var msg: String? = null
        try {
            msg = this.context.resources.getString(msgRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!TextUtils.isEmpty(msg)) {
            this.setText(null as String?, msg)
        } else {
            throw RuntimeException("title and msg can not both null,please check your resource id.")
        }
    }

    fun setMessage(msg: String?) {
        this.setText(null as String?, msg)
    }

    fun setText(titleRes: Int, msgRes: Int) {
        var title: String? = null
        var msg: String? = null
        try {
            title = this.context.resources.getString(titleRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            msg = this.context.resources.getString(msgRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(msg)) {
            throw RuntimeException("title and msg can not both null,please check your resource id.")
        } else {
            this.setText(title, msg)
        }
    }

    fun setText(title: String?, msg: String?) {
        if (TextUtils.isEmpty(title)) tvTitle!!.text = title
        if (TextUtils.isEmpty(msg)) {
            tvMsg!!.text = msg
        }
    }

    fun getView(resId: Int): View {
        return findViewById(resId)
    }

    fun setMessageTextSize(size: Float) {
        tvMsg!!.textSize = size
    }

    fun setMessageTextColor(color: Int) {
        tvMsg!!.setTextColor(color)
    }

    fun setMessageTextGravity(gravity: Int) {
        tvMsg!!.gravity = gravity
    }

    fun setLeftButtonText(s_left: String?) {
        if (TextUtils.isEmpty(s_left)) {
            btnLeft!!.visibility = View.GONE
        } else {
            btnLeft!!.text = s_left
            btnLeft!!.visibility = View.VISIBLE
        }
    }

    fun setLeftButtonText(resid: Int) {
        val s = this.context.resources.getString(resid)
        if (TextUtils.isEmpty(s)) {
            btnLeft!!.visibility = View.GONE
        } else {
            btnLeft!!.text = s
            btnLeft!!.visibility = View.VISIBLE
        }
    }

    fun setLeftButtonTextSize(size: Float) {
        btnLeft!!.textSize = size
    }

    fun setLeftButtonTextColor(color: Int) {
        btnLeft!!.setTextColor(color)
    }

    fun setRightButtonText(s_right: String?) {
        if (TextUtils.isEmpty(s_right)) {
            btnRight!!.visibility = View.GONE
        } else {
            btnRight!!.text = s_right
            btnRight!!.visibility = View.VISIBLE
        }
    }

    fun setRightButtonText(resId: Int) {
        val s = this.context.resources.getString(resId)
        if (TextUtils.isEmpty(s)) {
            btnRight!!.visibility = View.GONE
        } else {
            btnRight!!.text = s
            btnRight!!.visibility = View.VISIBLE
        }
    }

    fun setRightButtonTextSize(size: Float) {
        btnRight!!.textSize = size
    }

    fun setRightButtonTextColor(color: Int) {
        btnRight!!.setTextColor(color)
    }

    fun setOnCallBackListener(callback: Callback<Boolean>) {
        this.callback = callback;
    }
}