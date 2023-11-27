package com.weioule.explainpermissionsutil

import java.io.Serializable

/**
 * Author by weioule.
 * Date on 2022/10/15.
 */
interface Callback<T> : Serializable {
    fun onCallback(granted: T)
}