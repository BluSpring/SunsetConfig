import org.junit.jupiter.api.assertDoesNotThrow
import xyz.bluspring.sunset.SunsetConfig
import xyz.bluspring.sunset.serializer.JsonSerializer
import xyz.bluspring.sunset.serializer.JsonWithCommentsSerializer
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
                    .comment("Comments should not be getting written")

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

    @Test
    fun testJsonComments() {
        val configClass = ConfigClass()

        val path = Path("test_comments.jsonc")
        val config = SunsetConfig.create(path, JsonWithCommentsSerializer()) {
            category("test") {
                comment("Comment for category")

                string("string_created", configClass::stringCreated)
                    .comment("Comments should be getting written now")

                category("we_can_do_nested_right") {
                    integer("god_i_hope_so", 0, 8, configClass::secondIntWithRange)
                        .comment("Nested comment cuz why not")
                }
            }
        }

        config.save()
        assertTrue(path.exists())

        assertDoesNotThrow {
            config.load()
        }
    }
}