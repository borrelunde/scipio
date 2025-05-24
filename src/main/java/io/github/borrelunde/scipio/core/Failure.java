package io.github.borrelunde.scipio.core;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of {@link Try} representing a failed computation.
 *
 * @param <ValueType> the type of the value that would have been returned if the
 *                    computation had succeeded
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
public final class Failure<ValueType> implements Try<ValueType> {

	private final Exception exception;

	/**
	 * Creates a new Failure containing the given exception.
	 *
	 * @param exception the exception
	 */
	Failure(final Exception exception) {
		this.exception = Objects.requireNonNull(exception);
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public boolean isFailure() {
		return true;
	}

	@Override
	public ValueType get() throws Exception {
		throw exception;
	}

	@Override
	public Optional<ValueType> toOptional() {
		return Optional.empty();
	}

	@Override
	public <ResultType> Try<ResultType> map(final Function<? super ValueType, ? extends ResultType> mapper) {
		return Try.failure(exception);
	}

	@Override
	public <ResultType> Try<ResultType> flatMap(final Function<? super ValueType, ? extends Try<ResultType>> mapper) {
		return Try.failure(exception);
	}

	@Override
	public Try<ValueType> recover(final Function<? super Exception, ? extends ValueType> recovery) {
		Objects.requireNonNull(recovery, "Recovery function cannot be null");
		try {
			return Try.success(recovery.apply(exception));
		} catch (Exception e) {
			return Try.failure(e);
		}
	}

	@Override
	public Try<ValueType> recoverWith(final Function<? super Exception, ? extends Try<ValueType>> recovery) {
		Objects.requireNonNull(recovery, "Recovery function cannot be null");
		try {
			return recovery.apply(exception);
		} catch (Exception e) {
			return Try.failure(e);
		}
	}

	@Override
	public Try<ValueType> andFinally(final Runnable action) {
		Objects.requireNonNull(action, "Action cannot be null");
		try {
			action.run();
			return this;
		} catch (Exception e) {
			return Try.failure(e);
		}
	}

	@Override
	public <ResultType> ResultType fold(
			final Function<? super ValueType, ? extends ResultType> successFunction,
			final Function<? super Exception, ? extends ResultType> failureFunction) {
		Objects.requireNonNull(successFunction, "Success function cannot be null");
		Objects.requireNonNull(failureFunction, "Failure function cannot be null");
		try {
			return failureFunction.apply(exception);
		} catch (Exception e) {
			throw new RuntimeException("Failure function threw an exception", e);
		}
	}

	/**
	 * Returns the exception that caused this failure.
	 *
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Failure<?> failure = (Failure<?>) o;
		return Objects.equals(exception, failure.exception);
	}

	@Override
	public int hashCode() {
		return Objects.hash(exception);
	}

	@Override
	public String toString() {
		return "Failure(" + exception + ")";
	}
}
