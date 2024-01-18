package kea.dpang.mileage.entity

import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
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
    var personalChargedMileage: Int
) {
    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
}