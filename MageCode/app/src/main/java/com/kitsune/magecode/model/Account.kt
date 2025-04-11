package com.kitsune.magecode.model

import java.util.Date

data class Account(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val xp: Int = 0,
    val streak: Int = 0,
    val lastActive: Date = Date(),
    val selectedLanguages: List<String>,
    val level: Int
) {
    constructor() : this("", "", "", 0, 0, Date(), listOf(), 0)
}
