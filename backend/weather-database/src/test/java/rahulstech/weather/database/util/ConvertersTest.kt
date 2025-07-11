package rahulstech.weather.database.util

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalTime

class ConvertersTest {

    @Test
    fun testFromLocalTime() {
        val time = LocalTime.of(8,0)
        val expected = "08:00"
        val actual = Converters().fromLocalTime(time)
        assertEquals(expected, actual)
    }
}