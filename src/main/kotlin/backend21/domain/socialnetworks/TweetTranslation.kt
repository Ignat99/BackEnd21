package backend21.domain.socialnetworks

import backend21.application.project.TweetTranslationDTO

data class TweetTranslation(val sourceLang: String, val text: String) {
    fun toDTO(): TweetTranslationDTO = TweetTranslationDTO(this.sourceLang, this.text)
}
