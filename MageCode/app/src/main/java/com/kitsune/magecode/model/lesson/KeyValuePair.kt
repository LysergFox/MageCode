package com.kitsune.magecode.model.lesson

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class KeyValuePair(
    val key: String = "",
    val value: String = ""
) : Parcelable
