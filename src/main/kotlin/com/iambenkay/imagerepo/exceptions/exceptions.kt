package com.iambenkay.imagerepo.exceptions

import com.iambenkay.imagerepo.types.ApiResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/* HTTP LEVEL EXCEPTIONS */
open class HttpStatusException(override val message: String, val status: HttpStatus) :
    Exception(message)

open class BadRequestException(override val message: String) :
    HttpStatusException(message, HttpStatus.BAD_REQUEST)

open class InternalServerErrorException(override val message: String) :
    HttpStatusException(message, HttpStatus.INTERNAL_SERVER_ERROR)

open class NotFoundException(override val message: String) :
    HttpStatusException(message, HttpStatus.NOT_FOUND)
/* END */


/* APP LEVEL EXCEPTIONS INHERITING FROM HTTP LEVEL */
class InvalidImageUploadedException : BadRequestException("The image you uploaded is an invalid image")

class ImageNotDeletedException : BadRequestException("The delete operation you requested could not be completed")

class ImageSaveFailedException : InternalServerErrorException("The save operation you requested could not be completed")

class ImageRetrieveFailedException :
    InternalServerErrorException("The save operation you requested could not be completed")

class ImageNotFoundException : NotFoundException("The retrieve operation you requested could not be completed")
/* END */

/* CONTROLLER ADVICE TO SUGGEST TO SPRING HOW TO HANDLE CERTAIN ERRORS */
@ControllerAdvice
class ServiceExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(HttpStatusException::class)
    protected fun handleHttpStatusError(e: HttpStatusException): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity<ApiResponse<Any>>(ApiResponse(true, e.message, null), e.status)
    }

    @ExceptionHandler(MultipartException::class)
    protected fun handleMultipartError(e: MultipartException): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity<ApiResponse<Any>>(ApiResponse(true, e.message!!, null), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    protected fun handleIllegalArgumentError(e: IllegalArgumentException): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity<ApiResponse<Any>>(ApiResponse(true, e.message!!, null), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NumberFormatException::class)
    protected fun handleNumberFormatError(e: NumberFormatException): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity<ApiResponse<Any>>(ApiResponse(true, "Number is not valid", null), HttpStatus.BAD_REQUEST)
    }

    override fun handleMissingServletRequestParameter(
        e: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(
            ApiResponse(true, "'${e.parameterName}' must be provided", null),
            HttpStatus.BAD_REQUEST
        )
    }

    override fun handleMissingServletRequestPart(
        e: MissingServletRequestPartException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(
            ApiResponse(true, "'${e.requestPartName}' must be provided", null),
            HttpStatus.BAD_REQUEST
        )
    }

    override fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(
            ApiResponse(true, e.mostSpecificCause.localizedMessage, null),
            HttpStatus.BAD_REQUEST
        )
    }

    override fun handleBindException(
        e: BindException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = HashMap<String, String>()
        e.bindingResult.allErrors.forEach {
            val fieldName = (it as FieldError).field
            errors[fieldName] = "Invalid value received '${it.rejectedValue}'"
        }
        return ResponseEntity(
            ApiResponse(true, "Body binding error occurred", errors),
            HttpStatus.BAD_REQUEST
        )
    }
}

