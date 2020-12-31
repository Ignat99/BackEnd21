package backend21.application.login

import backend21.application.user.UserDTO

data class CredentialsDTO(val user: UserDTO, val login: OAuthCredentialsDTO)
