package magicsquare.quartumbackend.security.jwt

import magicsquare.quartumbackend.security.services.UserDetailsServiceImpl
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.util.StringUtils

/**
 * Auth token filter - фильтрует входящий токен
 *
 * @property jwtUtils
 * @property userDetailsService
 * @constructor Создаёт пустой фильтр токенов аутентификации
 */
@Component
class AuthTokenFilter (
    private val jwtUtils: JwtUtils,
    private val userDetailsService: UserDetailsServiceImpl
 ) : OncePerRequestFilter() {

    val logger = KotlinLogging.logger {}

    /**
     * Метод фильтрует фходящий токен. Проверяет на наличие правильных креденшиалсов в нём
     *
     * @param request
     * @param response
     * @param filterChain
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt: String? = parseJwt(request)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                val username = jwtUtils.getUserNameFromJwtToken(jwt)
                val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication: {}", e)
        }
        filterChain.doFilter(request, response)
    }

    /**
     * Метод осуществляет парсинг JWT токена
     *
     * @param request
     * @return true - если токен не пустой и начинается с "Bearer ", иначе null
     */
    private fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth = request.getHeader("Authorization")
        return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            headerAuth.substring(7, headerAuth.length)
        } else null
    }
}
