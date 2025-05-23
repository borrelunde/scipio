package io.github.borrelunde.scipio.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Try} interface and its implementations.
 * <p>
 * This test class is organized into logical groups based on the different aspects of the Try interface:
 * 1. Creation using direct factory methods (success/failure)
 * 2. Creation using the Try.of method which handles exceptions automatically
 * 3. Conversion to Optional
 * 4. Instance comparison behaviour
 * <p>
 * Each group is implemented as a nested class to provide clear separation of concerns
 * and to allow for focused testing of specific behaviours.
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
@DisplayName("Try Tests")
class TryTest {

	/**
	 * Tests for the direct factory methods: success() and failure().
	 * <p>
	 * These methods are grouped together because they represent the simplest way to create
	 * Try instances with predetermined outcomes. Unlike Try.of(), these methods don't involve
	 * exception handling logic - they directly create Success or Failure instances.
	 */
	@Nested
	@DisplayName("When creating with factory methods")
	class WhenCreatingWithFactoryMethods {

		/**
		 * Tests for the Success factory method.
		 * <p>
		 * Success instances represent successful computations, so we test all aspects
		 * of a successful outcome: correct state flags, value retrieval, and string representation.
		 * Each test focuses on a single behaviour to ensure clear failure messages.
		 */
		@Nested
		@DisplayName("When creating Success")
		class WhenCreatingSuccess {

			private String value;
			private Try<String> result;

			@BeforeEach
			void setUp() {
				// Arrange
				value = "test";

				// Act
				result = Try.success(value);
			}

			@Test
			@DisplayName("Should be a Success")
			void shouldBeSuccess() {
				// Assert
				assertTrue(result.isSuccess(), "Result should be a Success");
			}

			@Test
			@DisplayName("Should not be a Failure")
			void shouldNotBeFailure() {
				// Assert
				assertFalse(result.isFailure(), "Result should not be a Failure");
			}

			@Test
			@DisplayName("Should return correct value")
			void shouldReturnCorrectValue() throws Exception {
				// Assert
				assertEquals(value, result.get(), "Success value should match input");
			}

			@Test
			@DisplayName("Should have correct string representation")
			void shouldHaveCorrectStringRepresentation() {
				// Assert
				assertEquals("Success(test)", result.toString(), "String representation should be correct");
			}
		}

		/**
		 * Tests for the Failure factory method.
		 * <p>
		 * Failure instances represent failed computations, so we test all aspects
		 * of a failure outcome: correct state flags, exception throwing behaviour, and string representation.
		 * These tests mirror the Success tests to ensure both outcomes are thoroughly verified.
		 */
		@Nested
		@DisplayName("When creating Failure")
		class WhenCreatingFailure {

			private Exception exception;
			private Try<String> result;

			@BeforeEach
			void setUp() {
				// Arrange
				exception = new RuntimeException("test exception");

				// Act
				result = Try.failure(exception);
			}

			@Test
			@DisplayName("Should not be a Success")
			void shouldNotBeSuccess() {
				// Assert
				assertFalse(result.isSuccess(), "Result should not be a Success");
			}

			@Test
			@DisplayName("Should be a Failure")
			void shouldBeFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure");
			}

			@Test
			@DisplayName("Should throw exception when getting value")
			void shouldThrowExceptionWhenGettingValue() {
				// Assert
				Exception thrownException = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(exception, thrownException, "Exception should match input");
			}

