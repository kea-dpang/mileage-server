package kea.dpang.mileage.controller

import kea.dpang.mileage.base.ErrorResponse
import kea.dpang.mileage.exception.ChargeRequestNotFoundException
import kea.dpang.mileage.exception.InsufficientMileageException
import kea.dpang.mileage.exception.UserMileageAlreadyExistsException
import kea.dpang.mileage.exception.UserMileageNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ChargeRequestNotFoundException::class)
    private fun handleChargeRequestNotFoundException(ex: ChargeRequestNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.name,
            message = ex.message ?: "세부 정보가 제공되지 않았습니다",
            path = request.getDescription(false)
        )

        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InsufficientMileageException::class)
    private fun handleInsufficientMileageException(ex: InsufficientMileageException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.name,
            message = ex.message ?: "세부 정보가 제공되지 않았습니다",
            path = request.getDescription(false)
        )

        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserMileageAlreadyExistsException::class)
    private fun handleUserMileageAlreadyExistsException(ex: UserMileageAlreadyExistsException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.name,
            message = ex.message ?: "세부 정보가 제공되지 않았습니다",
            path = request.getDescription(false)
        )

        return ResponseEntity(errorMessage, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(UserMileageNotFoundException::class)
    private fun handleUserMileageNotFoundException(ex: UserMileageNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.name,
            message = ex.message ?: "세부 정보가 제공되지 않았습니다",
            path = request.getDescription(false)
        )

        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }
}
