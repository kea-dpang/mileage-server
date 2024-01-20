package kea.dpang.mileage.exception

class UserMileageAlreadyExistsException(userId: Long) : RuntimeException("사용자 아이디 ${userId}에 대한 마일리지가 이미 존재합니다.")
