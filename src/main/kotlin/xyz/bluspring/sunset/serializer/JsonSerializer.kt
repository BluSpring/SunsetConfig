package xyz.bluspring.sunset.serializer

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import java.io.IOException
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.reader
import kotlin.io.path.writeText

open class JsonSerializer(
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

    protected open fun writeJson(data: JsonElement, writer: JsonWriter) {
        Streams.write(data, writer)
    }

    override fun saveSerialized(path: Path, data: JsonElement) {
        val stringWriter = StringWriter()
        val writer = this.gson.newJsonWriter(stringWriter)
        try {
            this.writeJson(data, writer)
            path.writeText(stringWriter.toString(), Charsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                // why the hell is this not default?
                StandardOpenOption.TRUNCATE_EXISTING)
        } catch (e: IOException) {
            throw JsonIOException(e)
        }
    }
}
