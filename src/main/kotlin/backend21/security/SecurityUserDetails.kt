package backend21.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import backend21.domain.user.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.ArrayList

class SecurityUserDetails(val user: User): UserDetails {

    override fun isEnabled(): Boolean = true

    override fun getUsername(): String = user.username!!

    override fun isCredentialsNonExpired(): Boolean = true

    override fun getPassword(): String = user.password!!

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked() = true

    fun hasSamePassword(password: String) = user.hasSamePassword(password)

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val grantedAuthorities = ArrayList<GrantedAuthority>()
        val grantedAuthority = SimpleGrantedAuthority("USER")
        grantedAuthorities.add(grantedAuthority)
        return grantedAuthorities
    }

}
