package com.weioule.explainpermissionsutil

import android.os.Build
import android.text.TextUtils

object HarmonyUtils {
    /**
     * 是否为鸿蒙系统
     *
     * @return true为鸿蒙系统
     */
    val isHarmonyOs: Boolean
        get() = try {
            val buildExClass = Class.forName("com.huawei.system.BuildEx")
            val osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass)
            "Harmony".equals(osBrand.toString(), ignoreCase = true)
        } catch (x: Throwable) {
            false
        }

    /**
     * 获取鸿蒙系统版本号
     *
     * @return 版本号
     */
    val harmonyVersion: String
        get() = getProp("hw_sc.build.platform.version", "")

    /**
     * 获取属性
     * @param property
     * @param defaultValue
     * @return
     */
    private fun getProp(property: String, defaultValue: String): String {
        try {
            val spClz = Class.forName("android.os.SystemProperties")
            val method = spClz.getDeclaredMethod("get", String::class.java)
            val value = method.invoke(spClz, property) as String
            return if (TextUtils.isEmpty(value)) {
                defaultValue
            } else value
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return defaultValue
    }

    /**
     * 获得鸿蒙系统版本号（含小版本号，实际上同Android的android.os.Build.DISPLAY）
     * @return 版本号
     */
    val harmonyDisplayVersion: String
        get() = Build.DISPLAY
}