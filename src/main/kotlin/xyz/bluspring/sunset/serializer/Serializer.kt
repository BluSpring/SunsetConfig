package xyz.bluspring.sunset.serializer

import com.mojang.serialization.DynamicOps
import java.nio.file.Path

interface Serializer<T> {
    val ops: DynamicOps<T>

    fun readAsSerialized(path: Path): T
    fun saveSerialized(path: Path, data: T)
}