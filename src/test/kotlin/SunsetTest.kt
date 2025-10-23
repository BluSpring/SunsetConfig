import xyz.bluspring.sunset.SunsetConfig
import xyz.bluspring.sunset.serializer.JsonSerializer
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class SunsetTest {
    class ConfigClass {
        var stringCreated = "hello"
        var secondIntWithRange = 4
    }

    @Test
    fun createSaveAndReloadNewConfig() {
        val configClass = ConfigClass()

        val path = Path("test.json")
        val config = SunsetConfig.create(path, JsonSerializer()) {
            category("test") {
                string("string_created", configClass::stringCreated)

                category("we_can_do_nested_right") {
                    integer("god_i_hope_so", 0, 8, configClass::secondIntWithRange)
                }
            }
        }

        configClass.stringCreated = "hi"
        configClass.secondIntWithRange = 6
        config.save()
        assertTrue(path.exists())

        config.load()

        assertEquals("hi", configClass.stringCreated)
        assertEquals(6, configClass.secondIntWithRange)

        assertFails {
            configClass.secondIntWithRange = 12
            config.save()
        }
    }
}