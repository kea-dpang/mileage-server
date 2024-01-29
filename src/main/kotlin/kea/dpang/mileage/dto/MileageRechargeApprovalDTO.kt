package kea.dpang.mileage.dto

import jakarta.validation.constraints.NotNull

/**
 * 마일리지 충전 승인 요청에 대한 데이터를 담는 DTO.
 *
 * @property approve 충전 승인 여부.
 */
data class MileageRechargeApprovalDTO(
    @NotNull(message = "승인 여부는 비어있을 수 없습니다.")
    val approve: Boolean = false
)
