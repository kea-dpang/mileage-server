package kea.dpang.mileage.service

import com.querydsl.core.BooleanBuilder
import jakarta.transaction.Transactional
import kea.dpang.mileage.dto.*
import kea.dpang.mileage.dto.SortOption.*
import kea.dpang.mileage.entity.ChargeRequest
import kea.dpang.mileage.entity.ChargeRequestStatus
import kea.dpang.mileage.entity.ChargeRequestStatus.APPROVED
import kea.dpang.mileage.entity.ChargeRequestStatus.REJECTED
import kea.dpang.mileage.entity.Mileage
import kea.dpang.mileage.entity.QChargeRequest
import kea.dpang.mileage.exception.ChargeRequestNotFoundException
import kea.dpang.mileage.exception.InsufficientMileageException
import kea.dpang.mileage.exception.UserMileageNotFoundException
import kea.dpang.mileage.repository.ChargeRequestRepository
import kea.dpang.mileage.repository.MileageRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

/**
 * 마일리지 서비스 구현체.
 */
@Service
@Transactional
class MileageServiceImpl(
    private val mileageRepository: MileageRepository,
    private val chargeRequestRepository: ChargeRequestRepository
) : MileageService {

    private val logger = LoggerFactory.getLogger(MileageServiceImpl::class.java)

    override fun createMileage(userId: Long): Mileage {
        val newMileage = Mileage(
            userId = userId,
            mileage = 0,
            personalChargedMileage = 0,
            joinDate = LocalDate.now() // 회원가입 호출 시 해당 메소드가 호출되므로, 현재 날짜를 저장한다.
        )

        logger.info("새로운 마일리지 생성: $newMileage")
        return mileageRepository.save(newMileage)
    }

    override fun getMileage(userId: Long): Mileage {
        return mileageRepository.findById(userId).orElseThrow {
            UserMileageNotFoundException(userId)
        }.also {
            logger.info("마일리지 조회: $it")
        }
    }

    override fun deleteMileage(userId: Long) {
        if (mileageRepository.existsById(userId)) {
            mileageRepository.deleteById(userId)
            logger.info("사용자 ${userId}의 마일리지 삭제")

        } else {
            throw UserMileageNotFoundException(userId)
        }
    }

    override fun consumeMileage(request: ConsumeMileageRequestDTO) {
        // 사용자의 마일리지 정보를 조회한다.
        val myMileageInfo = mileageRepository.findById(request.userId).orElseThrow {
            UserMileageNotFoundException(request.userId)
        }

        // 사용자의 총 마일리지를 계산한다.
        val totalMileage = myMileageInfo.mileage + myMileageInfo.personalChargedMileage

        // 소비하려는 마일리지가 사용자의 총 마일리지보다 많을 경우 예외를 발생시킨다.
        if (request.amount > totalMileage) {
            throw InsufficientMileageException(request.userId)
        }

        // 연간 충전 마일리지를 먼저 사용한다.
        val remaining = myMileageInfo.mileage - request.amount

        // 연간 충전 마일리지가 소진되면 별도로 충전한 마일리지를 사용한다.
        if (remaining < 0) {
            myMileageInfo.mileage = 0
            myMileageInfo.personalChargedMileage += remaining

        } else {
            myMileageInfo.mileage = remaining
        }

        // 변경된 마일리지 정보를 로깅한다.
        logger.info("사용자(${request.userId})의 마일리지 소비: ${request.amount}, 잔여 마일리지: ${myMileageInfo.mileage + myMileageInfo.personalChargedMileage}")
    }

    override fun refundMileage(request: RefundRequestDTO) {
        // 사용자의 마일리지 정보를 가져온다.
        val mileage = mileageRepository.findById(request.userId)
            .orElseThrow { UserMileageNotFoundException(request.userId) }

        // 사용자의 마일리지에 환불할 마일리지를 더한다.
        mileage.mileage += request.amount
    }

    override fun requestMileageRecharge(request: MileageRechargeRequestDTO): ChargeRequest {
        // 마일리지 충전 요청 객체를 생성합니다.
        val chargeRequest = ChargeRequest(
            userId = request.userId,
            requestedMileage = request.amount,
            depositorName = request.depositor,
            requestDate = LocalDateTime.now(),
            status = ChargeRequestStatus.REQUESTED
        )

        // 생성된 마일리지 충전 요청 객체를 저장하고 반환합니다.
        return chargeRequestRepository.save(chargeRequest)
    }

    override fun getRechargeMileageRequests(
        userId: Long?,
        status: ChargeRequestStatus?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        depositorName: String?,
        sortOption: SortOption
    ): List<ChargeRequest> {

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
            builder.and(qChargeRequest.requestDate.between(startDate, endDate))
        } else {
            if (startDate != null) {
                builder.and(qChargeRequest.requestDate.after(startDate))
            }
            if (endDate != null) {
                builder.and(qChargeRequest.requestDate.before(endDate))
            }
        }
        if (depositorName != null) {
            builder.and(qChargeRequest.depositorName.eq(depositorName))
        }

        // 정렬 옵션에 따라 결과를 정렬하여 반환한다.
        return when (sortOption) {
            RECENT -> {
                chargeRequestRepository.findAll(builder, Sort.by(Sort.Direction.DESC, "requestDate")).toList()
            }

            OLDEST -> {
                chargeRequestRepository.findAll(builder, Sort.by(Sort.Direction.ASC, "requestDate")).toList()
            }

            MILEAGE_ASC -> {
                chargeRequestRepository.findAll(builder, Sort.by(Sort.Direction.ASC, "requestedMileage")).toList()
            }

            MILEAGE_DESC -> {
                chargeRequestRepository.findAll(builder, Sort.by(Sort.Direction.DESC, "requestedMileage")).toList()
            }
        }
    }

    override fun processMileageRechargeRequest(id: Long, request: MileageRechargeApprovalDTO): ChargeRequest {
        // 충전 요청을 가져온다.
        val chargeRequest = chargeRequestRepository.findById(id)
            .orElseThrow { ChargeRequestNotFoundException(id) }

        // 승인 상태가 변경된 경우 처리한다.
        if (request.approve != (chargeRequest.status == APPROVED)) {
            // 사용자 마일리지를 조회한다.
            val mileage = mileageRepository.findById(chargeRequest.userId)
                .orElseThrow { UserMileageNotFoundException(chargeRequest.userId) }

            // 승인 상태에 따라 사용자의 마일리지를 충전하거나 차감한다.
            val requestedMileage = chargeRequest.requestedMileage
            mileage.mileage += if (request.approve) requestedMileage else -requestedMileage

            // 충전 요청의 상태를 업데이트한다.
            chargeRequest.status = if (request.approve) APPROVED else REJECTED
        }

        // 변경된 충전 요청을 반환한다.
        return chargeRequest
    }

    override fun chargeAnnualMileage() {
        // 모든 유저의 마일리지 정보를 가져온다.
        val userMileageList = mileageRepository.findAll()

        // 각 유저의 마일리지를 100만 마일리지로 초기화 한다.
        userMileageList.forEach { mileage ->
            mileage.mileage = 1_000_000
            logger.info("사용자(${mileage.userId})에게 연간 마일리지 100만 충전 완료")
        }
    }

    override fun chargeQuarterlyMileageBasedOnTenure() {
        // 모든 유저의 마일리지 정보를 가져온다.
        val userMileageList = mileageRepository.findAll()

        // 현재 날짜를 가져온다.
        val now = LocalDate.now()

        // 각 유저의 근속년수를 계산하고, 그에 따른 마일리지를 충전합니다.
        userMileageList.forEach { mileage ->
            // 근속년수를 계산한다.
            val yearsOfTenure = Period.between(mileage.joinDate, now).years

            // 근속년수에 따른 마일리지를 계산한다.
            val chargeMileage = yearsOfTenure * 10_000

            // 마일리지를 충전한다.
            mileage.mileage += chargeMileage
            logger.info("사용자(${mileage.userId})에게 근속년수 기반 마일리지($chargeMileage) 충전 완료")
        }
    }

}