			@Test
			@DisplayName("Should have correct string representation")
			void shouldHaveCorrectStringRepresentation() {
				// Assert
				assertEquals("Failure(java.lang.RuntimeException: test exception)", result.toString(),
						"String representation should be correct");
			}
		}
	}

	/**
	 * Tests for the Try.of factory method.
	 * <p>
	 * This method is tested separately from the direct factory methods because it has fundamentally
	 * different behavior - it executes a supplier function and automatically handles exceptions.
	 * This creates two distinct paths (success and failure) that need to be tested independently,
	 * making it more complex than the direct factory methods.
	 */
	@Nested
	@DisplayName("When creating with Try.of method")
	class WhenCreatingWithOfMethod {

		@Test
		@DisplayName("Should create Success when supplier succeeds")
		void shouldCreateSuccessWhenSupplierSucceeds() {
			// Act
			Try<String> result = Try.of(() -> "test");

			// Assert
			assertTrue(result.isSuccess(), "Result should be a Success");
		}

		@Test
		@DisplayName("Should return correct value when supplier succeeds")
		void shouldReturnCorrectValueWhenSupplierSucceeds() throws Exception {
			// Act
			Try<String> result = Try.of(() -> "test");

			// Assert
			assertEquals("test", result.get(), "Success value should match supplier result");
		}

		/**
		 * Tests for the failure path of Try.of when the supplier throws an exception.
		 * <p>
		 * This is separated into its own nested class because it tests a completely different
		 * execution path than when the supplier succeeds. It requires special setup with a
		 * supplier that deliberately throws an exception and focuses on verifying that
		 * exceptions are properly captured and wrapped in a Failure instance.
		 */
		@Nested
		@DisplayName("When supplier throws")
		class WhenSupplierThrows {

			private final Supplier<String> supplier = () -> {
				throw new RuntimeException("test exception");
			};

			private Try<String> result;

			@BeforeEach
			void setUp() {
				result = Try.of(supplier);
			}

			@Test
			@DisplayName("Should create Failure")
			void shouldCreateFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure");
			}

			@Test
			@DisplayName("Should throw exception when getting value")
			void shouldThrowExceptionWhenGettingValue() {
				// Assert
				assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
			}

			@Test
			@DisplayName("Should get exception message")
			void shouldGetExceptionMessage() {
				// Act
				Exception thrownException = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");

				// Assert
				assertEquals("test exception", thrownException.getMessage(),
						"Exception message should match original message");
			}
		}
	}

	/**
	 * Tests for the toOptional() method.
	 * <p>
	 * This functionality is grouped separately because it tests the integration between
	 * Try and Optional types. Since Optional is a core Java type for representing
	 * potentially absent values, it's important to verify that Try instances convert
	 * correctly to maintain semantic consistency between the two types.
	 */
	@Nested
	@DisplayName("When converting to Optional")
	class WhenConvertingToOptional {

		@Test
		@DisplayName("Success should convert to Optional with value")
		void successShouldConvertToOptionalWithValue() {
			// Arrange
			Try<String> success = Try.success("test");

			// Act
			Optional<String> optional = success.toOptional();

			// Assert
			assertTrue(optional.isPresent(), "Optional should contain a value");
		}

		@Test
		@DisplayName("Success should convert to Optional with correct value")
		void successShouldConvertToOptionalWithCorrectValue() {
			// Arrange
			Try<String> success = Try.success("test");

			// Act
			Optional<String> optional = success.toOptional();

			// Assert
			assertTrue(optional.isPresent(), "Optional should contain a value");
			assertEquals("test", optional.get(), "Optional value should match Success value");
		}

		@Test
		@DisplayName("Failure should convert to empty Optional")
		void failureShouldConvertToEmptyOptional() {
			// Arrange
			Try<String> failure = Try.failure(new RuntimeException());

			// Act
			Optional<String> optional = failure.toOptional();

			// Assert
			assertFalse(optional.isPresent(), "Optional should be empty");
		}
	}

	/**
	 * Tests for equality comparison between Try instances.
	 * <p>
	 * This is grouped separately because equality testing is a distinct aspect of object behaviour
	 * that's independent of the core Try functionality. Proper equality implementation is crucial
	 * for collections and other contexts where object comparison is needed, so it deserves
	 * dedicated testing to ensure consistent behaviour.
	 */
	@Nested
	@DisplayName("When comparing instances")
	class WhenComparingInstances {

		@Test
		@DisplayName("Success instances with same value should be equal")
		void successInstancesWithSameValueShouldBeEqual() {
			// Arrange
			Try<String> successOne = Try.success("test");
			Try<String> successTwo = Try.success("test");

			// Assert
			assertEquals(successOne, successTwo, "Success instances with same value should be equal");
		}

		@Test
		@DisplayName("Failure instances with same exception should be equal")
		void failureInstancesWithSameExceptionShouldBeEqual() {
			// Arrange
			Exception exception = new RuntimeException("test");
			Try<String> failureOne = Try.failure(exception);
			Try<String> failureTwo = Try.failure(exception);

			// Assert
			assertEquals(failureOne, failureTwo, "Failure instances with same exception should be equal");
		}
	}
}
