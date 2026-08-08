package xyz.bluspring.sunset.codec

import com.mojang.serialization.*
import xyz.bluspring.sunset.values.ConfigValue
import java.util.stream.Stream

class ConfigCategoryMapCodec(values: List<ConfigValue<*>>) : MapCodec<List<ConfigValue<*>>>() {
    override fun <T : Any?> keys(ops: DynamicOps<T?>?): Stream<T?>? {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> decode(
        ops: DynamicOps<T?>?,
        input: MapLike<T?>?
    ): DataResult<List<ConfigValue<*>>?>? {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> encode(
        input: List<ConfigValue<*>>?,
        ops: DynamicOps<T?>?,
        prefix: RecordBuilder<T?>?
    ): RecordBuilder<T?>? {
        TODO("Not yet implemented")
    }
}
