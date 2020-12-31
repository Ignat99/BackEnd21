package backend21.application.login

// Fix access_token name
data class OAuthCredentialsDTO(
    val access_token: String,
    val tokenType: String,
    val refreshToken: String,
    val expiresIn: Long,
    val scope: String)
