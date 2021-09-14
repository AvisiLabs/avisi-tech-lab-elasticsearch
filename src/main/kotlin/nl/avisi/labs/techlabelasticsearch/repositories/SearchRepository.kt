package nl.avisi.labs.techlabelasticsearch.repositories

import nl.avisi.labs.techlabelasticsearch.annotations.SearchField
import nl.avisi.labs.techlabelasticsearch.configuration.search.SearchConfiguration
import nl.avisi.labs.techlabelasticsearch.exceptions.InvalidFilterException
import nl.avisi.labs.techlabelasticsearch.models.SearchResult
import nl.avisi.labs.techlabelasticsearch.models.SearchableType
import org.apache.http.HttpStatus
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.stereotype.Repository
import java.io.IOException

@Repository
class SearchRepository(
    private val elasticClient: RestHighLevelClient,
    private val searchConfiguration: SearchConfiguration
) {

    companion object {
        const val LARGE_BOOST = 65f
        const val MID_BOOST = 45f
        const val SMALL_BOOST = 15f

        const val EXACT_BOOST_OFFSET = 5
        const val HIGH_BOOST_OFFSET = 0
        const val MID_BOOST_OFFSET = -5
        const val LOW_BOOST_OFFSET = -10
    }

    /**
     * Constructs a search query using the given string and sends this to ElasticSearch using the [RestHighLevelClient]
     *
     * @param phrase the string used to query ElasticSearch
     * @return a list of SearchResult objects. Returns an empty list if no results were found
     * @throws RuntimeException if ElasticSearch returned an unexpected response
     * @throws IllegalArgumentException if ElasticSearch responded with a 400: Bad Request
     * @throws IOException if something went wrong while communicating with ElasticSearch
     */
    fun search(phrase: String): List<SearchResult> {
        return searchWithFilters(phrase, emptyList(), null)
    }

    /**
     * Construct a search query using the given string and filters
     * and sends this to ElasticSearch using the [RestHighLevelClient]
     *
     * @param phrase the string used to query ElasticSearch
     * @param searchableTypes the [SearchableType] that the resulting objects are limited to as List
     * @param limit the maximum amount of [SearchResponse]s that should be returned. The actual returned amount can be lower
     * @return a list of SearchResult. Returns an empty list if no results were found
     * @throws RuntimeException if ElasticSearch returned an unexpected response
     * @throws IllegalArgumentException if ElasticSearch responded with a 400: Bad Request
     * @throws InvalidFilterException if a filter is not of the correct datatype
     * @throws IOException if something went wrong while communicating with ElasticSearch
     */
    fun searchWithFilters(
        phrase: String?,
        searchableTypes: List<SearchableType>?,
        limit: Int?
    ): List<SearchResult> {

        val query = BoolQueryBuilder()
            .mustIfNotNull(getQuery(phrase))
            .mustIfNotNull(createFilterQuery(searchableTypes, SearchField.TYPE.esField, false))

        return executeSearch(query, limit)
    }

    private fun executeSearch(boolQuery: BoolQueryBuilder, limit: Int? = null): List<SearchResult> =
        elasticClient.search(getSearchRequest(boolQuery, limit), RequestOptions.DEFAULT).let { searchResponse ->
            handleErrors(searchResponse)
            searchResponse.hits.hits.map {
                SearchResult(
                    it.id,
                    SearchableType.valueOf(it.sourceAsMap["type"] as String),
                    it.score
                )
            }
        }

    private fun getSearchRequest(query: QueryBuilder, limit: Int? = null): SearchRequest =
        SearchRequest().also { searchRequest ->
            searchRequest.indices(searchConfiguration.index).source(SearchSourceBuilder().also {
                it.query(query).from(0).size(limit ?: searchConfiguration.resultCount)
            })
        }

    /**
     * Creates a filter using a BoolQuery by requiring a value in [filters] to be in the provided [field]
     * If [matchAll] is true all values in [filters] must be in the provided [field]
     */
    private fun createFilterQuery(filters: List<*>?, field: String, matchAll: Boolean): BoolQueryBuilder? {
        return filters?.filterNotNull()?.fold(BoolQueryBuilder()) { boolQueryBuilder, filter ->
            when (filter) {
                is Enum<*> -> boolQueryBuilder.mustOrShould(filter.name, field, matchAll)
                is String -> boolQueryBuilder.mustOrShould(filter, field, matchAll)
                else -> throw InvalidFilterException("Filter with datatype ${filter::class} is not allowed on $field")
            }
        }
    }


    /**
     * Creates a query by splitting the phrase at each space and adding a 3-part query for subphrase.
     * The first part searches for matches on the entire subphrase. The resulting match scores of this part get a big boost, because matches on an entire word are more important than partial matches.
     * The second part searches for a partial match of the subphrase starting from the beginning of the subphrase. The resulting match scores of this part get a medium boost.
     * The last part searches for a partial match of the subphrase on any part. The resulting match scores of this part get a small boost.
     */
    private fun getQuery(phrase: String?) =
        if (phrase == null) null else QueryBuilders.boolQuery().also { query ->
            phrase.trim()
                .split(" ")
                .filter { it.length > 1 }
                .map { it.lowercase() }
                .forEach { searchTerms ->
                    query.must(
                        BoolQueryBuilder()
                            .addFieldQueries(
                                searchTerms, "",
                                LARGE_BOOST
                            ) // Match on entire word, high score boost
                            .addFieldQueries(
                                searchTerms, ".edge_ngram",
                                MID_BOOST
                            ) // Partial match from the beginning, middle score boost
                            .addFieldQueries(
                                searchTerms, ".ngram",
                                SMALL_BOOST
                            ) // Partial match, low score boost
                    )
                }
        }

    /**
     * Adds a 4-part query on different fields for a term. Each field gets boosted by the provided boost value. Depending on the field this boost also gets an offset.
     * The first part matches on the exactTerms field and uses a termQuery.
     * The second part matches on the highPriorityTerms field and uses a fuzzyQuery.
     * The third part matches on the midPriorityTerms field and uses a fuzzyQiery.
     * The last part matches on the lowPriorityTerms field and also uses a fuzzyQuery.
     */
    private fun BoolQueryBuilder.addFieldQueries(term: String, fieldSuffix: String, boost: Float) = this
        .should(QueryBuilders.termQuery("${SearchField.EXACT.esField}$fieldSuffix", term).boost(boost + EXACT_BOOST_OFFSET))
        .should(QueryBuilders.fuzzyQuery("${SearchField.HIGH.esField}$fieldSuffix", term).fuzziness(Fuzziness.ONE).transpositions(true).boost(boost + HIGH_BOOST_OFFSET))
        .should(QueryBuilders.fuzzyQuery("${SearchField.MID.esField}$fieldSuffix", term).fuzziness(Fuzziness.ONE).transpositions(true).boost(boost + MID_BOOST_OFFSET))
        .should(QueryBuilders.fuzzyQuery("${SearchField.LOW.esField}$fieldSuffix", term).fuzziness(Fuzziness.ONE).transpositions(true).boost(boost + LOW_BOOST_OFFSET))


    private fun BoolQueryBuilder.mustIfNotNull(query: BoolQueryBuilder?): BoolQueryBuilder =
        query?.let { this.must(it) } ?: this

    private fun BoolQueryBuilder.mustOrShould(filter: String, field: String, matchAll: Boolean): BoolQueryBuilder =
        if (matchAll) {
            this.must(QueryBuilders.matchQuery(field, filter))
        } else {
            this.should(QueryBuilders.matchQuery(field, filter))
        }

    private fun handleErrors(searchResponse: SearchResponse) = when (searchResponse.status().status) {
        HttpStatus.SC_BAD_REQUEST -> throw IllegalArgumentException()
        HttpStatus.SC_NOT_FOUND -> Unit
        in HttpStatus.SC_OK..299 -> Unit
        else -> throw RuntimeException("Invalid response from Elasticsearch: $this")
    }

}
