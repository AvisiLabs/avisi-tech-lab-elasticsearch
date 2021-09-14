package nl.avisi.labs.techlabelasticsearch.configuration.database

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import nl.avisi.labs.techlabelasticsearch.models.Beer
import nl.avisi.labs.techlabelasticsearch.models.Wine
import org.litote.kmongo.getCollectionOfName
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoCollections {
    companion object {
        private const val BEER_COLLECTION = "beer"
        private const val WINE_COLLECTION = "wine"
    }

    @Bean
    fun beerCollection(database: MongoDatabase): MongoCollection<Beer> =
        database.getCollectionOfName(BEER_COLLECTION)

    @Bean
    fun wineCollection(database: MongoDatabase): MongoCollection<Wine> =
        database.getCollectionOfName(WINE_COLLECTION)
}
