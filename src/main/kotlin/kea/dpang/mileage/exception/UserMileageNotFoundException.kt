package kea.dpang.mileage.exception

class UserMileageNotFoundException(userId: Long) : NoSuchElementException("사용자 {$userId}의 마일리지 정보가 존재하지 않습니다.")
