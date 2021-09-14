package nl.avisi.labs.techlabelasticsearch.configuration.search

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "beer-searcher.elasticsearch")
data class SearchConfiguration(
    val uri: String,
    val port: Int,
    val scheme: String,
    val index: String,
    val indexShards: Int,
    val indexReplicas: Int,
    val maxNgramDiff: Int,
    val mappingFile: String,
    val analysisFile: String,
    val resultCount: Int
)
