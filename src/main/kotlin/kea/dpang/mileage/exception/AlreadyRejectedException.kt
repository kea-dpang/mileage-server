package kea.dpang.mileage.exception

class AlreadyRejectedException(id: Long): RuntimeException("마일리지 충전 요청 (식별자: $id)이 이미 REJECTED 상태입니다.")
