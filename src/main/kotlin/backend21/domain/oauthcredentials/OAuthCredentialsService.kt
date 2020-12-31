package backend21.domain.oauthcredentials

import backend21.domain.user.User
import backend21.wrappers.CurlWrapper
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.ArrayList

@Service
class OAuthCredentialsService @Autowired constructor(private val curlWrapper: CurlWrapper){

    fun getAccessToken(username: String, password: String): OAuthCredentials {
        val params = ArrayList<NameValuePair>(2)
        params.add(BasicNameValuePair("password", password))
        params.add(BasicNameValuePair("username", username))
        params.add(BasicNameValuePair("grant_type", "password"))
        return manageRequest(params)
    }

    fun getAccessTokenByRefreshToken(refreshToken: String): OAuthCredentials {
        val params = ArrayList<NameValuePair>(2)
        params.add(BasicNameValuePair("refresh_token", refreshToken))
        params.add(BasicNameValuePair("grant_type", "refresh_token"))
        return manageRequest(params);
    }

    private fun manageRequest(params: ArrayList<NameValuePair>): OAuthCredentials {
        val result = curlWrapper.doRequest(params)
        val parser = JSONParser()
        val jsonResult: JSONObject = parser.parse(result) as JSONObject;
        if (jsonResult.containsKey("error")) throw CredentialsException(
            jsonResult["error_description"] as String)
        return OAuthCredentials.fromJSON(jsonResult)
    }

    companion object {
//        private val LOG = LoggerFactory.getLogger(OAuthCredentialsService::class.java)
        private val LOG = LogManager.getLogger(OAuthCredentialsService::class.java)
    }
}
