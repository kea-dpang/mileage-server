package kea.dpang.mileage.repository

import kea.dpang.mileage.entity.Mileage
import org.springframework.data.jpa.repository.JpaRepository

interface MileageRepository: JpaRepository<Mileage, Long> {
}