# Scipio: A Try/Catch Monad Utility for Java

Scipio is a lightweight Java library that provides a functional approach to exception handling through a Try/Catch monad
implementation. It allows for more elegant, composable, and less error-prone code compared to traditional try-catch
blocks.

## Features

- **Functional Exception Handling**: Handle exceptions in a functional way without cluttering your code with try-catch
  blocks
- **Composable Operations**: Chain operations that might fail with map, flatMap, and recovery methods
- **Integration with Optional**: Seamless integration with Java's Optional class
- **Immutable and Thread-Safe**: All objects are immutable and thread-safe
- **Java 8 Compatible**: Works with Java 8 and above

## Installation

### Prerequisites

- Java 8 or higher
- Maven 3.6 or higher

It is planned that Scipio will be published to Maven Central.

## Basic Usage

### Creating a Try

```java
// Create a Success
Try<Integer> success = Try.success(42);

// Create a Failure
Try<Integer> failure = Try.failure(new RuntimeException("Something went wrong"));

// Wrap a potentially throwing operation
Try<Integer> result = Try.of(() -> Integer.parseInt("42"));
```

### Checking Success or Failure

```java
if (result.isSuccess()) {
    System.out.println("Operation succeeded");
}

if (result.isFailure()) {
    System.out.println("Operation failed");
}
```

### Transforming Values

```java
// Using map to transform a Success
Try<String> stringResult = result.map(n -> "The number is: " + n);

// Using flatMap to chain operations that might fail
Try<Integer> doubledResult = result.flatMap(n -> Try.of(() -> n * 2));

// Using fold to transform a Try into a value of a different type
String foldResult = result.fold(
    value -> "Success: " + value,
    exception -> "Failure: " + exception.getMessage()
);
```

### Using Fold for Unified Handling

```java
// Basic usage - handle both success and failure cases in one operation
String message = Try.of(() -> "42")
    .fold(
        value -> "Success: " + value,
        exception -> "Failure: " + exception.getMessage()
    );
// the message will be "Success: 42"

// Converting to different types
Integer number = Try.of(() -> "42")
    .fold(
        Integer::parseInt,
        exception -> -1
    );
// the number will be 42

// Combining with other methods
Integer combined = Try.of(() -> "39")
    .map(s -> s + "0")
    .fold(
        Integer::parseInt,
        exception -> -1
    );
// combined will be 390
```

### Recovering from Failures

```java
// Recover with a default value
Try<Integer> recovered = result.recover(ex -> - 1);

// Recover with another Try
Try<Integer> recoveredWithTry = result.recoverWith(ex -> {
	if (ex instanceof NumberFormatException) {
		return Try.success(0);
	} else {
		return Try.failure(new RuntimeException("Unexpected error", ex));
	}
});
```

### Executing Side Effects with andFinally

```java
// Execute a side effect regardless of success or failure
Try<Integer> resultWithSideEffect = result.andFinally(() -> System.out.println("Operation completed"));

// Useful for resource cleanup
AtomicInteger resourceClosed = new AtomicInteger(0);
Try<String> resourceResult = Try.of(() -> {
    // Use resource
    return "Resource used successfully";
}).andFinally(() -> resourceClosed.incrementAndGet());

// Chain multiple andFinally calls
Try<String> multipleActions = Try.of(() -> "Hello")
    .andFinally(() -> System.out.println("First action"))
    .andFinally(() -> System.out.println("Second action"))
    .andFinally(() -> System.out.println("Third action"));

// Combine with other methods
Try<Integer> combined = Try.of(() -> "42")
    .map(Integer::parseInt)
    .andFinally(() -> System.out.println("Parsing completed"))
    .recover(ex -> -1);
```

### Integration with Optional

```java
// Convert a Try to Optional
Optional<Integer> optional = result.toOptional();

// Use Optional methods with Try
String message = result
		.toOptional()
		.map(n -> n * 2)
		.map(n -> "The result is: " + n)
		.orElse("No result");
```

## License

This project is licensed under the MIT License — see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
