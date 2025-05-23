package io.github.borrelunde.scipio.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the andFinally method of the {@link Try} interface.
 * <p>
 * This test class is organized into logical groups based on the different scenarios:
 * 1. Using andFinally with Success instances
 * 2. Using andFinally with Failure instances
 * 3. Edge cases like null actions and chaining multiple andFinally calls
 * <p>
 * Each scenario is implemented as a nested class to provide clear separation of concerns
 * and to allow for focused testing of specific behaviours.
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
@DisplayName("andFinally Method Tests")
class FinallyTest {

    /**
     * Tests for using andFinally with Success instances.
     * <p>
     * These tests verify that:
     * 1. The action is executed
     * 2. The original Success is returned when the action completes normally
     * 3. A Failure is returned when the action throws an exception
     */
    @Nested
    @DisplayName("When using andFinally with Success")
    class WhenUsingAndFinallyWithSuccess {

        @Test
        @DisplayName("Should execute the action")
        void shouldExecuteTheAction() {
            // Arrange
            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Try<String> success = Try.success("test");

            // Act
            success.andFinally(() -> actionExecuted.set(true));

            // Assert
            assertTrue(actionExecuted.get(), "Action should be executed");
        }

        @Test
        @DisplayName("Should return the original Success when action completes normally")
        void shouldReturnOriginalSuccessWhenActionCompletesNormally() {
            // Arrange
            Try<String> success = Try.success("test");

            // Act
            Try<String> result = success.andFinally(() -> {});

            // Assert
            assertTrue(result.isSuccess(), "Result should be a Success");
            assertSame(success, result, "Result should be the same instance as the original Success");
        }

        @Test
        @DisplayName("Should return Failure when action throws exception")
        void shouldReturnFailureWhenActionThrowsException() {
            // Arrange
            Try<String> success = Try.success("test");
            RuntimeException exception = new RuntimeException("Test exception");

            // Act
            Try<String> result = success.andFinally(() -> {
                throw exception;
            });

            // Assert
            assertTrue(result.isFailure(), "Result should be a Failure");
            assertThrows(RuntimeException.class, result::get, "Getting value from Failure should throw exception");
            Exception thrownException = assertThrows(Exception.class, result::get);
            assertSame(exception, thrownException, "Exception should be the same instance as the thrown exception");
        }
    }

    /**
     * Tests for using andFinally with Failure instances.
     * <p>
     * These tests verify that:
     * 1. The action is executed
     * 2. The original Failure is returned when the action completes normally
     * 3. A new Failure is returned when the action throws an exception
     */
    @Nested
    @DisplayName("When using andFinally with Failure")
    class WhenUsingAndFinallyWithFailure {

        @Test
        @DisplayName("Should execute the action")
        void shouldExecuteTheAction() {
            // Arrange
            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Exception originalException = new RuntimeException("Original exception");
            Try<String> failure = Try.failure(originalException);

            // Act
            failure.andFinally(() -> actionExecuted.set(true));

            // Assert
            assertTrue(actionExecuted.get(), "Action should be executed");
        }

        @Test
        @DisplayName("Should return the original Failure when action completes normally")
        void shouldReturnOriginalFailureWhenActionCompletesNormally() {
            // Arrange
            Exception originalException = new RuntimeException("Original exception");
            Try<String> failure = Try.failure(originalException);

            // Act
            Try<String> result = failure.andFinally(() -> {});

            // Assert
            assertTrue(result.isFailure(), "Result should be a Failure");
            assertSame(failure, result, "Result should be the same instance as the original Failure");
            Exception resultException = assertThrows(Exception.class, result::get);
            assertSame(originalException, resultException, "Exception should be the same as the original exception");
        }

        @Test
        @DisplayName("Should return new Failure when action throws exception")
        void shouldReturnNewFailureWhenActionThrowsException() {
            // Arrange
            Exception originalException = new RuntimeException("Original exception");
            RuntimeException newException = new RuntimeException("New exception");
            Try<String> failure = Try.failure(originalException);

            // Act
            Try<String> result = failure.andFinally(() -> {
                throw newException;
            });

            // Assert
            assertTrue(result.isFailure(), "Result should be a Failure");
            assertNotSame(failure, result, "Result should not be the same instance as the original Failure");
            Exception resultException = assertThrows(Exception.class, result::get);
            assertSame(newException, resultException, "Exception should be the new exception");
        }
    }

