package io.github.borrelunde.scipio.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the integration between {@link Try} and {@link Optional}.
 * <p>
 * This test class is organized into logical groups based on the different integration aspects:
 * 1. Converting from Try to Optional - Tests for the toOptional() method
 * 2. Chaining operations with Optional - Tests for fluent API usage with Optional
 * 3. Converting from Optional to Try - Tests for creating Try instances from Optional
 * <p>
 * These tests ensure that Try and Optional work together seamlessly, allowing
 * developers to leverage both APIs consistently.
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
@DisplayName("Optional Integration Tests")
class OptionalIntegrationTest {

	/**
	 * Tests for converting Try instances to Optional.
	 * <p>
	 * The toOptional() method converts Success instances to Optional.of(value) and
	 * Failure instances to Optional.empty(). These tests verify this behaviour
	 * for different scenarios, including null values.
	 */
	@Nested
	@DisplayName("When converting from Try to Optional")
	class WhenConvertingFromTryToOptional {

		/**
		 * Tests for converting Success instances to Optional.
		 * <p>
		 * Success instances should be converted to Optional instances that contain
		 * the Success value, unless that value is null.
		 */
		@Nested
		@DisplayName("When converting Success to Optional")
		class WhenConvertingSuccessToOptional {

			@Test
			@DisplayName("Should convert to Optional with value")
			void shouldConvertToOptionalWithValue() {
				// Arrange
				Try<String> success = Try.success("test");

				// Act
				Optional<String> optional = success.toOptional();

				// Assert
				assertTrue(optional.isPresent(), "Optional should contain a value");
			}

			@Test
			@DisplayName("Should contain correct value")
			void shouldContainCorrectValue() {
				// Arrange
				Try<String> success = Try.success("test");

				// Act
				Optional<String> optional = success.toOptional();

				// Assert
				assertTrue(optional.isPresent(), "Optional should contain a value");
				assertEquals("test", optional.get(), "Optional value should match Success value");
			}

			@Test
			@DisplayName("Should reject null value in Success constructor")
			void shouldRejectNullValueInSuccessConstructor() {
				// Act & Assert
				assertThrows(NullPointerException.class, () -> Try.success(null),
						"Success constructor should reject null values");
			}
		}

		/**
		 * Tests for converting Failure instances to Optional.
		 * <p>
		 * Failure instances should always be converted to empty Optional instances,
		 * regardless of the exception they contain.
		 */
		@Nested
		@DisplayName("When converting Failure to Optional")
		class WhenConvertingFailureToOptional {

			@Test
			@DisplayName("Should convert to empty Optional")
			void shouldConvertToEmptyOptional() {
				// Arrange
				Try<String> failure = Try.failure(new RuntimeException());

				// Act
				Optional<String> optional = failure.toOptional();

				// Assert
				assertFalse(optional.isPresent(), "Optional should be empty when converted from Failure");
			}
		}
	}

	/**
	 * Tests for chaining operations with Optional after converting from Try.
	 * <p>
	 * These tests verify that the Optional returned from toOptional() can be
	 * used with the Optional API for further transformations and operations.
	 */
	@Nested
	@DisplayName("When chaining operations with Optional")
	class WhenChainingOperationsWithOptional {

		@Test
		@DisplayName("Should allow mapping operations on Success")
		void shouldAllowMappingOperationsOnSuccess() {
			// Arrange
			Try<Integer> success = Try.success(42);

			// Act
			String result = success
					.toOptional()
					.map(i -> i * 2)
					.map(i -> "Value: " + i)
					.orElse("No value");

			// Assert
			assertEquals("Value: 84", result, "Chained operations should transform the value correctly");
		}

		@Test
		@DisplayName("Should handle fallback for Failure")
		void shouldHandleFallbackForFailure() {
			// Arrange
			Try<Integer> failure = Try.failure(new RuntimeException());

			// Act
			String result = failure
					.toOptional()
					.map(i -> i * 2)
					.map(i -> "Value: " + i)
					.orElse("No value");

			// Assert
			assertEquals("No value", result, "Fallback value should be used when Optional is empty");
		}
	}

	/**
	 * Tests for converting Optional instances to Try.
	 * <p>
	 * These tests verify that Optional instances can be converted to Try instances,
	 * with present values becoming Success and empty values becoming Failure.
	 */
	@Nested
	@DisplayName("When converting from Optional to Try")
	class WhenConvertingFromOptionalToTry {

		/**
		 * Tests for converting non-empty Optional instances to Try.
		 */
		@Nested
		@DisplayName("When converting non-empty Optional to Try")
		class WhenConvertingNonEmptyOptionalToTry {

			private Try<String> result;

			@BeforeEach
			void setUp() {
				// Arrange
				final Optional<String> optional = Optional.of("test");

				// Act
				result = optional
						.map(Try::success)
						.orElseGet(() -> Try.failure(
								new RuntimeException("Empty optional"))
						);
			}

			@Test
			@DisplayName("Should result in Success")
			void shouldResultInSuccess() {
				// Assert
				assertTrue(result.isSuccess(), "Result should be a Success when Optional is present");
			}

			@Test
			@DisplayName("Should contain correct value")
			void shouldContainCorrectValue() throws Exception {
				// Assert
				assertEquals("test", result.get(), "Success value should match Optional value");
			}
		}

		/**
		 * Tests for converting empty Optional instances to Try.
		 */
		@Nested
		@DisplayName("When converting empty Optional to Try")
		class WhenConvertingEmptyOptionalToTry {

			private Try<String> result;
			private final String errorMessage = "Empty optional";

			@BeforeEach
			void setUp() {
				// Arrange
				final Optional<String> optional = Optional.empty();

				// Act
				result = optional
						.map(Try::success)
						.orElseGet(() ->
								Try.failure(new RuntimeException(errorMessage))
						);
			}

			@Test
			@DisplayName("Should result in Failure")
			void shouldResultInFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure when Optional is empty");
			}

			@Test
			@DisplayName("Should contain appropriate exception")
			void shouldContainAppropriateException() {
				// Assert
				Exception exception = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(errorMessage, exception.getMessage(),
						"Exception message should indicate empty Optional");
			}
		}
	}
}
