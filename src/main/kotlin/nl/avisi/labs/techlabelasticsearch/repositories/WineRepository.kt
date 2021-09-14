package nl.avisi.labs.techlabelasticsearch.repositories

import com.mongodb.client.MongoCollection
import nl.avisi.labs.techlabelasticsearch.models.Wine
import org.springframework.stereotype.Repository

@Repository
class WineRepository(collection: MongoCollection<Wine>) : MongoRepositoryBase<Wine>(collection)
