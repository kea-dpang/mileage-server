package kea.dpang.mileage.entity

import com.fasterxml.jackson.annotation.JsonCreator

enum class ChargeRequestStatus {
    REQUESTED,
    APPROVED,
    REJECTED;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): ChargeRequestStatus {
            return ChargeRequestStatus.valueOf(value.uppercase())
        }
    }
}
