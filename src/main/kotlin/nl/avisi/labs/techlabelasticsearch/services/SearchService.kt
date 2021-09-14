package nl.avisi.labs.techlabelasticsearch.services

import mu.KLogging
import nl.avisi.labs.techlabelasticsearch.exceptions.InvalidFilterException
import nl.avisi.labs.techlabelasticsearch.exceptions.NotFoundException
import nl.avisi.labs.techlabelasticsearch.exceptions.SearchRequestException
import nl.avisi.labs.techlabelasticsearch.exceptions.SearchResponseException
import nl.avisi.labs.techlabelasticsearch.models.SearchResponse
import nl.avisi.labs.techlabelasticsearch.models.SearchResult
import nl.avisi.labs.techlabelasticsearch.models.SearchableType
import nl.avisi.labs.techlabelasticsearch.repositories.SearchRepository
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val searchRepository: SearchRepository,
    private val beerService: BeerService,
    private val wineService: WineService
) {

    companion object : KLogging()

    /**
     * Queries the search engine using the given query and returns a list of [SearchResponse]
     *
     * @param query the string used to query the search engine
     * @param searchableTypes the types that the [SearchResponse]s should be limited to
     * @param limit the maximum amount of [SearchResponse]s that should be returned. The actual returned amount can be lower
     * @throws IllegalArgumentException if ElasticSearch responded with a 400: Bad Request
     * @throws SearchException if ElasticSearch responded with an unexpected response or something went wrong in communication
     * @return list containing [SearchResponse] objects
     */
    fun search(query: String?, searchableTypes: List<String>?, limit: Int?): List<SearchResponse> {
        val searchableTypesList = searchableTypes?.map { SearchableType.valueOf(it.trim().uppercase()) }
        val searchResults = try {
            searchRepository.searchWithFilters(
                query,
                searchableTypesList,
                limit
            )
        } catch (exception: Exception) {
            when (exception) {
                is IllegalArgumentException -> {
                    logger.warn(exception) { "Error occurred in query due to invalid arguments" }
                    throw SearchRequestException("The query contained invalid arguments")
                }
                is InvalidFilterException -> {
                    logger.warn(exception) { "Error occurred in query due to invalid filter datatype" }
                    throw SearchRequestException("The query contained a filter with an invalid datatype")
                }
                else -> {
                    logger.error(exception) { "Error querying search engine" }
                    throw SearchResponseException("An unexpected error occurred while querying the search engine")
                }
            }
        }
        return searchResults.mapNotNull { it.toSearchResponseOrNull() }
    }

    fun SearchResult.toSearchResponseOrNull(): SearchResponse? =
        try {
            SearchResponse(
                this.score,
                when (this.type) {
                    SearchableType.BEER -> {
                        beerService.getBeerById(this.id)
                    }
                    SearchableType.WINE -> {
                        wineService.getWineById(this.id)
                    }
                }
            )
        } catch (exception: NotFoundException) {
            null
        }
}
