package xyz.bluspring.sunset.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.reader
import kotlin.io.path.writeText

class JsonSerializer(
    private val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()
) : Serializer<JsonElement> {
    override val ops: DynamicOps<JsonElement>
        get() = JsonOps.INSTANCE

    override fun readAsSerialized(path: Path): JsonElement {
        return JsonParser.parseReader(path.reader(Charsets.UTF_8, StandardOpenOption.READ))
    }

    override fun saveSerialized(path: Path, data: JsonElement) {
        path.writeText(this.gson.toJson(data), Charsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }
}
