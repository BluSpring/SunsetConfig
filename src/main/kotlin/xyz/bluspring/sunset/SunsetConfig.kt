package xyz.bluspring.sunset

import com.mojang.serialization.Codec
import xyz.bluspring.sunset.codec.ConfigCategoryMapCodec
import xyz.bluspring.sunset.serializer.JsonSerializer
import xyz.bluspring.sunset.serializer.Serializer
import xyz.bluspring.sunset.values.*
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
        internal val values = mutableListOf<ConfigValue<*>>()
        internal val comments = mutableListOf<String>()
        private var parent: CategoryBuilder? = null

        /**
         * These comments will be visible in the written data if the serializer permits it,
         * but they will not be visible in the UI implementation unless explicitly written to do so,
         * as that information should instead be controlled via internationalization.
         *
         * Comments are not ordered!
         */
        fun comment(text: String) {
            comments.add(text)
        }

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

        fun <T : Number> ranged(id: String, codec: Codec<T>, min: T, max: T, step: T, property: KMutableProperty<T>, owner: Any? = null): RangedConfigValue<T> {
            if (values.any { it.id == id })
                throw IllegalArgumentException("Config value ID $fullId.$id already exists!")

            val configValue = RangedConfigValue(id, codec, property, owner, min, max, step)
            values.add(configValue)

            return configValue
        }

        fun <U, T : ConfigValue<U>> custom(value: T): T {
            if (values.any { it.id == value.id })
                throw IllegalArgumentException("Config value ID $fullId.$id already exists!")

            values.add(value)
            return value
        }

        fun <U, T : WrappedConfigValue<U>> custom(value: T): T {
            if (values.contains(value.wrapped))
                values.remove(value.wrapped)

            if (values.any { it.id == value.id })
                throw IllegalArgumentException("Config value ID $fullId.$id already exists!")

            values.add(value)
            return value
        }

        fun integer(id: String, property: KMutableProperty<Int>, owner: Any? = null) = value(id, Codec.INT, property, owner)
        fun integer(id: String, min: Int, max: Int, property: KMutableProperty<Int>, owner: Any? = null, step: Int = 1) = ranged(id, Codec.intRange(min, max), min, max, step, property, owner)

        fun long(id: String, property: KMutableProperty<Long>, owner: Any? = null) = value(id, Codec.LONG, property, owner)
        fun long(id: String, min: Long, max: Long, property: KMutableProperty<Long>, owner: Any? = null, step: Long = 1) = ranged(id, Codec.checkRange(min, max).run { Codec.LONG.flatXmap(this, this) }, min, max, step, property, owner)

        fun float(id: String, property: KMutableProperty<Float>, owner: Any? = null) = value(id, Codec.FLOAT, property, owner)
        fun float(id: String, min: Float, max: Float, property: KMutableProperty<Float>, owner: Any? = null, step: Float = 1f) = ranged(id, Codec.floatRange(min, max), min, max, step, property, owner)

        fun double(id: String, property: KMutableProperty<Double>, owner: Any? = null) = value(id, Codec.DOUBLE, property, owner)
        fun double(id: String, min: Double, max: Double, property: KMutableProperty<Double>, owner: Any? = null, step: Double = 1.0) = ranged(id, Codec.doubleRange(min, max), min, max, step, property, owner)

        fun string(id: String, property: KMutableProperty<String>, owner: Any? = null) = value(id, Codec.STRING, property, owner)

        internal fun build(): ConfigCategory {
            return ConfigCategory(this.id, ConfigCategoryMapCodec(values).codec(), values).apply {
                for (value in values) {
                    value.parent = this
                }

                for (comment in this@CategoryBuilder.comments) {
                    comment(comment)
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
