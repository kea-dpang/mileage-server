package kea.dpang.mileage.service

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kea.dpang.mileage.dto.ConsumeMileageRequestDTO
import kea.dpang.mileage.dto.MileageRechargeApprovalDTO
import kea.dpang.mileage.dto.MileageRechargeRequestDTO
import kea.dpang.mileage.dto.RefundRequestDTO
import kea.dpang.mileage.entity.ChargeRequest
import kea.dpang.mileage.entity.ChargeRequestStatus.*
import kea.dpang.mileage.entity.Mileage
import kea.dpang.mileage.exception.InsufficientMileageException
import kea.dpang.mileage.exception.UserMileageAlreadyExistsException
import kea.dpang.mileage.exception.UserMileageNotFoundException
import kea.dpang.mileage.repository.ChargeRequestRepository
import kea.dpang.mileage.repository.MileageRepository
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.*

class MileageServiceImplUnitTest : BehaviorSpec({

    val mileageRepository = mockk<MileageRepository>()
    val chargeRequestRepository = mockk<ChargeRequestRepository>()
    val mileageService = MileageServiceImpl(mileageRepository, chargeRequestRepository)

    Given("사용자의 마일리지를 생성하려고 할 때") {
        val testUserId = 1L
        val testMileage = Mileage(testUserId, 0, 0, LocalDate.now())
        every { mileageRepository.existsById(testUserId) } returns false
        every { mileageRepository.save(any()) } returns testMileage

        When("마일리지 생성 함수를 호출하면") {
            val result = mileageService.createMileage(testUserId)

            Then("생성된 마일리지 정보가 정상적으로 반환되어야 한다") {
                result shouldBe testMileage
            }

            Then("마일리지 저장 함수가 정확히 한 번 호출되어야 한다") {
                verify(exactly = 1) { mileageRepository.save(testMileage) }
            }
        }

        When("이미 존재하는 사용자라면") {
            every { mileageRepository.existsById(testUserId) } returns true

            Then("UserMileageAlreadyExistsException 예외가 발생해야 한다") {
                shouldThrow<UserMileageAlreadyExistsException> {
                    mileageService.createMileage(testUserId)
                }
            }
        }
    }

    Given("마일리지를 조회하려고 할 때") {
        When("존재하는 유저의 마일리지를 조회하면") {
            val userId = 1L
            val mileage = Mileage(userId, 1000, 500, LocalDate.now())
            every { mileageRepository.findById(userId) } returns Optional.of(mileage)

            Then("마일리지 정보가 정상적으로 반환되어야 한다") {
                val result = mileageService.getMileage(userId)
                result shouldBe mileage
            }
        }

        When("존재하지 않는 유저의 마일리지를 조회하면") {
            val userId = 2L
            every { mileageRepository.findById(userId) } returns Optional.empty()

            Then("UserMileageNotFoundException이 발생해야 한다") {
                shouldThrow<UserMileageNotFoundException> {
                    mileageService.getMileage(userId)
                }
            }
        }
    }

    Given("마일리지를 삭제하려고 할 때") {
        When("존재하는 유저의 마일리지를 삭제하면") {
            val userId = 1L
            every { mileageRepository.existsById(userId) } returns true
            every { mileageRepository.deleteById(userId) } just Runs

            Then("익셉션 없이 실행되어야 한다") {
                shouldNotThrowAny {
                    mileageService.deleteMileage(userId)
                }
            }
        }

        When("존재하지 않는 유저의 마일리지를 삭제하면") {
            val userId = 2L
            every { mileageRepository.existsById(userId) } returns false

            Then("UserMileageNotFoundException이 발생해야 한다") {
                shouldThrow<UserMileageNotFoundException> {
                    mileageService.deleteMileage(userId)
                }
            }
        }
    }

    Given("마일리지를 소비하려고 할 때") {
        val userId = 1L

        When("해당 유저의 마일리지 정보가 존재하지 않으면") {
            every { mileageRepository.findById(userId) } returns Optional.empty()

            Then("UserMileageNotFoundException이 발생해야 한다") {
                shouldThrow<UserMileageNotFoundException> {
                    mileageService.consumeMileage(ConsumeMileageRequestDTO(userId, 500, "test reason"))
                }
            }
        }

        When("요청된 마일리지가 사용자의 총 마일리지보다 많으면") {
            val userMileage = Mileage(userId, 1000, 500, LocalDate.now())
            every { mileageRepository.findById(userId) } returns Optional.of(userMileage)

            Then("InsufficientMileageException이 발생해야 한다") {
                shouldThrow<InsufficientMileageException> {
                    mileageService.consumeMileage(ConsumeMileageRequestDTO(userId, 2000, "test reason"))
                }
            }
        }

        When("연간 충전 마일리지가 요청된 마일리지보다 적으면") {
            val userMileage = Mileage(userId, 1000, 500, LocalDate.now())
            every { mileageRepository.findById(userId) } returns Optional.of(userMileage)

            Then("연간 충전 마일리지가 0이 되고 개인 충전 마일리지가 감소해야 한다") {
                mileageService.consumeMileage(ConsumeMileageRequestDTO(userId, 1200, "test reason"))
                userMileage.mileage shouldBe 0
                userMileage.personalChargedMileage shouldBe 300
            }
        }

        When("연간 충전 마일리지가 요청된 마일리지보다 많으면") {
            val userMileage = Mileage(userId, 1000, 500, LocalDate.now())
            every { mileageRepository.findById(userId) } returns Optional.of(userMileage)

            Then("연간 충전 마일리지만 감소해야 한다") {
                mileageService.consumeMileage(ConsumeMileageRequestDTO(userId, 800, "test reason"))
                userMileage.mileage shouldBe 200
                userMileage.personalChargedMileage shouldBe 500
            }
        }
    }

    Given("마일리지 환불을 요청할 때") {
        val userId = 1L
        val testMileage = Mileage(userId, 1000, 500, LocalDate.now())

        When("해당 유저의 마일리지 정보가 존재하지 않으면") {
            every { mileageRepository.findById(userId) } returns Optional.empty()

            Then("UserMileageNotFoundException이 발생해야 한다") {
                shouldThrow<UserMileageNotFoundException> {
                    mileageService.refundMileage(RefundRequestDTO(userId, 500, "test reason"))
                }
            }
        }

        When("환불 요청이 성공적으로 처리되면") {
            every { mileageRepository.findById(userId) } returns Optional.of(testMileage)
            every { mileageRepository.save(any()) } answers { firstArg() }

            Then("해당 유저의 마일리지가 증가해야 한다") {
                val refundAmount = 500
                mileageService.refundMileage(RefundRequestDTO(userId, refundAmount, "test reason"))
                testMileage.mileage shouldBe 1000 + refundAmount
            }
        }
    }

    Given("마일리지 충전을 요청할 때") {
        val userId = 1L
        val requestAmount = 1000
        val depositor = "tester"
        val requestDTO = MileageRechargeRequestDTO(userId, requestAmount, depositor)

        every { chargeRequestRepository.save(any()) } answers { firstArg() }

        Then("충전 요청이 성공적으로 저장되어야 한다") {
            val chargeRequest = mileageService.requestMileageRecharge(requestDTO)

            chargeRequest.userId shouldBe userId
            chargeRequest.requestedMileage shouldBe requestAmount
            chargeRequest.depositorName shouldBe depositor
            chargeRequest.status shouldBe REQUESTED
        }
    }

    Given("마일리지 충전 요청 처리") {
        val chargeRequest = ChargeRequest(1L, REQUESTED, LocalDateTime.now(), "tester", 1000)
        val mileage = Mileage(1L, 5000, 0, LocalDate.now())

        every { chargeRequestRepository.findById(1L) } returns Optional.of(chargeRequest)
        every { mileageRepository.findById(1L) } returns Optional.of(mileage)
        every { chargeRequestRepository.save(any()) } returnsArgument 0
        every { mileageRepository.save(any()) } returnsArgument 0

        When("요청 상태에서 승인 요청이 들어올 경우") {
            chargeRequest.status = REQUESTED
            val request = MileageRechargeApprovalDTO(true)

            Then("해당 유저의 개인 마일리지가 충전되어야 하고, 충전 요청의 상태가 승인으로 변경되어야 한다") {
                val result = mileageService.processMileageRechargeRequest(1L, request)
                result.status shouldBe APPROVED
                mileage.personalChargedMileage shouldBe 1000
            }
        }

        When("요청 상태에서 거절 요청이 들어올 경우") {
            chargeRequest.status = REQUESTED
            val request = MileageRechargeApprovalDTO(false)

            Then("해당 유저의 개인 마일리지가 그대로 유지되어야 하고, 충전 요청의 상태가 거절로 변경되어야 한다") {
                val result = mileageService.processMileageRechargeRequest(1L, request)
                result.status shouldBe REJECTED
                mileage.personalChargedMileage shouldBe 1000 // 개인 마일리지는 그대로 유지
            }
        }

        When("승인 상태에서 승인 요청이 들어올 경우") {
            chargeRequest.status = APPROVED
            val request = MileageRechargeApprovalDTO(true)

            Then("해당 유저의 개인 마일리지와 충전 요청의 상태가 그대로 유지되어야 한다") {
                val result = mileageService.processMileageRechargeRequest(1L, request)
                result.status shouldBe APPROVED
                mileage.personalChargedMileage shouldBe 1000 // 개인 마일리지는 그대로 유지
            }
        }

        When("승인 상태에서 거절 요청이 들어올 경우") {
            chargeRequest.status = APPROVED
            val request = MileageRechargeApprovalDTO(false)

            Then("해당 유저의 개인 마일리지가 차감되어야 하고, 충전 요청의 상태가 거절로 변경되어야 한다") {
                val result = mileageService.processMileageRechargeRequest(1L, request)
                result.status shouldBe REJECTED
                mileage.personalChargedMileage shouldBe 0
            }
        }

        When("거절 상태에서 승인 요청이 들어올 경우") {
            chargeRequest.status = REJECTED
            val request = MileageRechargeApprovalDTO(true)

            Then("해당 유저의 개인 마일리지가 충전되어야 하고, 충전 요청의 상태가 승인으로 변경되어야 한다") {
                val result = mileageService.processMileageRechargeRequest(1L, request)
                result.status shouldBe APPROVED
                mileage.personalChargedMileage shouldBe 1000
            }
        }

        When("거절 상태에서 거절 요청이 들어올 경우") {
            chargeRequest.status = REJECTED
            val request = MileageRechargeApprovalDTO(false)

            Then("해당 유저의 개인 마일리지와 충전 요청의 상태가 그대로 유지되어야 한다") {
                val result = mileageService.processMileageRechargeRequest(1L, request)
                result.status shouldBe REJECTED
                mileage.personalChargedMileage shouldBe 1000 // 개인 마일리지는 그대로 유지
            }
        }
    }

    Given("연간 마일리지를 충전하려고 할 때") {
        val userMileageList = listOf(
            Mileage(1, 100_000, 20_000, LocalDate.now().minusYears(1)),
            Mileage(2, 1_000_000, 300_000, LocalDate.now().minusYears(2))
        )
        every { mileageRepository.findAll() } returns userMileageList

        When("연간 마일리지 충전 함수를 호출하면") {
            mileageService.chargeAnnualMileage()

            Then("모든 사용자의 마일리지가 100만으로 설정되어야 한다") {
                userMileageList.forEach { mileage ->
                    assertEquals(1_000_000, mileage.mileage)
                }
            }
        }
    }

    Given("사용자의 근속년수에 따른 분기 마일리지를 충전하려고 할 때") {
        val userMileageList = listOf(
            Mileage(1, 0, 20_000, LocalDate.now().minusYears(1)),
            Mileage(2, 0, 300_000, LocalDate.now().minusYears(2))
        )
        every { mileageRepository.findAll() } returns userMileageList

        When("근속년수에 따른 분기 마일리지 충전 함수를 호출하면") {
            mileageService.chargeQuarterlyMileageBasedOnTenure()

            Then("각 사용자의 마일리지가 근속년수에 따라 적립되어야 한다") {
                userMileageList.forEach { mileage ->
                    val yearsOfTenure = Period.between(mileage.joinDate, LocalDate.now()).years
                    val expectedMileage = yearsOfTenure * 10_000
                    assertEquals(expectedMileage, mileage.mileage)
                }
            }
        }
    }


})
