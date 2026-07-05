package com.darkoled.app.model

data class UserPreferences(
    val interests: List<String> = emptyList(),
    val readHistory: List<String> = emptyList()
)
