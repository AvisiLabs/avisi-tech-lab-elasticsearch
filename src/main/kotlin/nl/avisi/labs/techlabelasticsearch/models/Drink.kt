package nl.avisi.labs.techlabelasticsearch.models

import org.bson.codecs.pojo.annotations.BsonId

open class Drink(
    @field:BsonId open val id: String?,
    open val type: SearchableType,
    open val name: String
)
