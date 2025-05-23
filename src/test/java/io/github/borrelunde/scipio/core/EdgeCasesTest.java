package io.github.borrelunde.scipio.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for edge cases and error handling in the {@link Try} interface.
 * <p>
 * This test class is organized into logical groups based on different types of edge cases:
 * 1. Null handling - Tests how the API handles null inputs to various methods
 * 2. Exception handling - Tests how the API handles different types of exceptions
 * <p>
 * These tests ensure the API behaves predictably in exceptional circumstances
 * and provides appropriate exception messages.
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
@DisplayName("Edge Cases and Error Handling Tests")
class EdgeCasesTest {

	/**
	 * Tests for null handling in the Try interface.
	 * <p>
	 * These tests verify that appropriate NullPointerExceptions are thrown
	 * when null values are passed to various methods of the Try interface.
	 * This ensures the API fails fast with clear error messages rather than
	 * causing confusing NullPointerExceptions later.
	 */
	@Nested
	@DisplayName("When handling null inputs")
	class WhenHandlingNullInputs {

		/**
		 * Tests for null handling in factory methods.
		 * <p>
		 * The factory methods (success, failure, of) should reject null inputs
		 * to prevent the creation of invalid Try instances.
		 */
		@Nested
		@DisplayName("When factory methods receive null")
		class WhenFactoryMethodsReceiveNull {

			@Test
			@DisplayName("Should reject null value in Success constructor")
			void shouldRejectNullValueInSuccessConstructor() {
				// Act & Assert
				assertThrows(NullPointerException.class, () -> Try.success(null),
						"Success constructor should reject null values");
			}

			@Test
			@DisplayName("Should reject null exception in Failure constructor")
			void shouldRejectNullExceptionInFailureConstructor() {
				// Act & Assert
				assertThrows(NullPointerException.class, () -> Try.failure(null),
						"Failure constructor should reject null exceptions");
			}

			@Test
			@DisplayName("Should reject null supplier in of method")
			void shouldRejectNullSupplierInOfMethod() {
				// Act & Assert
				assertThrows(NullPointerException.class, () -> Try.of(null),
						"Try.of method should reject null suppliers");
			}
		}

		/**
		 * Tests for null handling in transformation methods.
		 * <p>
		 * The transformation methods (map, flatMap) should reject null function arguments
		 * to prevent NullPointerExceptions during transformation operations.
		 */
		@Nested
		@DisplayName("When transformation methods receive null")
		class WhenTransformationMethodsReceiveNull {

			private Try<Integer> success;

			@BeforeEach
			void setUp() {
				success = Try.success(42);
			}

			@Test
			@DisplayName("Should reject null mapper in map method")
			void shouldRejectNullMapperInMapMethod() {
				// Act & Assert
				assertThrows(NullPointerException.class, () -> success.map(null),
						"map method should reject null mappers");
			}

			@Test
			@DisplayName("Should reject null mapper in flatMap method")
			void shouldRejectNullMapperInFlatMapMethod() {
				// Act & Assert
				assertThrows(NullPointerException.class, () -> success.flatMap(null),
						"flatMap method should reject null mappers");
			}
		}

		/**
		 * Tests for null handling in recovery methods.
		 * <p>
		 * The recovery methods (recover, recoverWith) should reject null function arguments
		 * to prevent NullPointerExceptions during recovery operations.
		 */
		@Nested
		@DisplayName("When recovery methods receive null")
		class WhenRecoveryMethodsReceiveNull {

			private Try<Integer> failure;

			@BeforeEach
			void setUp() {
				failure = Try.failure(new RuntimeException());
			}

			@Test
			@DisplayName("Should reject null recovery in recover method")
			void shouldRejectNullRecoveryInRecoverMethod() {
				// Act & Assert
				assertThrows(NullPointerException.class, () -> failure.recover(null),
						"recover method should reject null recovery functions");
			}

			@Test
			@DisplayName("Should reject null recovery in recoverWith method")
			void shouldRejectNullRecoveryInRecoverWithMethod() {
				// Act & Assert
				assertThrows(NullPointerException.class, () -> failure.recoverWith(null),
						"recoverWith method should reject null recovery functions");
			}
		}
	}

	/**
	 * Tests for exception handling in the Try interface.
	 * <p>
	 * These tests verify that the Try interface properly handles different types of exceptions
	 * that might occur during execution, including checked exceptions and Errors.
	 */
	@Nested
	@DisplayName("When handling exceptions")
	class WhenHandlingExceptions {

		/**
		 * Tests for handling checked exceptions in suppliers.
		 */
		@Nested
		@DisplayName("When supplier throws checked exception")
		class WhenSupplierThrowsCheckedException {

			private Try<String> result;
			private final String exceptionMessage = "Checked exception";

			@BeforeEach
			void setUp() {
				// Act
				result = Try.of(() -> {
					try {
						throw new Exception(exceptionMessage);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			}

			@Test
			@DisplayName("Should result in Failure")
			void shouldResultInFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure when supplier throws");
			}

			@Test
			@DisplayName("Should contain original exception message")
			void shouldContainOriginalExceptionMessage() {
				// Assert
				Exception exception = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(exceptionMessage, exception.getCause().getMessage(),
						"Exception message should match original message");
			}
		}

		/**
		 * Tests for handling unchecked exceptions in suppliers.
		 * <p>
		 * Unchecked exceptions (like RuntimeException) are directly thrown from the supplier
		 * without needing to be declared or caught. Try should properly capture these exceptions
		 * and wrap them in a Failure.
		 */
		@Nested
		@DisplayName("When supplier throws unchecked exception")
		class WhenSupplierThrowsUncheckedException {

			private Try<String> result;
			private final String exceptionMessage = "Unchecked exception";
			private final RuntimeException thrownException = new RuntimeException(exceptionMessage);

			@BeforeEach
			void setUp() {
				// Act
				result = Try.of(() -> {
					throw thrownException;
				});
			}

			@Test
			@DisplayName("Should result in Failure")
			void shouldResultInFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure when supplier throws unchecked exception");
			}

			@Test
			@DisplayName("Should contain original exception")
			void shouldContainOriginalException() {
				// Assert
				Exception exception = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(thrownException, exception, 
						"Exception should be the original unchecked exception");
			}

			@Test
			@DisplayName("Should preserve exception message")
			void shouldPreserveExceptionMessage() {
				// Assert
				Exception exception = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(exceptionMessage, exception.getMessage(),
						"Exception message should be preserved");
			}
		}
	}
}
