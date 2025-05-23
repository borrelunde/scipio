package io.github.borrelunde.scipio.core;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of {@link Try} representing a successful computation.
 *
 * @param <ValueType> the type of the value
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
public final class Success<ValueType> implements Try<ValueType> {

	private final ValueType value;

	/**
	 * Creates a new Success containing the given value.
	 *
	 * @param value the value
	 */
	Success(final ValueType value) {
		this.value = Objects.requireNonNull(value);
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isFailure() {
		return false;
	}

	@Override
	public ValueType get() {
		return value;
	}

	@Override
	public Optional<ValueType> toOptional() {
		return Optional.of(value);
	}

	@Override
	public <ResultType> Try<ResultType> map(final Function<? super ValueType, ? extends ResultType> mapper) {
		Objects.requireNonNull(mapper, "Mapper cannot be null");
		try {
			return Try.success(mapper.apply(value));
		} catch (Exception e) {
			return Try.failure(e);
		}
	}

	@Override
	public <ResultType> Try<ResultType> flatMap(final Function<? super ValueType, ? extends Try<ResultType>> mapper) {
		Objects.requireNonNull(mapper, "Mapper cannot be null");
		try {
			return mapper.apply(value);
		} catch (Exception e) {
			return Try.failure(e);
		}
	}

	@Override
	public Try<ValueType> recover(final Function<? super Exception, ? extends ValueType> recovery) {
		return this;
	}

	@Override
	public Try<ValueType> recoverWith(final Function<? super Exception, ? extends Try<ValueType>> recovery) {
		return this;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Success<?> success = (Success<?>) o;
		return Objects.equals(value, success.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "Success(" + value + ")";
	}
}
