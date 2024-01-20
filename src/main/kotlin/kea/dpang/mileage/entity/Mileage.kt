package kea.dpang.mileage.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "mileage")
data class Mileage(

    @Id
    @Column(name = "user_id")
    var userId: Long,

    @Min(0)
    @Column(name = "mileage", nullable = false)
    var mileage: Int,

    @Min(0)
    @Column(name = "personal_charged_mileage", nullable = false)
    var personalChargedMileage: Int,

    @NotNull
    @Column(name = "join_date", nullable = false)
    var joinDate: LocalDate

) {
    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
}