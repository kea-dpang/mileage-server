package kea.dpang.mileage.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import kea.dpang.mileage.dto.SortOption
import kea.dpang.mileage.entity.ChargeRequest
import kea.dpang.mileage.entity.ChargeRequestStatus
import kea.dpang.mileage.entity.QChargeRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate


/**
 * 충전 요청에 대한 사용자 정의 repository 구현 클래스.
 */
@Repository
class ChargeRequestRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ChargeRequestRepositoryCustom {

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
    override fun getRechargeMileageRequests(
        userId: Long?,
        status: ChargeRequestStatus?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        depositorName: String?,
        sortOption: SortOption,
        pageable: Pageable
    ): Page<ChargeRequest> {

        val qChargeRequest = QChargeRequest.chargeRequest
        val builder = BooleanBuilder()

        // 검색 조건을 추가한다.
        if (userId != null) {
            builder.and(qChargeRequest.userId.eq(userId))
        }
        if (status != null) {
            builder.and(qChargeRequest.status.eq(status))
        }
        if (startDate != null && endDate != null) {
            builder.and(qChargeRequest.requestDate.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()))
        } else {
            if (startDate != null) {
                builder.and(qChargeRequest.requestDate.after(startDate.atStartOfDay()))
            }
            if (endDate != null) {
                builder.and(qChargeRequest.requestDate.before(endDate.plusDays(1).atStartOfDay()))
            }
        }
        if (depositorName != null) {
            builder.and(qChargeRequest.depositorName.eq(depositorName))
        }

        // 정렬 옵션에 따라 결과를 정렬하여 반환한다.
        val chargeRequests = jpaQueryFactory
            .selectFrom(qChargeRequest)
            .where(builder)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(getSortOption(sortOption))
            .fetch()

        return PageImpl(chargeRequests, pageable, chargeRequests.size.toLong())
    }

    /**
     * 정렬 옵션에 따라 Querydsl의 OrderSpecifier를 반환한다.
     *
     * @param sortOption 정렬 옵션
     * @return 정렬 옵션에 맞는 Querydsl의 OrderSpecifier
     */
    private fun getSortOption(sortOption: SortOption): OrderSpecifier<*> {
        val qChargeRequest = QChargeRequest.chargeRequest

        return when (sortOption) {
            SortOption.RECENT -> qChargeRequest.requestDate.desc()
            SortOption.OLDEST -> qChargeRequest.requestDate.asc()
            SortOption.MILEAGE_ASC -> qChargeRequest.requestedMileage.asc()
            SortOption.MILEAGE_DESC -> qChargeRequest.requestedMileage.desc()
        }
    }
}

