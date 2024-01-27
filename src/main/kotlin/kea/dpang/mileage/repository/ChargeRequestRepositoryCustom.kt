package kea.dpang.mileage.repository

import kea.dpang.mileage.dto.SortOption
import kea.dpang.mileage.entity.ChargeRequest
import kea.dpang.mileage.entity.ChargeRequestStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime


/**
 * 충전 요청에 대한 사용자 정의 repository 인터페이스.
 */
interface ChargeRequestRepositoryCustom {

    /**
     * 사용자의 검색 조건에 따라 충전 요청을 검색하고, 페이지네이션과 정렬을 적용하여 반환한다.
     *
     * @param userId 사용자 ID
     * @param status 충전 요청 상태
     * @param startDate 검색할 기간의 시작 날짜
     * @param endDate 검색할 기간의 종료 날짜
     * @param depositorName 입금자 이름
     * @param sortOption 정렬 옵션
     * @param pageable 페이지네이션 옵션
     * @return 검색 조건에 맞는 충전 요청의 페이지네이션 결과
     */
    fun getRechargeMileageRequests(
        userId: Long?,
        status: ChargeRequestStatus?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        depositorName: String?,
        sortOption: SortOption,
        pageable: Pageable
    ): Page<ChargeRequest>
}

