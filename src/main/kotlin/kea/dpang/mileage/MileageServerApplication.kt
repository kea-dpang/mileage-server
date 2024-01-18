package kea.dpang.mileage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MileageServerApplication

fun main(args: Array<String>) {
	runApplication<MileageServerApplication>(*args)
}
