package com.example.gestaoacademica2.data.db

import androidx.room.TypeConverter
import com.example.gestaoacademica2.data.model.EventCategory
import com.example.gestaoacademica2.data.model.Priority

class Converters {
    @TypeConverter fun fromCategory(v: EventCategory): String = v.name
    @TypeConverter fun toCategory(v: String): EventCategory   = EventCategory.valueOf(v)
    @TypeConverter fun fromPriority(v: Priority): String      = v.name
    @TypeConverter fun toPriority(v: String): Priority        = Priority.valueOf(v)
}
