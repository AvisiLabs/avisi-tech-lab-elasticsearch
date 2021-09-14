package nl.avisi.labs.techlabelasticsearch.models

import nl.avisi.labs.techlabelasticsearch.annotations.SearchField.*
import nl.avisi.labs.techlabelasticsearch.annotations.Searchable
import org.bson.codecs.pojo.annotations.BsonId

data class Beer(
    @field:BsonId override val id: String? = null,
    @get:Searchable(TYPE) override val type: SearchableType = SearchableType.BEER,
    @get:Searchable(HIGH) override val name: String,
    @get:Searchable(MID) val style: String,
    @get:Searchable(LOW) val ingredients: List<String>,
    val abv: Double
) : Drink(
    id = id,
    name = name,
    type = type
)
