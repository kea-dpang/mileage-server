package kea.dpang.mileage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class MileageServerApplication

fun main(args: Array<String>) {
	runApplication<MileageServerApplication>(*args)
}
