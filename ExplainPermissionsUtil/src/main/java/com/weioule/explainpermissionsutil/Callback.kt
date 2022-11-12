package com.weioule.explainpermissionsutil

/**
 * Author by weioule.
 * Date on 2022/10/15.
 */
interface Callback<T> {
    fun onCallback(granted: T)
}