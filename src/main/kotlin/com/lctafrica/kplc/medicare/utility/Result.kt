package com.lctafrica.kplc.medicare.utility

data class Result<T> (
    val success: Boolean,
    val data: T? = null,
    val msg: String? = null
)