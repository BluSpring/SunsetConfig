package xyz.bluspring.sunset.serializer.json

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import xyz.bluspring.sunset.codec.DynamicOpsWithComments

object JsonOpsWithComments : JsonOps(false), DynamicOpsWithComments<JsonElement> {
    override fun createComment(value: String): JsonElement {
        return JsonComment(value)
    }
}