package valentinaferro.u4w3d1.exceptions;

import valentinaferro.u4w3d1.payloads.ErrorResponse;
import valentinaferro.u4w3d1.payloads.ErrorsWithListDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

// @RestControllerAdvice: intercetta le eccezioni lanciate da QUALSIASI controller dell'app
// e le trasforma in risposte HTTP coerenti, centralizzando la gestione degli errori
// invece di ripetere try/catch identici in ogni controller.
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 400
	@ExceptionHandler(ValidationException.class) // scatta quando nel codice viene lanciata questa eccezione
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorsWithListDTO handleValidation(ValidationException ex) {
		return new ErrorsWithListDTO(ex.getMessage(), LocalDateTime.now(), ex.getErrorsList());
	}

	@ExceptionHandler(DuplicateEmailException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleDuplicateEmail(DuplicateEmailException ex) {
		return new ErrorResponse(LocalDateTime.now(), ex.getMessage());
	}


	// 404
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFound(NotFoundException ex) {
		return new ErrorResponse(LocalDateTime.now(), ex.getMessage());
	}

	// 401
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorResponse handleUnauthorized(UnauthorizedException ex) {
		return new ErrorResponse(LocalDateTime.now(), ex.getMessage());
	}


	// 500: rete di sicurezza per qualunque altra eccezione non gestita esplicitamente sopra
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleGeneric(Exception ex) {
		ex.printStackTrace(); // utile in fase di sviluppo per vedere lo stack trace completo nei log
		return new ErrorResponse(LocalDateTime.now(), "Errore interno del server: " + ex.getMessage());
	}

}
