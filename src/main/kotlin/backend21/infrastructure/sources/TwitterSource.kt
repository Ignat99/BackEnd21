package backend21.infrastructure.sources

import backend21.domain.project.Project
import backend21.wrappers.CurlWrapper
import backend21.wrappers.EnvironmentVarWrapper
import backend21.wrappers.VariableVarWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TwitterSource @Autowired constructor(private val curlWrapper: CurlWrapper,
    private val environmentVarWrapper: VariableVarWrapper): NetworkSource
{
    override fun start(sources: List<String>, keywords: List<String>): String {
        if (keywords.isEmpty()) throw NetworkSourceException("Keywords cannot be empty")
        if (!sources.contains(Project.TWITTER)) throw
            NetworkSourceException("Sources must contain Twitter")
        var transformedKeywords = arrayListOf<String>()
        keywords.forEach { transformedKeywords.add("\"$it\"") }
        val jsonArray = "{\"keywords\": [${transformedKeywords
            .joinToString(",")}], \"users\":[], \"locations\":[]}"
        val host = environmentVarWrapper.getEnvironmentVar("TWITTER2RABBIT_HOST")
        val port = environmentVarWrapper.getEnvironmentVar("TWITTER2RABBIT_PORT")
        return curlWrapper.doPostWithJson("http://$host:$port/keywords", jsonArray)
    }

}
