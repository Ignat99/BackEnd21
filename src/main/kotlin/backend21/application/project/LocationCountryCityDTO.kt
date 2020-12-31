package backend21.application.project

import java.util.Locale



class LocationCountryCityDTO(country: String, val city: String, val posts:Long) {
    val country: String

    init {
        val l = Locale(Locale.ENGLISH.displayLanguage, country)
        this.country = l.getDisplayCountry(l)
    }
    
}

data class ListLocationCountryCityDTO(val data: List<LocationCountryCityDTO>)
