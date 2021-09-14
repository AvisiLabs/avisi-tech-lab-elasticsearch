# Avisi Tech Lab Elasticsearch annotations

## Bouwen en starten

Alle vereiste stappen voor het bouwen en starten van de applicatie zijn samengevoegd in een Makefile. Deze kan gebruikt worden met `make app`.
Dit voert de volgende stappen uit:
- Bouwt de applicatie met Gradle `./gradlew :clean :build :bootJar --no-build-cache` 
- Maakt een docker container met `docker build --no-cache -t avisilabs/beer-searcher:latest .` 
- Start de docker compose stack met `docker compose up -d --force-recreate`

In het docker compose bestand zijn MongoDB, Elasticsearch en onze bier zoeker beschreven.  
Tijdens het opstarten van de stack wordt maximaal een minuut gewacht tot Elasticsearch bereikbaar is voordat de bier zoeker gestart wordt. 
Hierdoor kan het even duren voordat de applicatie te gebruiken is.

## Gebruik

De applicatie biedt een aantal endpoints aan waarmee objecten geïndexeerd en gezocht kunnen worden:

- `POST localhost:8080/api/index/beer`  
   Via dit endpoint kan bier geïndexeerd worden. Hiervoor moet de volgende structuur meegestuurd worden:
   ```
   {
       "name": "Amstel",
       "style": "Pilsener",
       "ingredients": [
           "hop"
       ],
       "abv": 4.1
   }
   ```   
- `POST localhost:8080/api/index/wine`
   Via dit endpoint kan wijn geïndexeerd worden. Hiervoor moet de volgende structuur meegestuurd worden:
   ```
   {
       "name": "La Pauline 'Voluptueuse' Cabernet-Merlot",
       "style": "red",
       "region": "France, Pays d'Oc",
       "grape": [
           "Cabernet Sauvignon, Merlot"
       ],
       "abv": 13.0
   }
   ```
- `GET localhost:8080/api/search`
   Via dit endpoint kan gezocht worden in de geïndexeerde dranken.
   Aan dit verzoek kunnen meerdere query parameters meegegeven worden om de zoekresultaten te beïnvloeden.
   - `query`: De tekst waarop gezocht moet worden. Dit mogen meerdere woorden zijn gescheiden met spaties.
   - `drinkType`: Het soort drank waarop gezocht moet worden. Mogelijke waardes zijn `beer` en `wine`.
   - `limit`: Het maximaal aantal resultaten dat teruggeven moet worden.  
   
   Een verzoek naar dit endpoint kan dus als volgt zijn:  
   `GET localhost:8080/api/search?drinkType=beer&query=hertog jan` 

