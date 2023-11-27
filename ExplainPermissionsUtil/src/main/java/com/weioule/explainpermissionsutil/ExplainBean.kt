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
    var permission: String = ""//权限标识

    constructor(permission: String, title: String, content: String) {
        this.name = title
        this.explain = content
        this.permission = permission
    }
}