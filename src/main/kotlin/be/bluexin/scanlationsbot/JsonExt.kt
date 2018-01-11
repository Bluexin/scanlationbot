package be.bluexin.scanlationsbot

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class DateTimeDeserializer : StdDeserializer<LocalDateTime>(LocalDateTime::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        return try {
            LocalDateTime.parse(p.valueAsString.replace(' ', 'T'))
        } catch (e: DateTimeParseException) {
            LocalDateTime.MIN
        }
    }
}

class BooleanDeserializer: StdDeserializer<Boolean>(Boolean::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Boolean {
        return p.valueAsString == "true" || p.valueAsInt != 0
    }

}