    /**
     * Tests for edge cases of the andFinally method.
     * <p>
     * These tests verify:
     * 1. Behaviour with null action
     * 2. Chaining multiple andFinally calls
     * 3. Integration with other Try methods (map, flatMap, recover, etc.)
     */
    @Nested
    @DisplayName("When handling edge cases")
    class WhenHandlingEdgeCases {

        @Test
        @DisplayName("Should throw NullPointerException when action is null with Success")
        void shouldThrowNullPointerExceptionWhenActionIsNullWithSuccess() {
            // Arrange
            Try<String> success = Try.success("test");

            // Act & Assert
            assertThrows(NullPointerException.class, () -> success.andFinally(null),
                    "Should throw NullPointerException when action is null");
        }

        @Test
        @DisplayName("Should throw NullPointerException when action is null with Failure")
        void shouldThrowNullPointerExceptionWhenActionIsNullWithFailure() {
            // Arrange
            Try<String> failure = Try.failure(new RuntimeException());

            // Act & Assert
            assertThrows(NullPointerException.class, () -> failure.andFinally(null),
                    "Should throw NullPointerException when action is null");
        }

        @Test
        @DisplayName("Should execute multiple andFinally calls in sequence")
        void shouldExecuteMultipleAndFinallyCallsInSequence() {
            // Arrange
            AtomicInteger counter = new AtomicInteger(0);
            Try<String> success = Try.success("test");

            // Act
            Try<String> result = success
                    .andFinally(() -> counter.compareAndSet(0, 1))
                    .andFinally(() -> counter.compareAndSet(1, 2))
                    .andFinally(() -> counter.compareAndSet(2, 3));

            // Assert
            assertEquals(3, counter.get(), "All actions should be executed in sequence");
            assertTrue(result.isSuccess(), "Result should be a Success");
        }

        @Test
        @DisplayName("Should continue execution chain even when an action throws exception")
        void shouldContinueExecutionChainEvenWhenActionThrowsException() {
            // Arrange
            AtomicInteger counter = new AtomicInteger(0);
            Try<String> success = Try.success("test");

            // Act
            Try<String> result = success
                    .andFinally(() -> counter.compareAndSet(0, 1))
                    .andFinally(() -> {
                        counter.compareAndSet(1, 2);
                        throw new RuntimeException("Test exception");
                    })
                    .andFinally(() -> counter.compareAndSet(2, 3)); // This will still execute

            // Assert
            assertEquals(3, counter.get(), "All actions should be executed");
            assertTrue(result.isFailure(), "Result should be a Failure");
        }

        @Test
        @DisplayName("Should integrate with map method")
        void shouldIntegrateWithMapMethod() {
            // Arrange
            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Try<Integer> success = Try.success(42);

            // Act
            Try<String> result = success
                    .map(Object::toString)
                    .andFinally(() -> actionExecuted.set(true));

            // Assert
            assertTrue(actionExecuted.get(), "Action should be executed");
            assertTrue(result.isSuccess(), "Result should be a Success");
            try {
                assertEquals("42", result.get(), "Result should contain mapped value");
            } catch (Exception e) {
                fail("Should not throw exception: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should integrate with recover method")
        void shouldIntegrateWithRecoverMethod() {
            // Arrange
            AtomicBoolean actionExecuted = new AtomicBoolean(false);
            Try<String> failure = Try.failure(new RuntimeException("Test exception"));

            // Act
            Try<String> result = failure
                    .recover(ex -> "Recovered")
                    .andFinally(() -> actionExecuted.set(true));

            // Assert
            assertTrue(actionExecuted.get(), "Action should be executed");
            assertTrue(result.isSuccess(), "Result should be a Success");
            try {
                assertEquals("Recovered", result.get(), "Result should contain recovered value");
            } catch (Exception e) {
                fail("Should not throw exception: " + e.getMessage());
            }
        }
    }
}
