package nl.avisi.labs.techlabelasticsearch.models

data class SearchResponse(
    val score: Float,
    val result: Drink
)
