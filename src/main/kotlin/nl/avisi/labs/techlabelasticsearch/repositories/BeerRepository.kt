package nl.avisi.labs.techlabelasticsearch.repositories

import com.mongodb.client.MongoCollection
import nl.avisi.labs.techlabelasticsearch.models.Beer
import org.springframework.stereotype.Repository

@Repository
class BeerRepository(collection: MongoCollection<Beer>) : MongoRepositoryBase<Beer>(collection)
