package backend21.application.user



class UserDTO(val username: String, email: String, val password: String, val name: String, val surname: String) {
    val email: String

    init {
        EmailValidator().validate(email)
        this.email = email
    }
}
