package kea.dpang.mileage.dto

import kea.dpang.mileage.entity.ChargeRequest
import kea.dpang.mileage.entity.ChargeRequestStatus
import kea.dpang.mileage.feign.dto.UserDto
import java.time.LocalDate
import java.time.LocalDateTime

data class ChargeRequestDetailDTO(
    var userId: Long,
    var status: ChargeRequestStatus,
    var requestDate: LocalDateTime,
    var depositorName: String,
    var requestedMileage: Int,
    var chargeRequestId: Long,
    val employeeNumber: Long,
    val name: String,
    val joinDate: LocalDate
) {
    constructor(chargeRequest: ChargeRequest, userDto: UserDto) : this(
        userId = chargeRequest.userId,
        status = chargeRequest.status,
        requestDate = chargeRequest.requestDate,
        depositorName = chargeRequest.depositorName,
        requestedMileage = chargeRequest.requestedMileage,
        chargeRequestId = chargeRequest.id!!,
        employeeNumber = userDto.employeeNumber,
        name = userDto.name,
        joinDate = userDto.joinDate
    )
}
