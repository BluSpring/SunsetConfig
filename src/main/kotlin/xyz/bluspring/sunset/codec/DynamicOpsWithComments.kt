package xyz.bluspring.sunset.codec

import com.mojang.serialization.DynamicOps

interface DynamicOpsWithComments<T> : DynamicOps<T> {
    fun createComment(value: String): T
}