package nl.avisi.labs.techlabelasticsearch.configuration.elasticsearch

import nl.avisi.labs.techlabelasticsearch.configuration.search.SearchConfiguration
import org.apache.http.HttpHost
import org.elasticsearch.client.Node
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.sniff.Sniffer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticSearchConfiguration(private val searchConfiguration: SearchConfiguration) {
    @Bean
    fun elasticClient(): RestHighLevelClient =
        RestHighLevelClient(RestClient.builder(Node(HttpHost(searchConfiguration.uri, searchConfiguration.port, searchConfiguration.scheme))))
}
