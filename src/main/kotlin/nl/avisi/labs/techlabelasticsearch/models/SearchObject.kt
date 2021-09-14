package nl.avisi.labs.techlabelasticsearch.models

data class SearchObject(
    val id: String,
    var type: SearchableType,
    var exactTerms: List<String> = emptyList(),
    var highPriorityTerms: List<String> = emptyList(),
    var midPriorityTerms: List<String> = emptyList(),
    var lowPriorityTerms: List<String> = emptyList()
)
