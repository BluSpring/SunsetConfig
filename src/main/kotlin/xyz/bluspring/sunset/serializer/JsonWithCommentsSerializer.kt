package xyz.bluspring.sunset.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.stream.JsonWriter
import com.mojang.serialization.DynamicOps
import xyz.bluspring.sunset.serializer.json.COMMENTED_TYPE_ADAPTER
import xyz.bluspring.sunset.serializer.json.JsonOpsWithComments

class JsonWithCommentsSerializer(
    gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()
) : JsonSerializer(gson) {
    override val ops: DynamicOps<JsonElement> = JsonOpsWithComments

    override fun writeJson(data: JsonElement, writer: JsonWriter) {
        COMMENTED_TYPE_ADAPTER.write(writer, data)
    }
}
