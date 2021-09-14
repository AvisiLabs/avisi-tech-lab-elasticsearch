package nl.avisi.labs.techlabelasticsearch.annotations

@Target(AnnotationTarget.PROPERTY_GETTER)
annotation class Searchable(val field: SearchField)

enum class SearchField(val esField: String) {
    TYPE("type"),
    EXACT("exactTerms"),
    HIGH("highPriorityTerms"),
    MID("midPriorityTerms"),
    LOW("lowPriorityTerms")
}
