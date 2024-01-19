package kea.dpang.mileage.repository

import kea.dpang.mileage.entity.ChargeRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface ChargeRequestRepository : JpaRepository<ChargeRequest, Long>, QuerydslPredicateExecutor<ChargeRequest> {
}
