# Helpotus Arkeen

Helpotus Arkeen on Spring Boot -harjoitustyö, joka yhdistää henkilökohtaisen talouden seurannan ja ruokasuunnittelun.

## Toteutetut vaatimukset

- Spring Boot Web MVC
- Thymeleaf HTML5 -näkymä
- JPA + H2-tietokanta
- Spring Security ja kirjautuminen
- Bean Validation lomakkeissa
- REST-rajapinnat JSON-muodossa
- Laajempi JPA-käyttö: useita tauluja, useita relaatioita ja `findBy...`-metodeja
- Testaus: Spring Boot + MockMvc
- I18n: `messages.properties` ja `messages_fi.properties`

## Tietokantamalli

Sovelluksessa on useita toisiinsa liittyviä tauluja:

- `app_users`
- `category`
- `expense`
- `budget`
- `ingredient`
- `recipe`
- `recipe_ingredient`
- `shopping_list`
- `shopping_list_item`
- `wish_item`

Esimerkkirelaatioita:

- yhdellä käyttäjällä voi olla monta kulua, budjettia, ostoslistaa ja toivelistan tuotetta
- kategoriassa voi olla monta kulua ja budjettia
- reseptillä voi olla monta ainesosaa ja ainesosa voi kuulua moneen reseptiin

## Julkinen ja suojattu puoli

- Vierailija voi avata etusivun ja julkiset reseptit
- Hallintaliittymä vaatii kirjautumisen

## Testitunnukset

- `mira / user123`
- `admin / admin123`

## REST-endpointit

- `GET /api/recipes`
- `GET /api/expenses`
- `POST /api/expenses`
- `DELETE /api/expenses/{id}`

## Tietokanta-asetukset

Sovellus lukee tietokanta-asetukset ympäristömuuttujista, joten tunnuksia ei tarvitse kovakoodata projektin `application.properties`-tiedostoon.

Käytössä olevat muuttujat:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Jos ympäristömuuttujia ei ole asetettu, sovellus käyttää paikallista H2-tiedostotietokantaa oletusarvoilla.

## Huomio testauksesta

Projektiin lisättiin MockMvc-testit julkiselle etusivulle, suojatulle dashboardille ja resepti-API:lle.
