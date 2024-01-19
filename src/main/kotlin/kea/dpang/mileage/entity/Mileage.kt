package kea.dpang.mileage.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "mileage")
data class Mileage(

    @Id
    @Column(name = "user_id")
    var userId: Long,

    @Column(name = "mileage")
    var mileage: Int,

    @Column(name = "personal_charged_mileage")
    var personalChargedMileage: Int,

    @Column(name = "join_date")
    var joinDate: LocalDate

) {
    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
}