package backend21.infrastructure.sources

interface NetworkSource {
    fun start(sources: List<String>, keywords: List<String>):String
}
