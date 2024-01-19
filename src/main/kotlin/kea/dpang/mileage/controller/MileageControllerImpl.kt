package kea.dpang.mileage.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.mileage.base.BaseResponse
import kea.dpang.mileage.base.SuccessResponse
import kea.dpang.mileage.dto.*
import kea.dpang.mileage.entity.ChargeRequestStatus
import kea.dpang.mileage.service.MileageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/mileage")
@Tag(name = "Mileage", description = "마일리지 관련 API를 제공합니다.")
class MileageControllerImpl(private val mileageService: MileageService) : MileageController {

    @PostMapping
    @Operation(summary = "마일리지 생성", description = "마일리지를 생성합니다. 이 API는 회원 가입 시 호출됩니다.")
    override fun createMileage(userId: Long): ResponseEntity<SuccessResponse<MileageDTO>> {
        val mileage = mileageService.createMileage(userId)
        val successResponse = SuccessResponse(201, "마일리지가 생성되었습니다.", MileageDTO.fromEntity(mileage))
        return ResponseEntity(successResponse, HttpStatus.CREATED)
    }

    @GetMapping("/{userId}")
    @Operation(summary = "마일리지 조회", description = "사용자의 마일리지를 조회합니다.")
    override fun getMileage(@PathVariable userId: Long): ResponseEntity<SuccessResponse<MileageDTO>> {
        val mileage = mileageService.getMileage(userId)
        val successResponse = SuccessResponse(200, "마일리지 조회에 성공하였습니다.", MileageDTO.fromEntity(mileage))
        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "마일리지 삭제", description = "사용자의 마일리지를 삭제합니다. 이 API는 회원 탈퇴 시 호출됩니다.")
    override fun deleteMileage(@PathVariable userId: Long): ResponseEntity<BaseResponse> {
        mileageService.deleteMileage(userId)
        val response = BaseResponse(204, "마일리지가 삭제되었습니다.")
        return ResponseEntity(response, HttpStatus.NO_CONTENT)
    }

    @PostMapping("/consume")
    @Operation(summary = "마일리지 사용", description = "마일리지를 사용합니다.")
    override fun consumeMileage(@RequestBody request: ConsumeMileageRequestDTO): ResponseEntity<BaseResponse> {
        mileageService.consumeMileage(request)
        val response = BaseResponse(200, "마일리지 사용에 성공하였습니다.")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/refund")
    @Operation(summary = "마일리지 환불", description = "마일리지를 환불합니다.")
    override fun refundMileage(@RequestBody request: RefundRequestDTO): ResponseEntity<BaseResponse> {
        mileageService.refundMileage(request)
        val response = BaseResponse(200, "마일리지 환불에 성공하였습니다.")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/recharge-request")
    @Operation(summary = "마일리지 충전 요청", description = "마일리지 충전을 요청합니다.")
    override fun requestMileageRecharge(@RequestBody request: MileageRechargeRequestDTO): ResponseEntity<SuccessResponse<ChargeRequestDTO>> {
        val chargeRequest = mileageService.requestMileageRecharge(request)
        val successResponse =
            SuccessResponse(201, "마일리지 충전 요청이 성공적으로 제출되었습니다.", ChargeRequestDTO.fromEntity(chargeRequest))
        return ResponseEntity(successResponse, HttpStatus.CREATED)
    }

    @GetMapping("/recharge-requests")
    @Operation(summary = "마일리지 충전 요청 목록 조회", description = "마일리지 충전 요청 목록을 조회합니다.")
    override fun getRechargeMileageRequests(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: ChargeRequestStatus?,
        @RequestParam(required = false) startDate: LocalDateTime?,
        @RequestParam(required = false) endDate: LocalDateTime?,
        @RequestParam(required = false) depositorName: String?,
        @RequestParam(defaultValue = "RECENT") sortOption: SortOption
    ): ResponseEntity<SuccessResponse<List<ChargeRequestDTO>>> {
        val chargeRequests =
            mileageService.getRechargeMileageRequests(userId, status, startDate, endDate, depositorName, sortOption)

        val successResponse =
            SuccessResponse(200, "충전 요청 정보 조회에 성공하였습니다.", chargeRequests.map { ChargeRequestDTO.fromEntity(it) })
        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @PostMapping("/recharge-requests/{id}/process")
    @Operation(summary = "마일리지 충전 요청 처리", description = "마일리지 충전 요청을 처리합니다.")
    override fun processMileageRechargeRequest(
        @PathVariable id: Long,
        @RequestBody request: MileageRechargeApprovalDTO
    ): ResponseEntity<SuccessResponse<ChargeRequestDTO>> {
        val chargeRequest = mileageService.processMileageRechargeRequest(id, request)
        val successResponse =
            SuccessResponse(200, "마일리지 충전 요청 처리에 성공하였습니다.", ChargeRequestDTO.fromEntity(chargeRequest))
        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @PostMapping("/annual-mileage-charge")
    @Operation(summary = "연간 마일리지 충전", description = "연간 마일리지를 충전합니다.")
    override fun chargeAnnualMileage(): ResponseEntity<BaseResponse> {
        mileageService.chargeAnnualMileage()
        val response = BaseResponse(200, "연간 마일리지 충전이 성공적으로 처리되었습니다.")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/quarterly-mileage-charge")
    @Operation(summary = "분기별 근속년수에 따른 마일리지 충전", description = "분기별 근속년수에 따른 마일리지를 충전합니다.")
    override fun chargeQuarterlyMileageBasedOnTenure(): ResponseEntity<BaseResponse> {
        mileageService.chargeQuarterlyMileageBasedOnTenure()
        val response = BaseResponse(200, "분기별 근속년수 기반 마일리지 충전이 성공적으로 처리되었습니다.")
        return ResponseEntity(response, HttpStatus.OK)
    }

}
