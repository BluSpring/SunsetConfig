package xyz.bluspring.sunset

import com.mojang.serialization.Codec
import xyz.bluspring.sunset.codec.ConfigCategoryMapCodec
import xyz.bluspring.sunset.serializer.JsonSerializer
import xyz.bluspring.sunset.serializer.Serializer
import xyz.bluspring.sunset.values.ConfigCategory
import xyz.bluspring.sunset.values.ConfigValue
import xyz.bluspring.sunset.values.ReflectingConfigValue
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.reflect.KMutableProperty

class SunsetConfig private constructor(
    path: Path,
    val rootCategory: ConfigCategory,
    val serializer: Serializer<*>
) {
    val path = path.absolute()

    class CategoryBuilder internal constructor(private val id: String) {
        private val values = mutableListOf<ConfigValue<*>>()
        private var parent: CategoryBuilder? = null

        fun category(id: String, builder: CategoryBuilder.() -> Unit) {
            if (id.trim() == "")
                throw IllegalArgumentException("Category ID cannot be blank!")

            val categoryBuilder = CategoryBuilder(id)
            categoryBuilder.parent = this
            builder(categoryBuilder)

            values.add(categoryBuilder.build())
        }

        fun <T> value(id: String, codec: Codec<T>, property: KMutableProperty<T>, owner: Any? = null): ConfigValue<T> {
            if (values.any { it.id == id })
                throw IllegalArgumentException("Config value ID $fullId.$id already exists!")

            val configValue = ReflectingConfigValue(id, codec, property, owner)
            values.add(configValue)

            return configValue
        }

        fun integer(id: String, property: KMutableProperty<Int>, owner: Any? = null) = value(id, Codec.INT, property, owner)
        fun integer(id: String, min: Int, max: Int, property: KMutableProperty<Int>, owner: Any? = null) = value(id, Codec.intRange(min, max), property, owner)

        fun long(id: String, property: KMutableProperty<Long>, owner: Any? = null) = value(id, Codec.LONG, property, owner)
        fun long(id: String, min: Long, max: Long, property: KMutableProperty<Long>, owner: Any? = null) = value(id, Codec.checkRange(min, max).run { Codec.LONG.flatXmap(this, this) }, property, owner)

        fun float(id: String, property: KMutableProperty<Float>, owner: Any? = null) = value(id, Codec.FLOAT, property, owner)
        fun float(id: String, min: Float, max: Float, property: KMutableProperty<Float>, owner: Any? = null) = value(id, Codec.floatRange(min, max), property, owner)

        fun double(id: String, property: KMutableProperty<Double>, owner: Any? = null) = value(id, Codec.DOUBLE, property, owner)
        fun double(id: String, min: Double, max: Double, property: KMutableProperty<Double>, owner: Any? = null) = value(id, Codec.doubleRange(min, max), property, owner)

        fun string(id: String, property: KMutableProperty<String>, owner: Any? = null) = value(id, Codec.STRING, property, owner)

        internal fun build(): ConfigCategory {
            return ConfigCategory(this.id, ConfigCategoryMapCodec(values).codec(), values).apply {
                for (value in values) {
                    value.parent = this
                }
            }
        }

        private val fullId: String
            get() {
                val ids = mutableListOf<String>()
                var current: CategoryBuilder? = this

                while (current != null) {
                    ids.add(current.id)
                    current = current.parent
                }

                return ids.reversed().joinToString(".")
            }
    }

    fun load() {
        if (!this.path.exists())
            return

        val serializer = this.serializer as Serializer<Any?>

        this.rootCategory.codec.decode(serializer.ops, serializer.readAsSerialized(path))
            .map { entry ->
                this.rootCategory.value = entry.first
            }
    }

    fun save() {
        if (!this.path.parent.exists())
            this.path.createParentDirectories()

        val serializer = this.serializer as Serializer<Any?>
        serializer.saveSerialized(this.path, this.rootCategory.codec.encodeStart(serializer.ops, this.rootCategory.value).result().orElseThrow())
    }

    fun <T> getConfigValueById(id: String): ConfigValue<T>? {
        val splitId = id.split(".")
        var currentValue: ConfigValue<Any?> = this.rootCategory as ConfigValue<Any?>

        for ((index, string) in splitId.withIndex()) {
            if (currentValue is ConfigCategory) {
                currentValue = currentValue.getValueById(string) ?: return null
            } else if (index == splitId.size - 1) {
                if (currentValue.fullId != id) // validation check
                    return null

                return currentValue as ConfigValue<T>
            } else {
                return null
            }
        }

        return null
    }

    companion object {
        fun create(path: Path, serializer: Serializer<*> = JsonSerializer(), builder: CategoryBuilder.() -> Unit): SunsetConfig {
            return SunsetConfig(path, CategoryBuilder("")
                .apply(builder)
                .build(),
                serializer
            )
        }
    }
}
