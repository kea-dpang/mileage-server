package kea.dpang.mileage.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "charge_request")
data class ChargeRequest(

    @NotNull
    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ChargeRequestStatus,

    @NotNull
    @Column(name = "request_date", nullable = false)
    var requestDate: LocalDateTime,

    @NotNull
    @Column(name = "depositor_name", nullable = false)
    var depositorName: String,

    @Min(0)
    @Column(name = "requested_mileage", nullable = false)
    var requestedMileage: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_request_id")
    var id: Long? = null
)
