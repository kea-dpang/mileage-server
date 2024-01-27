package kea.dpang.mileage.dto

import kea.dpang.mileage.entity.ChargeRequest
import kea.dpang.mileage.entity.ChargeRequestStatus
import java.time.LocalDateTime

data class ChargeRequestDTO(
    var userId: Long,
    var status: ChargeRequestStatus,
    var requestDate: LocalDateTime,
    var depositorName: String,
    var requestedMileage: Int,
    var chargeRequestId: Long
) {
    companion object {
        fun fromEntity(chargeRequest: ChargeRequest): ChargeRequestDTO {
            return ChargeRequestDTO(
                userId = chargeRequest.userId,
                status = chargeRequest.status,
                requestDate = chargeRequest.requestDate,
                depositorName = chargeRequest.depositorName,
                requestedMileage = chargeRequest.requestedMileage,
                chargeRequestId = chargeRequest.id!!
            )
        }
    }
}
