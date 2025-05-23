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
}
