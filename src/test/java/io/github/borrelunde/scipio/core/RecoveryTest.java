package io.github.borrelunde.scipio.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the recovery methods of the {@link Try} interface.
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
@DisplayName("Recovery Methods Tests")
class RecoveryTest {

    @Test
    @DisplayName("Should not recover Success")
    void shouldNotRecoverSuccess() throws Exception {
        // Arrange
        Try<Integer> success = Try.success(42);

        // Act
        Try<Integer> result = success.recover(ex -> -1);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(42, result.get());
    }

    @Test
    @DisplayName("Should recover Failure")
    void shouldRecoverFailure() throws Exception {
        // Arrange
        Exception exception = new RuntimeException("Original error");
        Try<Integer> failure = Try.failure(exception);

        // Act
        Try<Integer> result = failure.recover(ex -> -1);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(-1, result.get());
    }

    @Test
    @DisplayName("Should propagate exception when recovery throws")
    void shouldPropagateExceptionWhenRecoveryThrows() {
        // Arrange
        Exception originalException = new RuntimeException("Original error");
        Try<Integer> failure = Try.failure(originalException);

        // Act
        Try<Integer> result = failure.recover(ex -> {
            throw new RuntimeException("Recovery error");
        });

        // Assert
        assertTrue(result.isFailure());
        Exception resultException = assertThrows(Exception.class, result::get);
        assertEquals("Recovery error", resultException.getMessage());
    }

    @Test
    @DisplayName("Should not recoverWith Success")
    void shouldNotRecoverWithSuccess() throws Exception {
        // Arrange
        Try<Integer> success = Try.success(42);

        // Act
        Try<Integer> result = success.recoverWith(ex -> Try.success(-1));

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(42, result.get());
    }

    @Test
    @DisplayName("Should recoverWith Failure to Success")
    void shouldRecoverWithFailureToSuccess() throws Exception {
        // Arrange
        Exception exception = new RuntimeException("Original error");
        Try<Integer> failure = Try.failure(exception);

        // Act
        Try<Integer> result = failure.recoverWith(ex -> Try.success(-1));

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(-1, result.get());
    }

    @Test
    @DisplayName("Should recoverWith Failure to Failure")
    void shouldRecoverWithFailureToFailure() {
        // Arrange
        Exception originalException = new RuntimeException("Original error");
        Exception recoveryException = new RuntimeException("Recovery error");
        Try<Integer> failure = Try.failure(originalException);

        // Act
        Try<Integer> result = failure.recoverWith(ex -> Try.failure(recoveryException));

        // Assert
        assertTrue(result.isFailure());
        Exception resultException = assertThrows(Exception.class, result::get);
        assertEquals(recoveryException, resultException);
    }

    @Test
    @DisplayName("Should propagate exception when recoverWith throws")
    void shouldPropagateExceptionWhenRecoverWithThrows() {
        // Arrange
        Exception originalException = new RuntimeException("Original error");
        Try<Integer> failure = Try.failure(originalException);

        // Act
        Try<Integer> result = failure.recoverWith(ex -> {
            throw new RuntimeException("RecoverWith error");
        });

        // Assert
        assertTrue(result.isFailure());
        Exception resultException = assertThrows(Exception.class, result::get);
        assertEquals("RecoverWith error", resultException.getMessage());
    }
}