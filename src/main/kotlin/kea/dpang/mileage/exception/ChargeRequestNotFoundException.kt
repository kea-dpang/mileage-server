package kea.dpang.mileage.exception

class ChargeRequestNotFoundException(requestId: Long) : NoSuchElementException("충전 요청 ${requestId}가 존재하지 않습니다.")
