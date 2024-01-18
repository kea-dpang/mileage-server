package kea.dpang.mileage.repository

import kea.dpang.mileage.entity.ChargeRequest
import org.springframework.data.jpa.repository.JpaRepository

interface ChargeRequestRepository: JpaRepository<ChargeRequest, Long> {
}