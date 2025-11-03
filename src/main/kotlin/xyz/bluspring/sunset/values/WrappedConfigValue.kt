package xyz.bluspring.sunset.values

import kotlin.reflect.KType

abstract class WrappedConfigValue<T>(val wrapped: ConfigValue<T>) : ConfigValue<T>(wrapped.id, wrapped.codec, wrapped.default) {
    override var parent: ConfigCategory?
        get() = wrapped.parent
        set(value) {
            wrapped.parent = value
        }

    override val type: KType
        get() = wrapped.type

    override fun resetToDefault() {
        wrapped.resetToDefault()
    }

    override var value: T
        get() = wrapped.value
        set(value) {
            wrapped.value = value
        }

    override val shouldBeSerialized: Boolean
        get() = wrapped.shouldBeSerialized
}