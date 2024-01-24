package com.lctafrica.kplc.medicare.utility

object ResultFactory {

    fun <T> getSuccessResult(data: T): Result<T> {
        return Result(success = true, data = data)
    }
}