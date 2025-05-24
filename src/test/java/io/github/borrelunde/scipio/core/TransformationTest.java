package io.github.borrelunde.scipio.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the transformation methods of the {@link Try} interface.
 * <p>
 * This test class is organized into logical groups based on the different transformation methods:
 * 1. map() - Transforms a Success value using a function that returns a raw value
 * 2. flatMap() - Transforms a Success value using a function that returns another Try
 * <p>
 * Each method is tested with both Success and Failure inputs, and with various scenarios
 * like exceptions during transformation.
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
@DisplayName("Transformation Methods Tests")
class TransformationTest {

	/**
	 * Tests for the map() method.
	 * <p>
	 * The map() method transforms a Success value using a function but doesn't affect Failure instances.
	 * These tests verify both the happy path (successful transformation) and various edge cases
	 * like exceptions during mapping and behaviour with Failure instances.
	 */
	@Nested
	@DisplayName("When using map method")
	class WhenUsingMapMethod {

		/**
		 * Tests for mapping Success instances.
		 * <p>
		 * Success instances should be transformed by the map function,
		 * resulting in a new Success containing the transformed value.
		 */
		@Nested
		@DisplayName("When mapping Success")
		class WhenMappingSuccess {

			private Try<String> result;

			@BeforeEach
			void setUp() {
				// Arrange
				final Try<Integer> success = Try.success(42);

				// Act
				result = success.map(i -> "Value: " + i);
			}

			@Test
			@DisplayName("Should remain a Success")
			void shouldRemainSuccess() {
				// Assert
				assertTrue(result.isSuccess(), "Result should be a Success");
			}

			@Test
			@DisplayName("Should contain transformed value")
			void shouldContainTransformedValue() throws Exception {
				// Assert
				assertEquals("Value: 42", result.get(), "Success value should be transformed");
			}
		}

		/**
		 * Tests for when the mapping function throws an exception.
		 * <p>
		 * When the mapping function throws, the result should be a Failure
		 * containing the thrown exception.
		 */
		@Nested
		@DisplayName("When mapping function throws")
		class WhenMappingFunctionThrows {

			private Try<String> result;
			private final String errorMessage = "Mapping error";

			@BeforeEach
			void setUp() {
				// Arrange
				final Try<Integer> success = Try.success(42);

				// Act
				result = success.map(i -> {
					throw new RuntimeException(errorMessage);
				});
			}

			@Test
			@DisplayName("Should convert to Failure")
			void shouldConvertToFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure");
			}

			@Test
			@DisplayName("Should contain thrown exception")
			void shouldContainThrownException() {
				// Assert
				Exception exception = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(errorMessage, exception.getMessage(),
						"Exception message should match original message");
			}
		}

		/**
		 * Tests for mapping Failure instances.
		 * <p>
		 * Failure instances should not be affected by the map function
		 * and should retain their original exception.
		 */
		@Nested
		@DisplayName("When mapping Failure")
		class WhenMappingFailure {

			private Exception exception;
			private Try<String> result;

			@BeforeEach
			void setUp() {
				// Arrange
				exception = new RuntimeException("Original error");
				final Try<Integer> failure = Try.failure(exception);

				// Act
				result = failure.map(i -> "Value: " + i);
			}

			@Test
			@DisplayName("Should remain a Failure")
			void shouldRemainFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure");
			}

