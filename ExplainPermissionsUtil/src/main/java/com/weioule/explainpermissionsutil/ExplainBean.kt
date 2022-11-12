package com.weioule.explainpermissionsutil

import java.io.Serializable

/**
 * Author by weioule.
 * Date on 2022/10/16.
 * 权限使用说明数据模型
 */
class ExplainBean : Serializable {
    var title: String? = null //使用说明标题，格式：“xxx权限使用说明：”，后续会根据“使用说明”文本做截取权限名称
    var content: String? = null //使用说明内容
    var permission: String? = null//权限

    constructor() {}
    constructor(title: String?, content: String?, permission: String?) {
        this.title = title
        this.content = content
        this.permission = permission
    }
}