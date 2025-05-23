# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-05-23

### Added

- New `andFinally` method to the `Try` interface for executing side effects regardless of success or failure
- Implementation of `andFinally` in both `Success` and `Failure` classes
- Documentation and usage examples for the new method

## [1.0.0] - 2025-05-23

### Added

- Initial release of the Scipio library
- Core `Try` interface with `Success` and `Failure` implementations
- Factory methods for creating `Try` instances: `of`, `success`, `failure`
- Transformation methods: `map`, `flatMap`
- Recovery methods: `recover`, `recoverWith`
- Conversion methods: `toOptional`
- Utility methods: `isSuccess`, `isFailure`, `get`
