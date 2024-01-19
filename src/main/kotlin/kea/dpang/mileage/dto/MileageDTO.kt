package kea.dpang.mileage.dto

import kea.dpang.mileage.entity.Mileage

data class MileageDTO(
    var userId: Long,
    var mileage: Int,
    var personalChargedMileage: Int
) {
    companion object {
        fun fromEntity(mileage: Mileage): MileageDTO {
            return MileageDTO(
                userId = mileage.userId,
                mileage = mileage.mileage,
                personalChargedMileage = mileage.personalChargedMileage
            )
        }
    }
}
