package xyz.bluspring.sunset.codec

import xyz.bluspring.sunset.serializer.Serializer
import xyz.bluspring.sunset.values.ConfigCategory
import java.nio.file.Path

object CategorySerializer {
    fun encode(category: ConfigCategory, path: Path, serializer: Serializer<Any?>) {
        serializer.saveSerialized(path, category.codec.encodeStart(serializer.ops, category.value)
            .result().orElseThrow())
    }

    fun decode(category: ConfigCategory, path: Path, serializer: Serializer<Any?>) {
        category.codec.decode(serializer.ops, serializer.readAsSerialized(path))
            .map { entry ->
                category.value = entry.first
            }
    }
}
