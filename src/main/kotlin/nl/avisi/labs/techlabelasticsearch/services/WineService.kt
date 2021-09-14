package nl.avisi.labs.techlabelasticsearch.services

import nl.avisi.labs.techlabelasticsearch.exceptions.NotFoundException
import nl.avisi.labs.techlabelasticsearch.models.Wine
import nl.avisi.labs.techlabelasticsearch.repositories.WineRepository
import org.springframework.stereotype.Service

@Service
class WineService(
    private val wineRepository: WineRepository,
    private val indexService: IndexService
) {
    fun addWine(wine: Wine) {
        wineRepository.insertOrUpdate(wine)
        indexService.save(wine)
    }

    fun getWineById(objectId: String): Wine {
        return wineRepository.getById(objectId) ?: throw NotFoundException("Wine with $objectId not found")
    }
}
