package nl.avisi.labs.techlabelasticsearch.services

import mu.KLogging
import nl.avisi.labs.techlabelasticsearch.annotations.SearchField
import nl.avisi.labs.techlabelasticsearch.annotations.Searchable
import nl.avisi.labs.techlabelasticsearch.exceptions.IngestException
import nl.avisi.labs.techlabelasticsearch.models.Drink
import nl.avisi.labs.techlabelasticsearch.models.SearchObject
import nl.avisi.labs.techlabelasticsearch.models.SearchableType
import nl.avisi.labs.techlabelasticsearch.repositories.IndexRepository
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

private typealias PropertyToSearchable = Pair<KProperty1<out Drink, Any?>, Searchable>

@Service
class IndexService(private val indexRepository: IndexRepository) {

    companion object : KLogging()

    private val classSearchAnnotations = ConcurrentHashMap<
            KClass<out Drink>,
            List<PropertyToSearchable>>()

    /**
     * Indexes a Drink and the object's content into ElasticSearch
     * making it searchable.
     *
     * @param drinkObject the Drink that should be made searchable in ElasticSearch
     */
    fun save(drinkObject: Drink) {
        val searchObject = drinkObject.toSearchObject()
        try {
            indexRepository.save(searchObject)
            logger.info { "Ingested record with id ${drinkObject.id} in search engine" }
        } catch (exception: IOException) {
            logger.error(exception) { "Failed to ingest record with id ${drinkObject.id} in search engine" }
            throw IngestException("Failed to ingest id ${drinkObject.id}")
        }
    }

    fun Drink.toSearchObject(): SearchObject =
        SearchObject(id = this.id!!, type = this.type).also {
            fillSearchObjectUsingAnnotations(it, this)
        }

    /**
     * Reads the [Searchable] annotation on fields of [Drink].
     * Caches the annotations for the Drink subclass to prevent unnecessary reflection.
     * Puts the content of the annotated properties in the correct [SearchObject] property using [SearchField]
     */
    private fun fillSearchObjectUsingAnnotations(searchObject: SearchObject, drink: Drink): SearchObject {
        classSearchAnnotations.getOrPut(drink::class) { getPropertyAnnotationsForClass(drink::class) }
            .forEach { (property, annotation) ->
                val propertyValue = property.getter.call(drink)!!
                when (annotation.field) {
                    SearchField.TYPE -> searchObject.type = propertyValue as SearchableType
                    SearchField.EXACT -> searchObject.exactTerms += convertFieldToStringList(propertyValue)
                    SearchField.HIGH -> searchObject.highPriorityTerms += convertFieldToStringList(propertyValue)
                    SearchField.MID -> searchObject.midPriorityTerms += convertFieldToStringList(propertyValue)
                    SearchField.LOW -> searchObject.lowPriorityTerms += convertFieldToStringList(propertyValue)
                }
            }

        return searchObject
    }

    private fun getPropertyAnnotationsForClass(drinkObjectClass: KClass<out Drink>): List<PropertyToSearchable> =
        drinkObjectClass.memberProperties
            .filter { property ->
                property.getter.annotations.any { it.annotationClass == Searchable::class }
            }
            .map { property ->
                val annotation: Searchable = property.getter.annotations.first { it.annotationClass == Searchable::class } as Searchable
                property to annotation
            }

    private fun convertFieldToStringList(field: Any): List<String> =
        when (field) {
            is String -> field.split(" ").map { it.trim() }
            is List<*> -> field.flatMap { convertFieldToStringList(it!!) }
            is Long, Int, Double, Float -> listOf(field.toString())
            else -> throw NotImplementedError("Conversion of ${field.javaClass} to List<String> is not supported")
        }
}
