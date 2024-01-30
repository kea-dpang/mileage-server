package kea.dpang.mileage.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kea.dpang.mileage.base.BaseResponse
import kea.dpang.mileage.base.SuccessResponse
import kea.dpang.mileage.dto.*
import kea.dpang.mileage.entity.ChargeRequestStatus
import kea.dpang.mileage.service.MileageService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/mileage")
@Tag(name = "Mileage", description = "마일리지 관련 API를 제공합니다.")
class MileageControllerImpl(private val mileageService: MileageService) : MileageController {

    @PostMapping
    @Operation(summary = "마일리지 생성", description = "마일리지를 생성합니다. 이 API는 회원 가입 시 호출됩니다.")
    @PreAuthorize("(hasRole('USER') and #clientId == #userId) or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    override fun createMileage(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "마일리지를 생성할 사용자 ID")
        @RequestParam userId: Long
    ): ResponseEntity<SuccessResponse<MileageDTO>> {

        val mileage = mileageService.createMileage(userId)
        val successResponse = SuccessResponse(201, "마일리지가 생성되었습니다.", MileageDTO.fromEntity(mileage))
        return ResponseEntity(successResponse, HttpStatus.CREATED)
    }

    @GetMapping("/{userId}")
    @Operation(summary = "마일리지 조회", description = "사용자의 마일리지를 조회합니다.")
    @PreAuthorize("(hasRole('USER') and #clientId == #userId) or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    override fun getMileage(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "마일리지 조회할 사용자 ID")
        @PathVariable userId: Long
    ): ResponseEntity<SuccessResponse<MileageDTO>> {

        val mileage = mileageService.getMileage(userId)
        val successResponse = SuccessResponse(200, "마일리지 조회에 성공하였습니다.", MileageDTO.fromEntity(mileage))
        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "마일리지 삭제", description = "사용자의 마일리지를 삭제합니다. 이 API는 회원 탈퇴 시 호출됩니다.")
    @PreAuthorize("(hasRole('USER') and #clientId == #userId) or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    override fun deleteMileage(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "마일리지 삭제할 사용자 ID")
        @PathVariable userId: Long
    ): ResponseEntity<BaseResponse> {

        mileageService.deleteMileage(userId)
        val response = BaseResponse(204, "마일리지가 삭제되었습니다.")
        return ResponseEntity(response, HttpStatus.NO_CONTENT)
    }

    @PostMapping("/consume")
    @Operation(summary = "마일리지 사용", description = "마일리지를 사용합니다.")
    @PreAuthorize("(hasRole('USER') and #clientId == #request.userId)")
    override fun consumeMileage(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "마일리지 사용 요청 정보")
        @RequestBody request: ConsumeMileageRequestDTO
    ): ResponseEntity<BaseResponse> {

        mileageService.consumeMileage(request)
        val response = BaseResponse(200, "마일리지 사용에 성공하였습니다.")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/refund")
    @Operation(summary = "마일리지 환불", description = "마일리지를 환불합니다.")
    @PreAuthorize("(hasRole('USER') and #clientId == #request.userId) or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    override fun refundMileage(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "마일리지 환불 요청 정보")
        @RequestBody request: RefundRequestDTO
    ): ResponseEntity<BaseResponse> {

        mileageService.refundMileage(request)
        val response = BaseResponse(200, "마일리지 환불에 성공하였습니다.")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/recharge-request")
    @Operation(summary = "마일리지 충전 요청", description = "마일리지 충전을 요청합니다.")
    @PreAuthorize("(hasRole('USER') and #clientId == #request.userId)")
    override fun requestMileageRecharge(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "마일리지 충전 요청 정보")
        @RequestBody request: MileageRechargeRequestDTO
    ): ResponseEntity<SuccessResponse<ChargeRequestDTO>> {

        val chargeRequest = mileageService.requestMileageRecharge(request)
        val successResponse =
            SuccessResponse(201, "마일리지 충전 요청이 성공적으로 제출되었습니다.", ChargeRequestDTO.fromEntity(chargeRequest))
        return ResponseEntity(successResponse, HttpStatus.CREATED)
    }

    @GetMapping("/recharge-requests")
    @Operation(summary = "마일리지 충전 요청 목록 조회", description = "마일리지 충전 요청 목록을 조회합니다.")
    @PreAuthorize("(hasRole('USER') and #clientId == #userId) or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    override fun getRechargeMileageRequests(
        @Parameter(hidden = true)
        @RequestHeader("X-DPANG-CLIENT-ID") clientId: Long,
        @Parameter(description = "사용자 ID")
        @RequestParam(required = false) userId: Long?,
        @Parameter(description = "충전 요청 상태", required = false)
        @RequestParam(required = false) status: ChargeRequestStatus?,
        @Parameter(description = "조회 시작 일시")
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate?,
        @Parameter(description = "조회 종료 일시")
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate?,
        @Parameter(description = "입금자 이름")
        @RequestParam(required = false) depositorName: String?,
        @Parameter(description = "정렬 옵션")
        @RequestParam(defaultValue = "RECENT") sortOption: SortOption,
        @Parameter(description = "페이지 정보")
        pageable: Pageable
    ): ResponseEntity<SuccessResponse<Page<ChargeRequestDetailDTO>>> {

        val chargeRequests =
            mileageService.getRechargeMileageRequests(userId, status, startDate, endDate, depositorName, sortOption, pageable)

        val successResponse =
            SuccessResponse(200, "충전 요청 정보 조회에 성공하였습니다.", chargeRequests)
        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @PostMapping("/recharge-requests/{requestId}/process")
    @Operation(summary = "마일리지 충전 요청 처리", description = "마일리지 충전 요청을 처리합니다.")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    override fun processMileageRechargeRequest(
        @Parameter(description = "처리할 마일리지 충전 요청 ID")
        @PathVariable requestId: Long,
        @Parameter(description = "마일리지 충전 요청 처리 정보")
        @RequestBody request: MileageRechargeApprovalDTO
    ): ResponseEntity<SuccessResponse<ChargeRequestDTO>> {

        val chargeRequest = mileageService.processMileageRechargeRequest(requestId, request)
        val successResponse =
            SuccessResponse(200, "마일리지 충전 요청 처리에 성공하였습니다.", ChargeRequestDTO.fromEntity(chargeRequest))
        return ResponseEntity(successResponse, HttpStatus.OK)
    }

    @PostMapping("/annual-mileage-charge")
    @Operation(summary = "연간 마일리지 충전", description = "연간 마일리지를 충전합니다.")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    override fun chargeAnnualMileage(): ResponseEntity<BaseResponse> {

        mileageService.chargeAnnualMileage()
        val response = BaseResponse(200, "연간 마일리지 충전이 성공적으로 처리되었습니다.")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/quarterly-mileage-charge")
    @Operation(summary = "분기별 근속년수에 따른 마일리지 충전", description = "분기별 근속년수에 따른 마일리지를 충전합니다.")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    override fun chargeQuarterlyMileageBasedOnTenure(): ResponseEntity<BaseResponse> {

        mileageService.chargeQuarterlyMileageBasedOnTenure()
        val response = BaseResponse(200, "분기별 근속년수 기반 마일리지 충전이 성공적으로 처리되었습니다.")
        return ResponseEntity(response, HttpStatus.OK)
    }

}
