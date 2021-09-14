package nl.avisi.labs.techlabelasticsearch.repositories

import nl.avisi.labs.techlabelasticsearch.models.SearchObject
import org.elasticsearch.action.DocWriteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.litote.kmongo.json
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class IndexRepository(
    @Value("\${beer-searcher.elasticsearch.index}") private val elasticIndex: String,
    private val elasticClient: RestHighLevelClient
) {

    /**
     * Saves the given nl.avisi.labs.techlabelasticsearch.models.SearchObject to the ElasticSearch index
     *
     * @param searchObject the nl.avisi.labs.techlabelasticsearch.models.SearchObject that should be indexed
     * @throws IOException if the response from ElasticSearch could not be parsed
     */
    fun save(searchObject: SearchObject) {
        val jsonObject = searchObject.json
        val indexRequest = IndexRequest(elasticIndex).apply {
            id(searchObject.id)
            source(jsonObject.toByteArray(), XContentType.JSON)
            opType(DocWriteRequest.OpType.CREATE)
        }

        elasticClient.index(indexRequest, RequestOptions.DEFAULT)
    }
}
