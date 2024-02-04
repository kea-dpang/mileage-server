package kea.dpang.mileage.repository

import kea.dpang.mileage.entity.ChargeRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChargeRequestRepository : JpaRepository<ChargeRequest, Long>, ChargeRequestRepositoryCustom {

    fun findByUserId(userId: Long): List<ChargeRequest>
}
