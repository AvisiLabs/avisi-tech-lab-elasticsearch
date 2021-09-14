package nl.avisi.labs.techlabelasticsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("nl.avisi.labs.techlabelasticsearch")
class TechlabElasticsearchApplication

fun main(args: Array<String>) {
    runApplication<TechlabElasticsearchApplication>(*args)
}
