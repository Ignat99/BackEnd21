package backend21.security

import backend21.application.ApplicationServiceException
import backend21.domain.stopwords.EnStopWords
import java.util.*

class ProjectValidator {
    companion object {
        @Throws(InvalidPropertiesFormatException::class)
        fun validateWords(keywords: List<String>?) {
            val esStopWords: EnStopWords = EnStopWords()
            if (keywords?.isEmpty() == true) throw InvalidPropertiesFormatException(
                "keywords list cannot be empty")

            keywords?.forEach {
                if (esStopWords.isStopWord(it)) throw InvalidPropertiesFormatException(
                    "keyword ${it} is a stop word")
            }
        }

        fun validateType(type: String?) {
            if (type.isNullOrEmpty()) return
            val allowedTypes = arrayListOf<String>("keyword", "dataset")
            if (!allowedTypes.contains(type)) throw InvalidPropertiesFormatException(
                "Invalid project type")
        }
    }
}
