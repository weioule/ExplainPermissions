package com.weioule.explainpermissionsutil

import java.io.Serializable

/**
 * Author by weioule.
 * Date on 2022/10/16.
 * 权限使用说明数据模型
 */
class ExplainBean : Serializable {
    var name: String = "" //权限名称
    var explain: String = "" //权限使用说明内容
    var permissions: MutableList<String> = mutableListOf()//权限标识组

    constructor(title: String, content: String, vararg permissions: String) {
        this.name = title
        this.explain = content
        this.permissions = permissions.toMutableList()
    }
}