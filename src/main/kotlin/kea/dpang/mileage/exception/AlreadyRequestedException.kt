package kea.dpang.mileage.exception

class AlreadyRequestedException(id: Long): RuntimeException("마일리지 충전 요청 (식별자: $id)이 이미 REQUESTED 상태입니다.")
