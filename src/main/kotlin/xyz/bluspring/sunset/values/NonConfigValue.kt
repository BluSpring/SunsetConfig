package xyz.bluspring.sunset.values

import com.mojang.serialization.Codec
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Non-config value, may be used for adding more information within GUIs as you wish.
 */
abstract class NonConfigValue(id: String) : ConfigValue<Unit>(id, Codec.unit(Unit), Unit) {
    override var value: Unit
        get() = Unit
        set(value) {}

    override val type: KType
        get() = Unit::class.createType()
    override val shouldBeSerialized = false
    override fun resetToDefault() {}
}