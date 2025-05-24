package io.github.borrelunde.scipio.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the peek methods of the {@link Try} interface.
 * <p>
 * This test class is organized into logical groups based on the different peek methods:
 * 1. WhenUsingPeekSuccessWithSuccess - Tests for peekSuccess with Success instances
 * 2. WhenUsingPeekSuccessWithFailure - Tests for peekSuccess with Failure instances
 * 3. WhenUsingPeekFailureWithSuccess - Tests for peekFailure with Success instances
 * 4. WhenUsingPeekFailureWithFailure - Tests for peekFailure with Failure instances
 * 5. WhenUsingPeekWithSuccess - Tests for peek with Success instances
 * 6. WhenUsingPeekWithFailure - Tests for peek with Failure instances
 * 7. WhenHandlingEdgeCases - Tests for edge cases and integration with other methods
 * <p>
 * Each method is tested with both Success and Failure inputs and with various scenarios
 * including normal execution and exception handling.
 *
 * @author Børre A. Opedal Lunde
 * @since 1.1.0
 */
@DisplayName("Peek Methods Tests")
class PeekTest {

    @Nested
    @DisplayName("When using peekSuccess with Success")
    class WhenUsingPeekSuccessWithSuccess {

        private final String value = "test";
        private final Try<String> success = Try.success(value);

        @Test
        @DisplayName("Should execute the consumer")
        void shouldExecuteTheConsumer() {
            // Arrange
            AtomicBoolean consumerExecuted = new AtomicBoolean(false);

            // Act
            success.peekSuccess(v -> consumerExecuted.set(true));

            // Assert
            assertTrue(consumerExecuted.get(), "Consumer should be executed");
        }

        @Test
        @DisplayName("Should return original Success when consumer completes normally")
        void shouldReturnOriginalSuccessWhenConsumerCompletesNormally() {
            // Arrange
            AtomicBoolean consumerExecuted = new AtomicBoolean(false);

            // Act
            Try<String> result = success.peekSuccess(v -> consumerExecuted.set(true));

            // Assert
            assertTrue(consumerExecuted.get(), "Consumer should be executed");
            assertSame(success, result, "Should return the original Success instance");
        }

        @Test
        @DisplayName("Should return Failure when consumer throws exception")
        void shouldReturnFailureWhenConsumerThrowsException() {
            // Arrange
            RuntimeException exception = new RuntimeException("Consumer exception");

            // Act
            Try<String> result = success.peekSuccess(v -> {
                throw exception;
            });

            // Assert
            assertTrue(result.isFailure(), "Result should be a Failure");
            try {
                result.get();
                fail("Should throw exception");
            } catch (Exception e) {
                assertSame(exception, e, "Exception should be the one thrown by consumer");
            }
            assertSame(exception, ((Failure<String>) result).getException(), "Failure should contain the thrown exception");
        }
    }

    @Nested
    @DisplayName("When using peekSuccess with Failure")
    class WhenUsingPeekSuccessWithFailure {

        private final Exception exception = new RuntimeException("Test exception");
        private final Try<String> failure = Try.failure(exception);

        @Test
        @DisplayName("Should not execute the consumer")
        void shouldNotExecuteTheConsumer() {
            // Arrange
            AtomicBoolean consumerExecuted = new AtomicBoolean(false);

            // Act
            failure.peekSuccess(v -> consumerExecuted.set(true));

            // Assert
            assertFalse(consumerExecuted.get(), "Consumer should not be executed");
        }

        @Test
        @DisplayName("Should return the original Failure")
        void shouldReturnTheOriginalFailure() {
            // Arrange
            AtomicBoolean consumerExecuted = new AtomicBoolean(false);

            // Act
            Try<String> result = failure.peekSuccess(v -> consumerExecuted.set(true));

            // Assert
            assertFalse(consumerExecuted.get(), "Consumer should not be executed");
            assertSame(failure, result, "Should return the original Failure instance");
        }
    }

