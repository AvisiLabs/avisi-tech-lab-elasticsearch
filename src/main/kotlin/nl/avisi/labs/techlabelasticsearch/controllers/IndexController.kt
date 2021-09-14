package nl.avisi.labs.techlabelasticsearch.controllers

import nl.avisi.labs.techlabelasticsearch.models.Beer
import nl.avisi.labs.techlabelasticsearch.models.Wine
import nl.avisi.labs.techlabelasticsearch.services.BeerService
import nl.avisi.labs.techlabelasticsearch.services.WineService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/index")
class IndexController(
    private val beerService: BeerService,
    private val wineService: WineService
) {

    @PostMapping("/beer")
    fun addBeer(@RequestBody beer: Beer) {
        beerService.addBeer(beer)
    }

    @PostMapping("/wine")
    fun addWine(@RequestBody wine: Wine) {
        wineService.addWine(wine)
    }
}
