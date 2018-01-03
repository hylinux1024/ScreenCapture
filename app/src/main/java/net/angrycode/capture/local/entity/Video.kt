package net.angrycode.capture.local.entity

/**
 * Created by pc on 2018/1/3.
 */
data class Video(var path: String, var name: String, var thumb: String, var createTime: Long = 0, val duration: Long = 0)