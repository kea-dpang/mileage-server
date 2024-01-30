package kea.dpang.mileage.feign

import kea.dpang.mileage.feign.dto.UserDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "user-server")
interface UserFeignClient {

    @GetMapping("/api/users/{userId}")
    fun getUserInfo(@PathVariable userId: Long): UserDto
}
