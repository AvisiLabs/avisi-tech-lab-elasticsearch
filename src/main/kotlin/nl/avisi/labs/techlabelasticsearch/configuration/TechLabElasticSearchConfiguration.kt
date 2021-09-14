package nl.avisi.labs.techlabelasticsearch.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class TechLabElasticSearchConfiguration {
    @Bean
    fun objectMapper(): ObjectMapper = Jackson2ObjectMapperBuilder().build<ObjectMapper>().registerModule(KotlinModule())
}
