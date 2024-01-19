package kea.dpang.mileage.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "charge_request")
data class ChargeRequest(

    @Column(name = "user_id")
    var userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: ChargeRequestStatus,

    @Column(name = "request_date")
    var requestDate: LocalDateTime,

    @Column(name = "depositor_name")
    var depositorName: String,

    @Column(name = "requested_mileage")
    var requestedMileage: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_request_id")
    var chargeRequestId: Long? = null
)
