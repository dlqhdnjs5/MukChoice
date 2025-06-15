package com.project.mukchoice.config

import com.project.mukchoice.interceptor.ContextHoldInterceptor
import com.project.mukchoice.manager.JwtManager
import com.project.mukchoice.service.UserService
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val userService: UserService,
    private val jwtManager: JwtManager,
) : WebMvcConfigurer {
    companion object {
        private const val PATTERN_ALL = "/**"
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ContextHoldInterceptor(userService, jwtManager))
            .addPathPatterns(PATTERN_ALL)
            .excludePathPatterns("/oauth/**")
        super.addInterceptors(registry)
    }
}