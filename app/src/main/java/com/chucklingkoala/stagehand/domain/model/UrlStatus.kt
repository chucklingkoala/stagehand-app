package com.chucklingkoala.stagehand.domain.model

enum class UrlStatus(val value: String) {
    ON_SHOW("on_show"),
    DUMP("dump");

    companion object {
        fun fromString(value: String?): UrlStatus? {
            return when (value) {
                "on_show" -> ON_SHOW
                "dump" -> DUMP
                else -> null
            }
        }
    }
}
