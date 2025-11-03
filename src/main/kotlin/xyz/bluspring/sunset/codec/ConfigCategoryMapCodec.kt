package xyz.bluspring.sunset.codec

import com.mojang.serialization.*
import xyz.bluspring.sunset.values.ConfigValue
import java.util.stream.Stream

// yes, i am very well aware that i'm using codecs incredibly incorrectly.
// it's funny.
class ConfigCategoryMapCodec(private val values: List<ConfigValue<*>>) : MapCodec<List<ConfigValue<*>>>() {
    private val codecs = values.associate { it.id to ((it as ConfigValue<Any?>).codec to it.codec.optionalFieldOf(it.id, it.value)) }

    override fun <T : Any> keys(ops: DynamicOps<T>): Stream<T> {
        /*return Stream.concat(
            Stream.of(ops.createString(category.id)),
            this.category.value.map { ops.createString(it.id) }.stream()
        )*/

        return this.codecs.keys.map { ops.createString(it) }.stream()
    }

    override fun <T : Any> decode(
        ops: DynamicOps<T>,
        input: MapLike<T>
    ): DataResult<List<ConfigValue<*>>> {
        val success = mutableListOf<ConfigValue<Any?>>()
        val errors = mutableListOf<DataResult.PartialResult<Any?>>()

        for ((key, codecs) in this.codecs.entries) {
            val value = this.values.first { it.id == key } as ConfigValue<Any?>

            if (!value.shouldBeSerialized)
                continue

            val parsed = codecs.second.decode(ops, input)
            parsed.error().ifPresent { e ->
                errors.add(e)
            }

            parsed.result().ifPresent { result ->
                val value = value
                value.value = result
                success.add(value)
            }
        }

        return if (errors.isNotEmpty()) {
            DataResult.error({ "Failed to decode config category: ${errors.joinToString(", ") { it.message() }}" }, success, Lifecycle.stable())
        } else {
            DataResult.success(success)
        }
    }

    override fun <T : Any?> encode(
        input: List<ConfigValue<*>>,
        ops: DynamicOps<T>,
        prefix: RecordBuilder<T>
    ): RecordBuilder<T> {
        for (value in input) {
            if (!value.shouldBeSerialized)
                continue

            prefix.add(
                ops.createString(value.id),
                (value as ConfigValue<Any?>).codec.encodeStart(ops, value.value)
            )
        }

        return prefix
    }
}