package kea.dpang.mileage.dto

import com.fasterxml.jackson.annotation.JsonCreator

enum class SortOption {
    RECENT, // 최신 순 정렬.
    OLDEST, // 오래된 순서로 정렬.
    MILEAGE_ASC, // 마일리지를 기준으로 오름차순 정렬.
    MILEAGE_DESC; // 마일리지를 기준으로 내림차순 정렬.

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): SortOption {
            return SortOption.valueOf(value.uppercase())
        }
    }
}
