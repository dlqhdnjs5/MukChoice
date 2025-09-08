package com.project.mukchoice.config

import com.project.mukchoice.interceptor.ContextHoldInterceptor
import com.project.mukchoice.manager.JwtManager
import com.project.mukchoice.service.UserService
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebMvcConfig(
    private val userService: UserService,
    private val jwtManager: JwtManager,
) : WebMvcConfigurer {
    companion object {
        private const val PATTERN_ALL = "/**"
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        // 1단계(/abc)
        registry.addViewController("/{path:^(?!api|oauth|actuator|.*\\..*$).*$}")
            .setViewName("forward:/index.html")
        // 2단계 이상(/abc/def, /kakao-oauth/redirect 등)

    }

    // 정적 리소스 핸들러 (통합 배포용)
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(31536000) // 1년 캐시 (운영환경 최적화)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ContextHoldInterceptor(userService, jwtManager))
            .addPathPatterns(PATTERN_ALL)
            .excludePathPatterns("/oauth/**")
        super.addInterceptors(registry)
    }
}