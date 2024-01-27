package kea.dpang.mileage.controller

import kea.dpang.mileage.base.BaseResponse
import kea.dpang.mileage.base.SuccessResponse
import kea.dpang.mileage.dto.*
import kea.dpang.mileage.entity.ChargeRequestStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

/**
 * 마일리지 관련 작업을 처리하는 컨트롤러 인터페이스.
 */
interface MileageController {

    /**
     * 마일리지 생성 요청을 처리합니다.
     * 해당 요청은 회원가입 과정에서만 사용됩니다.
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param userId 마일리지를 생성할 사용자의 ID.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun createMileage(clientId: Long, userId: Long): ResponseEntity<SuccessResponse<MileageDTO>>

    /**
     * 사용자의 마일리지를 조회합니다.
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param userId 마일리지를 조회할 사용자의 ID.
     * @return 조회 결과를 포함하는 Response 객체.
     */
    fun getMileage(clientId: Long, userId: Long): ResponseEntity<SuccessResponse<MileageDTO>>

    /**
     * 마일리지를 삭제하는 요청을 처리합니다.
     * 해당 요청은 사용자를 탈퇴하는 과정에서만 사용됩니다.
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param userId 마일리지를 삭제할 사용자의 ID.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun deleteMileage(clientId: Long, userId: Long): ResponseEntity<BaseResponse>

    /**
     * 마일리지 소비 요청을 처리합니다.
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param request 마일리지 소비 요청 정보를 담은 DTO.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun consumeMileage(clientId: Long, request: ConsumeMileageRequestDTO): ResponseEntity<BaseResponse>

    /**
     * 마일리지 환불 요청을 처리합니다.
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param request 마일리지 환불 요청 정보를 담은 DTO.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun refundMileage(clientId: Long, request: RefundRequestDTO): ResponseEntity<BaseResponse>

    /**
     * 마일리지 충전을 요청합니다.
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param request 마일리지 충전 요청 정보를 담은 DTO.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun requestMileageRecharge(
        clientId: Long,
        request: MileageRechargeRequestDTO
    ): ResponseEntity<SuccessResponse<ChargeRequestDTO>>

    /**
     * 마일리지 충전 요청 정보를 조회합니다.
     *
     * @param clientId 클라이언트 ID, 클라이언트의 식별자로서 API 요청에 포함된 'X-DPANG-CLIENT-ID' 헤더 값
     * @param userId 사용자 아이디
     * @param status 충전 요청 상태
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param depositorName 예금주 이름
     * @param sortOption 정렬 옵션
     * @param pageable 페이지 정보
     * @return 조회된 충전 요청 목록을 포함하는 응답
     */
    fun getRechargeMileageRequests(
        clientId: Long,
        userId: Long?,
        status: ChargeRequestStatus?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        depositorName: String?,
        sortOption: SortOption,
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<ChargeRequestDTO>>>

    /**
     * 마일리지 충전 요청을 처리합니다.
     *
     * @param requestId 처리할 마일리지 충전 요청의 ID.
     * @param request 마일리지 충전 요청의 승인 정보를 담은 DTO.
     * @return 처리된 마일리지 충전 요청 정보.
     */
    fun processMileageRechargeRequest(
        requestId: Long,
        request: MileageRechargeApprovalDTO
    ): ResponseEntity<SuccessResponse<ChargeRequestDTO>>

    /**
     * 연간 1일마다 모든 회원에게 100만 마일리지를 충전합니다.
     *
     * @return 마일리지 충전 결과
     */
    fun chargeAnnualMileage(): ResponseEntity<BaseResponse>

    /**
     * 분기별 (1월, 4월, 7월, 10월) 1일에 모든 회원에게 근속년수 기반으로 마일리지를 충전합니다.
     *
     * @return 마일리지 충전 결과
     */
    fun chargeQuarterlyMileageBasedOnTenure(): ResponseEntity<BaseResponse>

}
