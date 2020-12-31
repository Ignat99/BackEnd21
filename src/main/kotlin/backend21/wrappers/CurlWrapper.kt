package backend21.wrappers

import backend21.domain.oauthcredentials.OAuthCredentialsService
import backend21.wrappers.VariableVarWrapper
import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class CurlWrapper @Autowired constructor(private val environmentVarWrapper: VariableVarWrapper) {

    fun doRequest(params: ArrayList<NameValuePair>): String? {
        params.add(BasicNameValuePair("scope", "read write"))
        params.add(BasicNameValuePair("client_secret", "secret"))
        params.add(BasicNameValuePair("client-id", "client_id"))
        val url = "http://client_id:secret@localhost:8080/oauth/token"
        return doPost(url, params)
    }

    fun doPost(url: String, params: ArrayList<NameValuePair>): String {
        return sdoPost(url) {
            UrlEncodedFormEntity(params, "UTF-8")
        }
    }


    fun doPostWithJson(url: String, data:String): String {
        return sdoPostWithJson(url) {
            StringEntity(data, "UTF-8")


        }
    }


    fun doPost(url: String, data: String): String {
        return sdoPost(url) {
            StringEntity(data, "UTF-8")
        }
    }


    private fun sdoPost(url: String, callback: () -> HttpEntity): String {
        val post = HttpPost(url)
        //val post = HttpPost("http://client_id:secret@192.168.99.1:9090/oauth/token")
        post.addHeader("Accept", "application/json")
        //post.addHeader("Content-Type", "application/json;charset=UTF-8")
        return executePost(post, callback)
    }

    private fun executePost(post: HttpPost, callback: () -> HttpEntity): String {
        try {
            post.entity = callback()
            //Execute and get the response.
            post.allHeaders.map { LOG.info(it.name + " " + it.value)}
            val result = executeConnection(post)
            LOG.info(result)
            return result
        } catch (e: Exception) {
            throw CurlException(e.message!!)
        }
    }


    private fun sdoPostWithJson(url: String, callback: () -> HttpEntity): String {
        val post = HttpPost(url)
        post.addHeader("Accept", "application/json")
        post.addHeader("Content-Type", "application/json;charset=UTF-8")
        return executePost(post, callback)
    }


    private fun executeConnection(post: HttpRequestBase): String {
        val httpclient = HttpClients.createDefault()
        val response: HttpResponse = httpclient.execute(post)
        val entity = response.entity
        val instream = entity?.content
        return IOUtils.toString(instream, StandardCharsets.UTF_8.name())
    }

    fun doGet(url: String): String {
        val get = HttpGet(url)
        get.addHeader("Accept", "application/json")
        return executeConnection(get)
    }


    companion object {
//        private val LOG = LoggerFactory.getLogger(CurlWrapper::class.java)
        private val LOG = LogManager.getLogger(CurlWrapper::class.java)
    }
}
