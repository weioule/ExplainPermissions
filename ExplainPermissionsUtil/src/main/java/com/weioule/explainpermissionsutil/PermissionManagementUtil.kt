package com.weioule.explainpermissionsutil

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Created by weioule
 * on 2023/11/05
 */
object PermissionManagementUtil {
    /**
     * Build.MANUFACTURER
     */
    private const val MANUFACTURER_HUAWEI = "HUAWEI"
    private const val MANUFACTURER_MEIZU = "Meizu"
    private const val MANUFACTURER_XIAOMI = "Xiaomi"
    private const val MANUFACTURER_SONY = "Sony"
    private const val MANUFACTURER_OPPO = "OPPO"
    private const val MANUFACTURER_LG = "LG"
    private const val MANUFACTURER_VIVO = "vivo"
    fun goToSetting(activity: Activity) {
        when (Build.MANUFACTURER) {
            MANUFACTURER_HUAWEI -> Huawei(activity)
            MANUFACTURER_MEIZU -> Meizu(activity)
            MANUFACTURER_XIAOMI -> Xiaomi(activity)
            MANUFACTURER_SONY -> Sony(activity)
            MANUFACTURER_OPPO -> OPPO(activity)
            MANUFACTURER_VIVO -> VIVO(activity)
            MANUFACTURER_LG -> LG(activity)
            else -> goApplicationInfo(activity)
        }
    }

    private fun Huawei(activity: Activity) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("packageName", activity.applicationInfo.packageName)

            var comp = if (HarmonyUtils.isHarmonyOs)
                ComponentName(
                    "om.ohos.permissionmanager",
                    "com.ohos.permissionmanager.SpecificAbility"
                )
            else
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity"
                )

            intent.component = comp
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            goDetailsSetting(activity)
        }
    }

    private fun Meizu(activity: Activity) {
        try {
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("packageName", activity.packageName)
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            goDetailsSetting(activity)
        }
    }

    private fun Xiaomi(activity: Activity) {
        try {
            if ("Redmi" == Build.BRAND && "M2012K11C" == Build.MODEL && "12" == Build.VERSION.RELEASE) {
                //红米手机的bug，该型号的手机使用else里面的方式去设置权限后不起效
                goDetailsSetting(activity)
            } else {
                val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("extra_pkgname", activity.packageName)
                val componentName = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
                intent.component = componentName
                activity.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            goDetailsSetting(activity)
        }
    }

    private fun Sony(activity: Activity) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("packageName", activity.packageName)
            val comp = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
            intent.component = comp
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            goDetailsSetting(activity)
        }
    }

    private fun OPPO(activity: Activity) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("packageName", activity.packageName)
            //ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
            val comp = ComponentName(
                "com.coloros.securitypermission",
                "com.coloros.securitypermission.permission.PermissionAppAllPermissionActivity"
            ) //R11t 7.1.1 os-v3.2
            intent.component = comp
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            goDetailsSetting(activity)
        }
    }

    private fun VIVO(activity: Activity) {
        val intent: Intent
        if (Build.MODEL.contains("Y85") && !Build.MODEL.contains("Y85A") || Build.MODEL.contains("vivo Y53L")) {
            intent = Intent()
            intent.setClassName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.PurviewTabActivity"
            )
            intent.putExtra("packagename", activity.packageName)
            intent.putExtra("tabId", "1")
        } else {
            intent = Intent()
            intent.setClassName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"
            )
            intent.action = "secure.intent.action.softPermissionDetail"
            intent.putExtra("packagename", activity.packageName)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
    }

    private fun LG(activity: Activity) {
        try {
            val intent = Intent("android.intent.action.MAIN")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("packageName", activity.packageName)
            val comp = ComponentName(
                "com.android.settings",
                "com.android.settings.Settings\$AccessLockSummaryActivity"
            )
            intent.component = comp
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            goDetailsSetting(activity)
        }
    }

    /**
     * 应用详细页
     */
    private fun goDetailsSetting(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        try {
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 应用信息界面
     */
    private fun goApplicationInfo(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 9) {
            goDetailsSetting(activity)
        } else if (Build.VERSION.SDK_INT <= 8) {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Intent.ACTION_VIEW
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            intent.putExtra("com.android.settings.ApplicationPkgName", activity.packageName)
            activity.startActivity(intent)
        }
    }
}