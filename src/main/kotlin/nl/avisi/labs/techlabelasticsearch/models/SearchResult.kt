package nl.avisi.labs.techlabelasticsearch.models

data class SearchResult(
    val id: String,
    val type: SearchableType,
    val score: Float
)
