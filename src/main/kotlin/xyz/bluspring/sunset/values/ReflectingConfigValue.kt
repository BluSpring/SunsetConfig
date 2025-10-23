package xyz.bluspring.sunset.values

import com.mojang.serialization.Codec
import kotlin.reflect.KMutableProperty

class ReflectingConfigValue<T>(id: String, codec: Codec<T>, val property: KMutableProperty<T>, val owner: Any? = null) : ConfigValue<T>(id, codec,
    if (owner != null) property.getter.call(owner) else property.getter.call()
) {
    override var value: T
        get() = if (owner != null) this.property.getter.call(owner) else this.property.getter.call()
        set(value) {
            if (owner != null)
                this.property.setter.call(owner, value)
            else
                this.property.setter.call(value)
        }
}
