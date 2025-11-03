package xyz.bluspring.sunset.values

import com.mojang.serialization.Codec
import kotlin.reflect.KMutableProperty

open class RangedConfigValue<T : Number>(id: String, codec: Codec<T>, property: KMutableProperty<T>, owner: Any? = null, val min: T, val max: T, val step: T) : ReflectingConfigValue<T>(id, codec, property, owner) {
}