			@Test
			@DisplayName("Should contain original exception")
			void shouldContainOriginalException() {
				// Assert
				Exception resultException = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(exception, resultException,
						"Exception should be the original exception");
			}
		}
	}

	/**
	 * Tests for the flatMap() method.
	 * <p>
	 * The flatMap() method transforms a Success value using a function that returns another Try
	 * but doesn't affect Failure instances. These tests verify both the happy path
	 * (successful transformation) and various edge cases like exceptions during flatMapping,
	 * flatMapping to a Failure, and behaviour with Failure instances.
	 */
	@Nested
	@DisplayName("When using flatMap method")
	class WhenUsingFlatMapMethod {

		/**
		 * Tests for flatMapping Success instances to another Success.
		 * <p>
		 * Success instances should be transformed by the flatMap function,
		 * resulting in the Try instance returned by the function.
		 */
		@Nested
		@DisplayName("When flatMapping Success to Success")
		class WhenFlatMappingSuccessToSuccess {

			private Try<String> result;

			@BeforeEach
			void setUp() {
				// Arrange
				final Try<Integer> success = Try.success(42);

				// Act
				result = success.flatMap(i -> Try.success("Value: " + i));
			}

			@Test
			@DisplayName("Should result in Success")
			void shouldResultInSuccess() {
				// Assert
				assertTrue(result.isSuccess(), "Result should be a Success");
			}

			@Test
			@DisplayName("Should contain transformed value")
			void shouldContainTransformedValue() throws Exception {
				// Assert
				assertEquals("Value: 42", result.get(), "Success value should be transformed");
			}
		}

		/**
		 * Tests for when the flatMapping function throws an exception.
		 * <p>
		 * When the flatMapping function throws, the result should be a Failure
		 * containing the thrown exception.
		 */
		@Nested
		@DisplayName("When flatMapping function throws")
		class WhenFlatMappingFunctionThrows {

			private Try<String> result;
			private final String errorMessage = "FlatMapping error";

			@BeforeEach
			void setUp() {
				// Arrange
				final Try<Integer> success = Try.success(42);

				// Act
				result = success.flatMap(i -> {
					throw new RuntimeException(errorMessage);
				});
			}

			@Test
			@DisplayName("Should result in Failure")
			void shouldResultInFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure");
			}

			@Test
			@DisplayName("Should contain thrown exception")
			void shouldContainThrownException() {
				// Assert
				Exception exception = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(errorMessage, exception.getMessage(),
						"Exception message should match original message");
			}
		}

		/**
		 * Tests for flatMapping Success instances to a Failure.
		 * <p>
		 * When the flatMap function returns a Failure, the result should be that Failure.
		 */
		@Nested
		@DisplayName("When flatMapping Success to Failure")
		class WhenFlatMappingSuccessToFailure {

			private Exception mappedException;
			private Try<String> result;

			@BeforeEach
			void setUp() {
				// Arrange
				final Try<Integer> success = Try.success(42);
				mappedException = new RuntimeException("Mapped error");

				// Act
				result = success.flatMap(i -> Try.failure(mappedException));
			}

			@Test
			@DisplayName("Should result in Failure")
			void shouldResultInFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure");
			}

			@Test
			@DisplayName("Should contain mapped exception")
			void shouldContainMappedException() {
				// Assert
				Exception resultException = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(mappedException, resultException,
						"Exception should be the mapped exception");
			}
		}

		/**
		 * Tests for flatMapping Failure instances.
		 * <p>
		 * Failure instances should not be affected by the flatMap function
		 * and should retain their original exception.
		 */
		@Nested
		@DisplayName("When flatMapping Failure")
		class WhenFlatMappingFailure {

			private Exception exception;
			private Try<String> result;

			@BeforeEach
			void setUp() {
				// Arrange
				exception = new RuntimeException("Original error");
				final Try<Integer> failure = Try.failure(exception);

				// Act
				result = failure.flatMap(i -> Try.success("Value: " + i));
			}

			@Test
			@DisplayName("Should remain a Failure")
			void shouldRemainFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure");
			}

			@Test
			@DisplayName("Should contain original exception")
			void shouldContainOriginalException() {
				// Assert
				Exception resultException = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertEquals(exception, resultException,
						"Exception should be the original exception");
			}
		}
	}

	/**
	 * Tests for the fold() method.
	 * <p>
	 * The fold() method transforms a Try into a value of a different type by applying
	 * either the success function to the value if it's a Success, or the failure function
	 * to the exception if it's a Failure. These tests verify both the happy path
	 * (successful transformation) and various edge cases like exceptions during folding.
	 */
	@Nested
	@DisplayName("When using fold method")
	class WhenUsingFoldMethod {

		/**
		 * Tests for folding Success instances.
		 * <p>
		 * Success instances should be transformed by the success function,
		 * resulting in the value returned by the function.
		 */
		@Nested
		@DisplayName("When folding Success")
		class WhenFoldingSuccess {

			private String result;

			@BeforeEach
			void setUp() {
				// Arrange
				final Try<Integer> success = Try.success(42);

				// Act
				result = success.fold(
						i -> "Success: " + i,
						e -> "Failure: " + e.getMessage()
				);
			}

			@Test
			@DisplayName("Should apply success function")
			void shouldApplySuccessFunction() {
				// Assert
				assertEquals("Success: 42", result, "Success function should be applied");
			}
		}

		/**
		 * Tests for when the success function throws an exception.
		 * <p>
		 * When the success function throws, the failure function should be applied
		 * to the thrown exception.
		 */
		@Nested
		@DisplayName("When success function throws")
		class WhenSuccessFunctionThrows {

			private String result;
			private final String errorMessage = "Success function error";

			@BeforeEach
			void setUp() {
				// Arrange
				final Try<Integer> success = Try.success(42);

				// Act
				result = success.fold(
						i -> {
							throw new RuntimeException(errorMessage);
						},
						e -> "Failure: " + e.getMessage()
				);
			}

			@Test
			@DisplayName("Should apply failure function to thrown exception")
			void shouldApplyFailureFunctionToThrownException() {
				// Assert
				assertEquals("Failure: " + errorMessage, result,
						"Failure function should be applied to exception thrown by success function");
			}
		}

		/**
		 * Tests for when both functions throw exceptions.
		 * <p>
		 * When both the success and failure functions throw, a RuntimeException should be thrown.
		 */
		@Nested
		@DisplayName("When both functions throw")
		class WhenBothFunctionsThrow {

			private Try<Integer> success;
			private final String successErrorMessage = "Success function error";
			private final String failureErrorMessage = "Failure function error";

			@BeforeEach
			void setUp() {
				// Arrange
				success = Try.success(42);
			}

			@Test
			@DisplayName("Should throw RuntimeException")
			void shouldThrowRuntimeException() {
				// Assert
				RuntimeException exception = assertThrows(RuntimeException.class, () -> success.fold(
						i -> {
							throw new RuntimeException(successErrorMessage);
						},
						e -> {
							throw new RuntimeException(failureErrorMessage);
						}
				), "Should throw RuntimeException when both functions throw");

				assertEquals("Both success and failure functions threw exceptions", exception.getMessage(),
						"Exception message should indicate both functions threw");
			}
		}

		/**
		 * Tests for folding Failure instances.
		 * <p>
		 * Failure instances should be transformed by the failure function,
		 * resulting in the value returned by the function.
		 */
		@Nested
		@DisplayName("When folding Failure")
		class WhenFoldingFailure {

			private String result;
			private final String errorMessage = "Original error";

			@BeforeEach
			void setUp() {
				// Arrange
				final Exception exception = new RuntimeException(errorMessage);
				final Try<Integer> failure = Try.failure(exception);

				// Act
				result = failure.fold(
						i -> "Success: " + i,
						e -> "Failure: " + e.getMessage()
				);
			}

			@Test
			@DisplayName("Should apply failure function")
			void shouldApplyFailureFunction() {
				// Assert
				assertEquals("Failure: " + errorMessage, result, "Failure function should be applied");
			}
		}

		/**
		 * Tests for when the failure function throws an exception.
		 * <p>
		 * When the failure function throws, a RuntimeException should be thrown.
		 */
		@Nested
		@DisplayName("When failure function throws")
		class WhenFailureFunctionThrows {

			private Try<Integer> failure;
			@SuppressWarnings("FieldCanBeLocal")  // Improved readability
			private final String originalErrorMessage = "Original error";
			private final String failureErrorMessage = "Failure function error";

			@BeforeEach
			void setUp() {
				// Arrange
				final Exception exception = new RuntimeException(originalErrorMessage);
				failure = Try.failure(exception);
			}

			@Test
			@DisplayName("Should throw RuntimeException")
			void shouldThrowRuntimeException() {
				// Assert
				RuntimeException exception = assertThrows(RuntimeException.class, () -> failure.fold(
						i -> "Success: " + i,
						e -> {
							throw new RuntimeException(failureErrorMessage);
						}
				), "Should throw RuntimeException when failure function throws");

				assertEquals("Failure function threw an exception", exception.getMessage(),
						"Exception message should indicate failure function threw");
			}
		}

		/**
		 * Tests for null function parameters.
		 * <p>
		 * When either function parameter is null, a NullPointerException should be thrown.
		 */
		@Nested
		@DisplayName("When function parameters are null")
		class WhenFunctionParametersAreNull {

			private Try<Integer> success;
			private Try<Integer> failure;

			@BeforeEach
			void setUp() {
				// Arrange
				success = Try.success(42);
				failure = Try.failure(new RuntimeException("Error"));
			}

			@Test
			@DisplayName("Should throw NullPointerException for null success function")
			void shouldThrowNullPointerExceptionForNullSuccessFunction() {
				// Assert
				assertThrows(NullPointerException.class, () -> success.fold(
						null,
						e -> "Failure: " + e.getMessage()
				), "Should throw NullPointerException for null success function");
			}

			@Test
			@DisplayName("Should throw NullPointerException for null failure function")
			void shouldThrowNullPointerExceptionForNullFailureFunction() {
				// Assert
				assertThrows(NullPointerException.class, () -> success.fold(
						i -> "Success: " + i,
						null
				), "Should throw NullPointerException for null failure function");
			}

			@Test
			@DisplayName("Should throw NullPointerException for null success function with Failure")
			void shouldThrowNullPointerExceptionForNullSuccessFunctionWithFailure() {
				// Assert
				assertThrows(NullPointerException.class, () -> failure.fold(
						null,
						e -> "Failure: " + e.getMessage()
				), "Should throw NullPointerException for null success function with Failure");
			}

			@Test
			@DisplayName("Should throw NullPointerException for null failure function with Failure")
			void shouldThrowNullPointerExceptionForNullFailureFunctionWithFailure() {
				// Assert
				assertThrows(NullPointerException.class, () -> failure.fold(
						i -> "Success: " + i,
						null
				), "Should throw NullPointerException for null failure function with Failure");
			}
		}
	}
}
