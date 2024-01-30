package kea.dpang.mileage.controller

import kea.dpang.mileage.base.ErrorResponse
import kea.dpang.mileage.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ChargeRequestNotFoundException::class, UserMileageNotFoundException::class)
    private fun handleNotFoundException(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> {
        return generateErrorResponse(HttpStatus.NOT_FOUND, ex, request)
    }

    @ExceptionHandler(InsufficientMileageException::class, AlreadyRequestedException::class, AlreadyApprovedException::class, AlreadyRejectedException::class)
    private fun handleBadRequestException(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> {
        return generateErrorResponse(HttpStatus.BAD_REQUEST, ex, request)
    }

    @ExceptionHandler(UserMileageAlreadyExistsException::class)
    private fun handleConflictException(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> {
        return generateErrorResponse(HttpStatus.CONFLICT, ex, request)
    }

    private fun generateErrorResponse(status: HttpStatus, ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = status.value(),
            error = status.name,
            message = ex.message ?: "세부 정보가 제공되지 않았습니다",
            path = request.getDescription(false)
        )

        return ResponseEntity(errorMessage, status)
    }
}
