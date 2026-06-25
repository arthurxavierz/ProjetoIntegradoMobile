package com.example.gestaoacademica2.data.db

import androidx.room.TypeConverter
import com.example.gestaoacademica2.data.model.EventType

class Converters {
    @TypeConverter fun fromType(v: EventType): String = v.key
    @TypeConverter fun toType(v: String): EventType   = EventType.fromKey(v)
}