    @Nested
    @DisplayName("When using peekFailure with Success")
    class WhenUsingPeekFailureWithSuccess {

        private final String value = "test";
        private final Try<String> success = Try.success(value);

        @Test
        @DisplayName("Should not execute the consumer")
        void shouldNotExecuteTheConsumer() {
            // Arrange
            AtomicBoolean consumerExecuted = new AtomicBoolean(false);

            // Act
            success.peekFailure(e -> consumerExecuted.set(true));

            // Assert
            assertFalse(consumerExecuted.get(), "Consumer should not be executed");
        }

        @Test
        @DisplayName("Should return the original Success")
        void shouldReturnTheOriginalSuccess() {
            // Arrange
            AtomicBoolean consumerExecuted = new AtomicBoolean(false);

            // Act
            Try<String> result = success.peekFailure(e -> consumerExecuted.set(true));

            // Assert
            assertFalse(consumerExecuted.get(), "Consumer should not be executed");
            assertSame(success, result, "Should return the original Success instance");
        }
    }

    @Nested
    @DisplayName("When using peekFailure with Failure")
    class WhenUsingPeekFailureWithFailure {

        private final Exception exception = new RuntimeException("Test exception");
        private final Try<String> failure = Try.failure(exception);

        @Test
        @DisplayName("Should execute the consumer")
        void shouldExecuteTheConsumer() {
            // Arrange
            AtomicBoolean consumerExecuted = new AtomicBoolean(false);

            // Act
            failure.peekFailure(e -> consumerExecuted.set(true));

            // Assert
            assertTrue(consumerExecuted.get(), "Consumer should be executed");
        }

        @Test
        @DisplayName("Should return original Failure when consumer completes normally")
        void shouldReturnOriginalFailureWhenConsumerCompletesNormally() {
            // Arrange
            AtomicBoolean consumerExecuted = new AtomicBoolean(false);

            // Act
            Try<String> result = failure.peekFailure(e -> consumerExecuted.set(true));

            // Assert
            assertTrue(consumerExecuted.get(), "Consumer should be executed");
            assertSame(failure, result, "Should return the original Failure instance");
        }

        @Test
        @DisplayName("Should return new Failure when consumer throws exception")
        void shouldReturnNewFailureWhenConsumerThrowsException() {
            // Arrange
            RuntimeException consumerException = new RuntimeException("Consumer exception");

            // Act
            Try<String> result = failure.peekFailure(e -> {
                throw consumerException;
            });

            // Assert
            assertTrue(result.isFailure(), "Result should be a Failure");
            assertSame(consumerException, ((Failure<String>) result).getException(), "Failure should contain the thrown exception");
        }
    }

    @Nested
    @DisplayName("When using peek with Success")
    class WhenUsingPeekWithSuccess {

        private final String value = "test";
        private final Try<String> success = Try.success(value);

        @Test
        @DisplayName("Should execute only the success consumer")
        void shouldExecuteOnlyTheSuccessConsumer() {
            // Arrange
            AtomicBoolean successConsumerExecuted = new AtomicBoolean(false);
            AtomicBoolean failureConsumerExecuted = new AtomicBoolean(false);

            // Act
            success.peek(
                    v -> successConsumerExecuted.set(true),
                    e -> failureConsumerExecuted.set(true)
            );

            // Assert
            assertTrue(successConsumerExecuted.get(), "Success consumer should be executed");
            assertFalse(failureConsumerExecuted.get(), "Failure consumer should not be executed");
        }

        @Test
        @DisplayName("Should return original Success when consumer completes normally")
        void shouldReturnOriginalSuccessWhenConsumerCompletesNormally() {
            // Arrange
            AtomicBoolean successConsumerExecuted = new AtomicBoolean(false);

            // Act
            Try<String> result = success.peek(
                    v -> successConsumerExecuted.set(true),
                    e -> fail("Failure consumer should not be executed")
            );

            // Assert
            assertTrue(successConsumerExecuted.get(), "Success consumer should be executed");
            assertSame(success, result, "Should return the original Success instance");
        }

