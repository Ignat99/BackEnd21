package backend21.wrappers

import org.apache.catalina.util.URLEncoder
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.StringUtils
import org.json.simple.JSONArray
import org.json.simple.parser.JSONParser
import org.springframework.stereotype.Component
import java.net.URLDecoder
import java.nio.charset.Charset

@Component
class GoogleTranslateWrapper {
    companion object {
        val API_KEY = "AIzaSyDWpDx3Ria7kzzPSaAshEw-QmL-uANlYIc"
    }
    fun translate(text: String, orig: String, dest: String): String {
        val encodedText = URLEncoder().encode(text, Charset.forName("UTF-8"))
        val curlWrapper = CurlWrapper(EnvironmentVarWrapper())
        val result = curlWrapper.doGet(
            "https://translate.googleapis.com/translate_a/single?key=AIzaSyDWpDx3Ria7kzzPSaAshEw-QmL-uANlYIc&client=gtx&ie=UTF-8&oe=UTF-8&sl=$orig&tl=$dest&dt=t&q=$encodedText")

        val arrayResult: JSONArray = JSONParser().parse(result) as JSONArray

        val secondArray: JSONArray = arrayResult[0] as JSONArray
        val translatedText:String = secondArray.map { (it as JSONArray)[0] as String }.joinToString("\n")
        return translatedText
    }
}
