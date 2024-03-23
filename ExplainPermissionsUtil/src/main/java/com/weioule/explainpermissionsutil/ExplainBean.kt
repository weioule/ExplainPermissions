package com.weioule.explainpermissionsutil

import android.os.Parcel
import android.os.Parcelable

/**
 * Author by weioule.
 * Date on 2022/10/16.
 * 权限使用说明数据模型
 */
class ExplainBean() : Parcelable {
    var name: String = "" //权限名称
    var explain: String = "" //权限使用说明内容
    var permissions: Array<String> = arrayOf()//权限标识组

    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
        explain = parcel.readString().toString()
        permissions = parcel.createStringArray() as Array<String>
    }

    constructor(title: String, content: String, vararg permissions: String) : this() {
        this.name = title
        this.explain = content
        this.permissions = permissions as Array<String>
    }

    override fun toString(): String {
        return "ExplainBean(name='$name', explain='$explain', permissions=${permissions.contentToString()})"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(explain)
        parcel.writeStringArray(permissions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExplainBean> {
        override fun createFromParcel(parcel: Parcel): ExplainBean {
            return ExplainBean(parcel)
        }

        override fun newArray(size: Int): Array<ExplainBean?> {
            return arrayOfNulls(size)
        }
    }


}