        @Test
        @DisplayName("Should return Failure when success consumer throws exception")
        void shouldReturnFailureWhenSuccessConsumerThrowsException() {
            // Arrange
            RuntimeException exception = new RuntimeException("Consumer exception");

            // Act
            Try<String> result = success.peek(
                    v -> { throw exception; },
                    e -> fail("Failure consumer should not be executed")
            );

            // Assert
            assertTrue(result.isFailure(), "Result should be a Failure");
            Exception resultException = ((Failure<String>) result).getException();
            assertSame(exception, resultException, "Failure should contain the thrown exception");
        }
    }

    @Nested
    @DisplayName("When using peek with Failure")
    class WhenUsingPeekWithFailure {

        private final Exception exception = new RuntimeException("Test exception");
        private final Try<String> failure = Try.failure(exception);

        @Test
        @DisplayName("Should execute only the failure consumer")
        void shouldExecuteOnlyTheFailureConsumer() {
            // Arrange
            AtomicBoolean successConsumerExecuted = new AtomicBoolean(false);
            AtomicBoolean failureConsumerExecuted = new AtomicBoolean(false);

            // Act
            failure.peek(
                    v -> successConsumerExecuted.set(true),
                    e -> failureConsumerExecuted.set(true)
            );

            // Assert
            assertFalse(successConsumerExecuted.get(), "Success consumer should not be executed");
            assertTrue(failureConsumerExecuted.get(), "Failure consumer should be executed");
        }

        @Test
        @DisplayName("Should return original Failure when consumer completes normally")
        void shouldReturnOriginalFailureWhenConsumerCompletesNormally() {
            // Arrange
            AtomicBoolean failureConsumerExecuted = new AtomicBoolean(false);

            // Act
            Try<String> result = failure.peek(
                    v -> fail("Success consumer should not be executed"),
                    e -> failureConsumerExecuted.set(true)
            );

            // Assert
            assertTrue(failureConsumerExecuted.get(), "Failure consumer should be executed");
            assertSame(failure, result, "Should return the original Failure instance");
        }

        @Test
        @DisplayName("Should return new Failure when failure consumer throws exception")
        void shouldReturnNewFailureWhenFailureConsumerThrowsException() {
            // Arrange
            RuntimeException consumerException = new RuntimeException("Consumer exception");

            // Act
            Try<String> result = failure.peek(
                    v -> fail("Success consumer should not be executed"),
                    e -> { throw consumerException; }
            );

            // Assert
            assertTrue(result.isFailure(), "Result should be a Failure");
            assertSame(consumerException, ((Failure<String>) result).getException(), "Failure should contain the thrown exception");
        }
    }

    @Nested
    @DisplayName("When handling edge cases")
    class WhenHandlingEdgeCases {

        @Test
        @DisplayName("Should throw NullPointerException when consumer is null in peekSuccess")
        void shouldThrowNullPointerExceptionWhenConsumerIsNullInPeekSuccess() {
            // Arrange
            Try<String> success = Try.success("test");

            // Act & Assert
            assertThrows(NullPointerException.class, () -> success.peekSuccess(null),
                    "Should throw NullPointerException when consumer is null");
        }

        @Test
        @DisplayName("Should throw NullPointerException when consumer is null in peekFailure")
        void shouldThrowNullPointerExceptionWhenConsumerIsNullInPeekFailure() {
            // Arrange
            Try<String> failure = Try.failure(new RuntimeException("Test exception"));

            // Act & Assert
            assertThrows(NullPointerException.class, () -> failure.peekFailure(null),
                    "Should throw NullPointerException when consumer is null");
        }

