package backend21.domain.stopwords

interface StopWords {
    fun isStopWord(stopWord: String): Boolean
}
