package org.users.core.validation;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

public class ValidationExceptionHandlerTest {
	@Test
	public void handleDataIntegrityViolation_UniqueViolation() {
		var v = new ValidationExceptionHandler();
		var ex = Mockito.mock(DataIntegrityViolationException.class);
		var constraintEx = Mockito.mock(org.hibernate.exception.ConstraintViolationException.class);
		var sqlEx = Mockito.mock(java.sql.SQLIntegrityConstraintViolationException.class);
		when(ex.getCause()).thenReturn(constraintEx);
		// authentically generated exception message
		when(constraintEx.getMessage()).thenReturn("could not execute statement [ERROR: duplicate key value violates unique constraint \"uk_6dotkott2kjsp8vw4d0m25fb7\"\n" +
				"  Detail: Key (email)=(johndoe@example.com) already exists.] [insert into users (house_number,street,city,country,zip_code,birth_date,created_at,email,first_name,last_name,phone_number,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?)]; SQL [insert into users (house_number,street,city,country,zip_code,birth_date,created_at,email,first_name,last_name,phone_number,updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?)]; constraint [uk_6dotkott2kjsp8vw4d0m25fb7]");
		when(constraintEx.getSQLException()).thenReturn(sqlEx);
		when(sqlEx.getMessage()).thenReturn("ERROR: duplicate key value violates unique constraint \"uk_6dotkott2kjsp8vw4d0m25fb7\"\n " +
				"Detail: Key (email)=(johndoe@example.com) already exists.");
		var expected = ResponseEntity.badRequest().body("email must be unique, but a duplicate was found: johndoe@example.com");
		var actual = v.handleDataIntegrityViolation(ex);

		assertThat(actual.getBody()).asString().isEqualToIgnoringWhitespace(expected.getBody());
	}
}