        @Test
        @DisplayName("Should throw NullPointerException when success consumer is null in peek")
        void shouldThrowNullPointerExceptionWhenSuccessConsumerIsNullInPeek() {
            // Arrange
            Try<String> success = Try.success("test");

            // Act & Assert
            assertThrows(NullPointerException.class, () -> success.peek(null, e -> {}),
                    "Should throw NullPointerException when success consumer is null");
        }

        @Test
        @DisplayName("Should throw NullPointerException when failure consumer is null in peek")
        void shouldThrowNullPointerExceptionWhenFailureConsumerIsNullInPeek() {
            // Arrange
            Try<String> success = Try.success("test");

            // Act & Assert
            assertThrows(NullPointerException.class, () -> success.peek(v -> {}, null),
                    "Should throw NullPointerException when failure consumer is null");
        }

        @Test
        @DisplayName("Should execute multiple peek calls in sequence")
        void shouldExecuteMultiplePeekCallsInSequence() {
            // Arrange
            AtomicInteger counter = new AtomicInteger(0);
            Try<String> success = Try.success("test");

            // Act
            Try<String> result = success
                    .peekSuccess(v -> counter.incrementAndGet())
                    .peekSuccess(v -> counter.incrementAndGet())
                    .peekSuccess(v -> counter.incrementAndGet());

            // Assert
            assertEquals(3, counter.get(), "All three consumers should be executed");
            assertSame(success, result, "Should return the original Success instance");
        }

        @Test
        @DisplayName("Should integrate with map method")
        void shouldIntegrateWithMapMethod() throws Exception {
            // Arrange
            AtomicBoolean beforeMapExecuted = new AtomicBoolean(false);
            AtomicBoolean afterMapExecuted = new AtomicBoolean(false);
            Try<String> success = Try.success("test");

            // Act
            Try<Integer> result = success
                    .peekSuccess(v -> beforeMapExecuted.set(true))
                    .map(String::length)
                    .peekSuccess(v -> afterMapExecuted.set(true));

            // Assert
            assertTrue(beforeMapExecuted.get(), "Consumer before map should be executed");
            assertTrue(afterMapExecuted.get(), "Consumer after map should be executed");
            assertTrue(result.isSuccess(), "Result should be a Success");
            assertEquals(4, result.get(), "Result should contain the mapped value");
        }

        @Test
        @DisplayName("Should integrate with recover method")
        void shouldIntegrateWithRecoverMethod() throws Exception {
            // Arrange
            AtomicBoolean beforeRecoverExecuted = new AtomicBoolean(false);
            AtomicBoolean afterRecoverExecuted = new AtomicBoolean(false);
            Try<String> failure = Try.failure(new RuntimeException("Test exception"));

            // Act
            Try<String> result = failure
                    .peekFailure(e -> beforeRecoverExecuted.set(true))
                    .recover(e -> "recovered")
                    .peekSuccess(v -> afterRecoverExecuted.set(true));

            // Assert
            assertTrue(beforeRecoverExecuted.get(), "Consumer before recover should be executed");
            assertTrue(afterRecoverExecuted.get(), "Consumer after recover should be executed");
            assertTrue(result.isSuccess(), "Result should be a Success");
            assertEquals("recovered", result.get(), "Result should contain the recovered value");
        }

        @Test
        @DisplayName("Should integrate with andFinally method")
        void shouldIntegrateWithAndFinallyMethod() {
            // Arrange
            AtomicBoolean peekExecuted = new AtomicBoolean(false);
            AtomicBoolean finallyExecuted = new AtomicBoolean(false);
            Try<String> success = Try.success("test");

            // Act
            Try<String> result = success
                    .peekSuccess(v -> peekExecuted.set(true))
                    .andFinally(() -> finallyExecuted.set(true));

            // Assert
            assertTrue(peekExecuted.get(), "Peek consumer should be executed");
            assertTrue(finallyExecuted.get(), "Finally action should be executed");
            assertSame(success, result, "Should return the original Success instance");
        }
    }
}
