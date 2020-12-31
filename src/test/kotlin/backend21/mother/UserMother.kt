package backend21.mother

import backend21.application.user.UserDTO
import backend21.domain.user.User

class UserMother {

    fun testAdminInstance(): User {
        val user = User.fromDTO(UserDTO("admi", "admin@admin.com", "asfdafsaf", "luis", "heredia"))
        user.id = "1"
        return user
    }
}