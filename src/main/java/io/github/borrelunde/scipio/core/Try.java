package io.github.borrelunde.scipio.core;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a computation that may either result in a value or an exception.
 * <p>
 * This is the main interface of the Try monad, which provides a functional approach
 * to exception handling. It allows for more elegant and composable error handling
 * compared to traditional try-catch blocks.
 *
 * @param <ValueType> the type of the value in case of success
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
public interface Try<ValueType> {

	/**
	 * Returns whether this is a Success.
	 *
	 * @return true if this is a Success, false otherwise
	 */
	boolean isSuccess();

	/**
	 * Returns whether this is a Failure.
	 *
	 * @return true if this is a Failure, false otherwise
	 */
	boolean isFailure();

	/**
	 * Returns the value if this is a Success, otherwise throws the exception.
	 *
	 * @return the value if this is a Success
	 * @throws Exception if this is a Failure
	 */
	ValueType get() throws Exception;

	/**
	 * Converts this Try to an Optional. If this is a Success, returns an Optional
	 * containing the value, otherwise returns an empty Optional.
	 *
	 * @return an Optional containing the value if this is a Success, otherwise an empty Optional
	 */
	Optional<ValueType> toOptional();

	/**
	 * Maps the value of this Try if it is a Success, otherwise returns this Failure.
	 *
	 * @param mapper the function to apply to the value
	 * @param <ResultType> the type of the result
	 * @return a new Try with the mapped value if this is a Success, otherwise this Failure
	 */
	<ResultType> Try<ResultType> map(final Function<? super ValueType, ? extends ResultType> mapper);

	/**
	 * Maps the value of this Try to a new Try if it is a Success, otherwise returns this Failure.
	 *
	 * @param mapper the function to apply to the value
	 * @param <ResultType> the type of the result
	 * @return the new Try if this is a Success, otherwise this Failure
	 */
	<ResultType> Try<ResultType> flatMap(final Function<? super ValueType, ? extends Try<ResultType>> mapper);

	/**
	 * Recovers from a Failure by applying the given function to the exception.
	 *
	 * @param recovery the function to apply to the exception
	 * @return a new Success with the recovered value if this is a Failure, otherwise this Success
	 */
	Try<ValueType> recover(final Function<? super Exception, ? extends ValueType> recovery);

	/**
	 * Recovers from a Failure by applying the given function to the exception.
	 *
	 * @param recovery the function to apply to the exception
	 * @return the recovered Try if this is a Failure, otherwise this Success
	 */
	Try<ValueType> recoverWith(final Function<? super Exception, ? extends Try<ValueType>> recovery);

	/**
	 * Executes the given action regardless of whether this Try is a Success or Failure.
	 * Similar to a finally block in a try-catch-finally statement.
	 *
	 * @param action the action to execute
	 * @return this Try instance if the action completes normally, otherwise a Failure containing the exception thrown by the action
	 */
	Try<ValueType> andFinally(final Runnable action);

	/**
	 * Transforms this Try into a value of type ResultType by applying either the success function
	 * to the value if this is a Success, or the failure function to the exception if this is a Failure.
	 *
	 * @param <ResultType> the type of the result
	 * @param successFunction the function to apply if this is a Success
	 * @param failureFunction the function to apply if this is a Failure
	 * @return the result of applying the appropriate function
	 */
	<ResultType> ResultType fold(
			final Function<? super ValueType, ? extends ResultType> successFunction,
			final Function<? super Exception, ? extends ResultType> failureFunction);

	/**
	 * Creates a new Try by applying the given supplier.
	 *
	 * @param supplier the supplier to apply
	 * @param <ValueType> the type of the value
	 * @return a Success containing the value if the supplier succeeds, otherwise a Failure
	 */
	static <ValueType> Try<ValueType> of(final Supplier<? extends ValueType> supplier) {
		Objects.requireNonNull(supplier, "Supplier cannot be null");
		try {
			return success(supplier.get());
		} catch (Exception e) {
			return failure(e);
		}
	}

	/**
	 * Creates a new Success containing the given value.
	 *
	 * @param value the value
	 * @param <ValueType> the type of the value
	 * @return a new Success containing the value
	 */
	static <ValueType> Try<ValueType> success(final ValueType value) {
		return new Success<>(value);
	}

	/**
	 * Creates a new Failure containing the given exception.
	 *
	 * @param exception the exception
	 * @param <ValueType> the type of the value
	 * @return a new Failure containing the exception
	 */
	static <ValueType> Try<ValueType> failure(final Exception exception) {
		return new Failure<>(exception);
	}
}
