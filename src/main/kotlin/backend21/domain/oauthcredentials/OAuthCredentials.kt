package backend21.domain.oauthcredentials

import backend21.application.login.OAuthCredentialsDTO
import org.json.simple.JSONObject
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager

data class OAuthCredentials(val access_token: String, val tokenType: String,
    val refreshToken: String, val expiresIn: Long, val scope: String)
{
    companion object {
//        private val LOG = LoggerFactory.getLogger(OAuthCredentials::class.java)
        private val LOG = LogManager.getLogger(OAuthCredentials::class.java)
        fun fromJSON(jsonResult: JSONObject?): OAuthCredentials = OAuthCredentials(
            jsonResult?.get("access_token") as String, jsonResult.get("token_type") as String,
            jsonResult.get("refresh_token") as String, jsonResult.get("expires_in") as Long,
            jsonResult.get("scope") as String)
    }

    fun toDTO(): OAuthCredentialsDTO = OAuthCredentialsDTO(this.access_token, this.tokenType,
        this.refreshToken, this.expiresIn, this.scope)

}
