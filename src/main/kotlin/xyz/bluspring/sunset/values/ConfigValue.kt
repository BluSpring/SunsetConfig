package xyz.bluspring.sunset.values

import com.mojang.serialization.Codec
import kotlin.reflect.KType

abstract class ConfigValue<T>(val id: String, val codec: Codec<T>, val default: T) {
    open var parent: ConfigCategory? = null
        internal set

    abstract val type: KType

    val comments = mutableListOf<String>()

    fun comment(comment: String) {
        this.comments.add(comment)
    }

    private var cachedFullId: String? = null
    val fullId: String
        get() {
            if (cachedFullId == null) {
                val ids = mutableListOf<String>()
                ids.add(this.id)

                var current: ConfigCategory? = this.parent

                while (current != null) {
                    ids.add(current.id)
                    current = current.parent
                }

                return ids.reversed().joinToString(".").apply {
                    this@ConfigValue.cachedFullId = this
                }
            }

            return this.cachedFullId!!
        }

    open var value: T = default

    open fun resetToDefault() {
        this.value = default
    }

    open val shouldBeSerialized = true
}