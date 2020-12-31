package backend21.config

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletResponse
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SimpleCorsFilter : Filter {

    @Throws(IOException::class, ServletException::class)
    @Override
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val response: javax.servlet.http.HttpServletResponse = res as javax.servlet.http.HttpServletResponse
        val request = req as HttpServletRequest
        response.setHeader("Access-Control-Allow-Origin",
                "http://" + System.getenv("FRONTEND_HOST") + ":" + System.getenv("FRONTEND_PORT"))
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, content-type")
        response.setHeader("Access-Control-Allow-Credentials", "true")

        if ("OPTIONS".equals(request.method, ignoreCase = true)) {
            response.status = HttpServletResponse.SC_OK
        } else {
            chain.doFilter(req, res)
        }
    }

    @Override
    override fun init(filterConfig: FilterConfig) {
    }

    @Override
    override fun destroy() {
    }
}
