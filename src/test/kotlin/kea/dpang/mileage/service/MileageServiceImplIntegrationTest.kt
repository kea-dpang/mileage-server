package kea.dpang.mileage.service

import jakarta.transaction.Transactional
import kea.dpang.mileage.dto.MileageRechargeApprovalDTO
import kea.dpang.mileage.dto.MileageRechargeRequestDTO
import kea.dpang.mileage.entity.ChargeRequestStatus
import kea.dpang.mileage.entity.Mileage
import kea.dpang.mileage.repository.ChargeRequestRepository
import kea.dpang.mileage.repository.MileageRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@SpringBootTest
@ExtendWith(SpringExtension::class)
class MileageServiceImplIntegrationTest {

    @Autowired
    lateinit var mileageService: MileageService

    @Autowired
    lateinit var mileageRepository: MileageRepository

    @Autowired
    lateinit var chargeRequestRepository: ChargeRequestRepository

    @BeforeEach
    fun setUp() {
        mileageRepository.deleteAll()
        chargeRequestRepository.deleteAll()
    }

    @Test
    @Transactional
    @DisplayName("마일리지 생성, 조회, 삭제 테스트")
    fun testCreateGetDeleteMileage() {
        // 마일리지를 생성합니다.
        val createdMileage = mileageService.createMileage(1)
        assertEquals(Mileage(1, 0, 0, LocalDate.now()), createdMileage)

        // 마일리지를 조회합니다.
        val gottenMileage = mileageService.getMileage(1)
        assertEquals(createdMileage, gottenMileage)

        // 마일리지를 삭제합니다.
        mileageService.deleteMileage(1)

        // 마일리지가 정상적으로 삭제되었는지 확인합니다.
        assertThrows<Exception> {
            mileageService.getMileage(1)
        }
    }

    @Test
    @DisplayName("마일리지 충전 요청 및 승인 테스트")
    fun testRequestAndApprovalMileageRecharge() {
        // 마일리지를 생성합니다.
        val createdMileage = mileageService.createMileage(1)
        assertEquals(Mileage(1, 0, 0, LocalDate.now()), createdMileage)

        // 마일리지 충전을 요청합니다.
        val rechargeRequestDTO = MileageRechargeRequestDTO(1, 10000, "홍길동")
        val requestedMileage = mileageService.requestMileageRecharge(rechargeRequestDTO)

        // 마일리지 충전 요청을 승인합니다.
        val approvalDTO = MileageRechargeApprovalDTO(true)
        val approvedMileage =
            mileageService.processMileageRechargeRequest(requestedMileage.id!!, approvalDTO)
        assertEquals(ChargeRequestStatus.APPROVED, approvedMileage.status)

        // 사용자의 마일리지가 올바르게 증가했는지 확인합니다.
        val userMileage = mileageService.getMileage(1)
        assertEquals(10000, userMileage.personalChargedMileage)
    }

    @Test
    @DisplayName("잘못된 사용자 ID로 마일리지 충전 요청을 시도하는 경우")
    fun testRequestMileageRechargeWithInvalidUserId() {
        val rechargeRequestDTO = MileageRechargeRequestDTO(-1, 10000, "홍길동")
        assertThrows<Exception> {
            mileageService.requestMileageRecharge(rechargeRequestDTO)
        }
    }

    @Test
    @DisplayName("마일리지 충전 요청이 거부되는 경우")
    fun testRequestRejectionMileageRecharge() {
        // 마일리지를 생성합니다.
        val createdMileage = mileageService.createMileage(1)
        assertEquals(Mileage(1, 0, 0, LocalDate.now()), createdMileage)

        // 마일리지 충전을 요청합니다.
        val rechargeRequestDTO = MileageRechargeRequestDTO(1, 10000, "홍길동")
        val requestedMileage = mileageService.requestMileageRecharge(rechargeRequestDTO)

        // 마일리지 충전 요청을 거부합니다.
        val rejectionDTO = MileageRechargeApprovalDTO(false)
        val rejectedMileage =
            mileageService.processMileageRechargeRequest(requestedMileage.id!!, rejectionDTO)
        assertEquals(ChargeRequestStatus.REJECTED, rejectedMileage.status)
    }

    @Test
    @DisplayName("이미 승인된 충전 요청에 대해 다시 승인을 시도하는 경우")
    fun testRequestApprovalForAlreadyApprovedMileageRecharge() {
        // 마일리지를 생성합니다.
        val createdMileage = mileageService.createMileage(1)
        assertEquals(Mileage(1, 0, 0, LocalDate.now()), createdMileage)

        // 마일리지 충전을 요청합니다.
        val rechargeRequestDTO = MileageRechargeRequestDTO(1, 10000, "홍길동")
        var requestedMileage = mileageService.requestMileageRecharge(rechargeRequestDTO)

        // 마일리지 충전 요청을 승인합니다.
        val approvalDTO = MileageRechargeApprovalDTO(true)
        requestedMileage = mileageService.processMileageRechargeRequest(requestedMileage.id!!, approvalDTO)
        assertEquals(ChargeRequestStatus.APPROVED, requestedMileage.status)

        // 이미 승인된 충전 요청에 대해 다시 승인을 시도합니다.
        val repeatedApprovalMileage =
            mileageService.processMileageRechargeRequest(requestedMileage.id!!, approvalDTO)
        assertEquals(ChargeRequestStatus.APPROVED, repeatedApprovalMileage.status)

        // 사용자의 마일리지가 올바르게 증가했는지 확인합니다.
        val userMileage = mileageService.getMileage(1)
        assertEquals(10000, userMileage.personalChargedMileage)
    }


}
