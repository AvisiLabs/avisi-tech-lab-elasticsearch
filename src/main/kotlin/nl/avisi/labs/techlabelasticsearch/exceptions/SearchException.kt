package nl.avisi.labs.techlabelasticsearch.exceptions

sealed class SearchException(override val message: String) : Exception(message)

class SearchRequestException(override val message: String) : SearchException(message)

class SearchResponseException(override val message: String) : SearchException(message)
