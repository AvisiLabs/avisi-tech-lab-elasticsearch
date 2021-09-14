package nl.avisi.labs.techlabelasticsearch.controllers

import nl.avisi.labs.techlabelasticsearch.models.SearchResponse
import nl.avisi.labs.techlabelasticsearch.services.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchService: SearchService
) {

    @GetMapping("")
    fun searchDrink(
        @RequestParam(required = false) drinkType: List<String>?,
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) limit: Int?
    ): List<SearchResponse> {
        return searchService.search(query = query, searchableTypes = drinkType, limit = limit)
    }

}

