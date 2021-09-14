package nl.avisi.labs.techlabelasticsearch.configuration.elasticsearch

import mu.KLogging
import nl.avisi.labs.techlabelasticsearch.configuration.search.SearchConfiguration
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.springframework.context.annotation.Configuration
import java.io.FileNotFoundException
import java.net.URL
import javax.annotation.PostConstruct

@Configuration
class ElasticSearchIndexConfiguration(
    private val searchConfiguration: SearchConfiguration,
    private val elasticClient: RestHighLevelClient
) {
    companion object : KLogging()

    @PostConstruct
    fun createElasticIndexIfNotExists() {
        logger.info { "checking existence of Elastic index" }
        val getIndexRequest = GetIndexRequest(searchConfiguration.index)
        val exists = elasticClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT)
        if (!exists) {
            logger.info { "creating Elastic index" }
            val createIndexRequest = CreateIndexRequest(searchConfiguration.index)
            val mapping = getFile(searchConfiguration.mappingFile)
            val analysis = getFile(searchConfiguration.analysisFile)

            createIndexRequest.source(
                """
                {
                    "mappings": ${mapping.readText()},
                    "settings": {
                        "number_of_shards": ${searchConfiguration.indexShards},
                        "number_of_replicas": ${searchConfiguration.indexReplicas},
                        "max_ngram_diff": ${searchConfiguration.maxNgramDiff},
                        "analysis": ${analysis.readText()}
                    }
                }
            """.trimIndent(), XContentType.JSON
            )

            elasticClient.indices().create(createIndexRequest, RequestOptions.DEFAULT)
        } else {
            logger.info { "Elastic index exists" }
        }
    }

    private fun getFile(fileName: String): URL = this::class.java.classLoader.getResource(fileName)
        ?: throw FileNotFoundException("Elasticsearch index does not exist and required file $fileName was not found")
}
