package nl.avisi.labs.techlabelasticsearch.services

import nl.avisi.labs.techlabelasticsearch.exceptions.NotFoundException
import nl.avisi.labs.techlabelasticsearch.models.Beer
import nl.avisi.labs.techlabelasticsearch.repositories.BeerRepository
import org.springframework.stereotype.Service

@Service
class BeerService(
    private val beerRepository: BeerRepository,
    private val indexService: IndexService
    ) {
    fun addBeer(beer: Beer) {
        beerRepository.insertOrUpdate(beer)
        indexService.save(beer)
    }

    fun getBeerById(objectId: String): Beer = beerRepository.getById(objectId) ?: throw NotFoundException("Beer with id: $objectId not found.")
}
