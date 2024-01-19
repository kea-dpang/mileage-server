package kea.dpang.mileage.dto

/**
 * 마일리지 충전 승인 요청에 대한 데이터를 담는 DTO.
 *
 * @property requestId 마일리지 충전 요청의 ID.
 * @property approve 충전 승인 여부.
 */
data class MileageRechargeApprovalDTO(val requestId: Long, val approve: Boolean)
