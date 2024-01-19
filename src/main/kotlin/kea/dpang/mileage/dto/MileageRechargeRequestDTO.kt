package kea.dpang.mileage.dto

import java.time.LocalDate

/**
 * 마일리지 충전 요청에 대한 데이터를 담는 DTO.
 *
 * @property userId 마일리지 충전을 요청하는 사용자의 ID.
 * @property amount 충전할 마일리지의 양.
 * @property depositor 입금자명.
 * @property joinDate 가입일.
 */
data class MileageRechargeRequestDTO(val userId: Long, val amount: Int, val depositor: String, val joinDate: LocalDate)
