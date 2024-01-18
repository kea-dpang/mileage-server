package kea.dpang.mileage.controller

import jakarta.ws.rs.core.Response
import kea.dpang.mileage.dto.*

/**
 * 마일리지 관련 작업을 처리하는 컨트롤러 인터페이스.
 */
interface MileageController {

    /**
     * 마일리지 생성 요청을 처리합니다.
     *
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun createMileage(): Response

    /**
     * 사용자의 마일리지를 조회합니다.
     *
     * @param userId 마일리지를 조회할 사용자의 ID.
     * @return 조회 결과를 포함하는 Response 객체.
     */
    fun getMileage(userId: Long): Response

    /**
     * 마일리지를 삭제하는 요청을 처리합니다.
     *
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun deleteMileage(): Response

    /**
     * 마일리지 소비 요청을 처리합니다.
     *
     * @param request 마일리지 소비 요청 정보를 담은 DTO.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun consumeMileage(request: ConsumeMileageRequestDTO): Response

    /**
     * 마일리지 환불 요청을 처리합니다.
     *
     * @param request 마일리지 환불 요청 정보를 담은 DTO.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun refundMileage(request: RefundRequestDTO): Response

    /**
     * 마일리지 충전 요청을 처리합니다.
     *
     * @param request 마일리지 충전 요청 정보를 담은 DTO.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun requestMileageRecharge(request: MileageRechargeRequestDTO): Response

    /**
     * 마일리지 충전 요청을 처리합니다.
     *
     * @param request 마일리지 충전 승인 요청 정보를 담은 DTO.
     * @return 처리 결과를 포함하는 Response 객체.
     */
    fun processMileageRechargeRequest(request: MileageRechargeApprovalDTO): Response
}
