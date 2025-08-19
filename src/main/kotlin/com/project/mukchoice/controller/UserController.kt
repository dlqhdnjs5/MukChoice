package com.project.mukchoice.controller

import com.project.mukchoice.model.user.UserDto
import com.project.mukchoice.util.ContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController {

    @GetMapping("/me")
    fun getCurrentUser(): UserDto {
        return ContextHolder.getUserInfoWithCheck()
    }
}