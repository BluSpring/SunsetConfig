package xyz.bluspring.sunset.serializer.json

import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.internal.bind.TypeAdapters
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.Writer

private val NEW_LINE_METHOD = JsonWriter::class.java.getDeclaredMethod("newline").apply {
    this.isAccessible = true
}

private val OUT_FIELD = JsonWriter::class.java.getDeclaredField("out").apply {
    this.isAccessible = true
}

fun JsonWriter.comment(comment: String): JsonWriter {
    NEW_LINE_METHOD.invoke(this)
    val writer = OUT_FIELD.get(this) as Writer

    if (!comment.contains("\n")) {
        writer.write("/* $comment */")
    } else {
        writer.write("/*")
        for (line in comment.split("\n")) {
            NEW_LINE_METHOD.invoke(this)
            writer.write(line)
        }
        NEW_LINE_METHOD.invoke(this)
        writer.write("*/")
    }

    return this
}

val COMMENTED_TYPE_ADAPTER = object : TypeAdapter<JsonElement>() {
    override fun write(writer: JsonWriter, value: JsonElement?) {
        if (value is JsonComment) {
            writer.comment(value.comment)
            return
        }

        // copied directly from TypeAdapters
        if (value == null || value.isJsonNull) {
            writer.nullValue()
        } else if (value.isJsonPrimitive) {
            val primitive = value.asJsonPrimitive
            if (primitive.isNumber) {
                writer.value(primitive.asNumber)
            } else if (primitive.isBoolean) {
                writer.value(primitive.asBoolean)
            } else {
                writer.value(primitive.asString)
            }
        } else if (value.isJsonArray) {
            writer.beginArray()
            for (e in value.asJsonArray) {
                write(writer, e)
            }
            writer.endArray()
        } else if (value.isJsonObject) {
            writer.beginObject()
            for (e in value.asJsonObject.entrySet()) {
                // except this, we handle comments here
                if (e.value is JsonComment) {
                    write(writer, e.value)
                    continue
                }

                writer.name(e.key)
                write(writer, e.value)
            }
            writer.endObject()
        } else {
            throw IllegalArgumentException("Couldn't write " + value.javaClass)
        }
    }

    override fun read(reader: JsonReader): JsonElement {
        // we shouldn't parse comments
        return TypeAdapters.JSON_ELEMENT.read(reader)
    }

}