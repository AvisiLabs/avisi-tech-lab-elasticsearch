package nl.avisi.labs.techlabelasticsearch.configuration.database

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.withKMongo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoConfiguration {
    @Bean
    fun mongoClient(@Value("\${beer-searcher.mongo.uri}") mongoUri: String): MongoClient {
        return KMongo.createClient(ConnectionString(mongoUri))
    }

    @Bean
    fun database(mongoClient: MongoClient, @Value("\${beer-searcher.mongo.database}") databaseName: String): MongoDatabase =
        mongoClient.getDatabase(databaseName).withKMongo()
}
