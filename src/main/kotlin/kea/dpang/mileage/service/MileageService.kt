package kea.dpang.mileage.service

import kea.dpang.mileage.dto.*
import kea.dpang.mileage.entity.ChargeRequest
import kea.dpang.mileage.entity.Mileage

/**
 * 마일리지 서비스 인터페이스.
 */
interface MileageService {

    /**
     * 사용자의 마일리지를 생성합니다.
     *
     * @param userId 마일리지를 생성할 사용자의 ID.
     * @return 생성된 마일리지.
     */
    fun createMileage(userId: Long): Mileage

    /**
     * 사용자의 마일리지를 조회합니다.
     *
     * @param userId 마일리지를 조회할 사용자의 ID.
     * @return 조회된 마일리지.
     */
    fun getMileage(userId: Long): Mileage

    /**
     * 사용자의 마일리지를 삭제합니다.
     *
     * @param userId 마일리지를 삭제할 사용자의 ID.
     */
    fun deleteMileage(userId: Long)

    /**
     * 마일리지를 소비합니다. 연간 충전 마일리지를 먼저 사용하고, 해당 마일리지가 소진되면 별도로 충전한 마일리지를 사용합니다.
     *
     * @param request 마일리지 소비 요청 정보를 담은 DTO.
     */
    fun consumeMileage(request: ConsumeMileageRequestDTO)

    /**
     * 사용한 마일리지를 환불합니다.
     *
     * @param request 마일리지 환불 요청 정보를 담은 DTO.
     */
    fun refundMileage(request: RefundRequestDTO)

    /**
     * 마일리지 충전을 요청합니다.
     *
     * @param request 마일리지 충전 요청 정보를 담은 DTO.
     * @return 충전 요청 정보를 담은 엔티티.
     */
    fun requestMileageRecharge(request: MileageRechargeRequestDTO): ChargeRequest

    /**
     * 마일리지 충전 요청을 처리합니다.
     *
     * @param request 마일리지 충전 요청 처리 정보를 담은 DTO.
     * @return 충전  정보를 담은 엔티티.
     */
    fun processMileageRechargeRequest(request: MileageRechargeApprovalDTO): ChargeRequest

    /**
     * 모든 회원을 대상으로 연간 1일마다 100만 마일리지를 자동으로 충전합니다.
     */
    fun autoChargeAnnualMileage()

    /**
     * 모든 회원을 대상으로 분기별 (1월, 4월, 7월, 10월)1일에 근속년수 기반으로 마일리지를 자동으로 충전합니다.
     */
    fun autoChargeQuarterlyMileageBasedOnTenure()
}
