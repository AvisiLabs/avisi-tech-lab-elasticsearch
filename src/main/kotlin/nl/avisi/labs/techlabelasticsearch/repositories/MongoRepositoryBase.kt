package nl.avisi.labs.techlabelasticsearch.repositories

import com.mongodb.MongoException
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import nl.avisi.labs.techlabelasticsearch.exceptions.DatabaseException
import org.bson.types.ObjectId
import org.litote.kmongo.findOneById
import org.litote.kmongo.util.KMongoUtil
import org.litote.kmongo.util.KMongoUtil.toBsonModifier
import org.springframework.beans.factory.annotation.Autowired

open class MongoRepositoryBase<T : Any>(protected open val collection: MongoCollection<T>) {
    @Autowired
    lateinit var mongoClient: MongoClient

    fun getById(objectId: String): T? {
        return collection.findOneById(objectId)
    }

    fun insertOrUpdate(entity: T): T {
        try {
            val objectId = getObjectId(entity)
            if (objectId != null) {
                val updated = toBsonModifier(entity)
                val filter = eq("_id", objectId.toHexString())
                collection.updateOne(filter, updated)
            } else {
                collection.insertOne(entity)
            }
        } catch (exception: MongoException) {
            throw DatabaseException(
                exception.message ?: "An error occurred while inserting $entity into MongoDB"
            )
        }

        return entity
    }

    private fun getObjectId(model: Any) = KMongoUtil.getIdValue(model)?.let {
        when (it) {
            is ObjectId -> it
            is String -> ObjectId(it)
            else -> throw IllegalArgumentException("Unable to create ObjectId using ${model.javaClass.simpleName}; _id is $it (${it.javaClass.simpleName})")
        }
    }
}
