package xyz.bluspring.sunset.values

import com.mojang.serialization.Codec
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class ConfigCategory(id: String, codec: Codec<List<ConfigValue<*>>>, values: List<ConfigValue<*>>) : ConfigValue<List<ConfigValue<*>>>(id, codec, values) {
    override var value: List<ConfigValue<*>>
        get() = super.value
        set(values) {
            val original = super.value

            for (newValue in values) {
                (original.first { it.id == newValue.id } as ConfigValue<Any?>)
                    .value = newValue.value
            }
        }

    override val type: KType
        get() = Unit::class.createType()

    fun <T> getValueById(id: String): ConfigValue<T>? {
        return this.value.firstOrNull { it.id == id } as? ConfigValue<T>
    }

    fun getCategoryById(id: String): ConfigCategory? {
        return this.value.firstOrNull { it.id == id } as? ConfigCategory
    }

    override fun resetToDefault() {
        for (value in this.value) {
            value.resetToDefault()
        }
    }
}