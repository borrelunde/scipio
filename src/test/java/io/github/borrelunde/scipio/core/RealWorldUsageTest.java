package io.github.borrelunde.scipio.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for real-world usage scenarios of the {@link Try} interface.
 * <p>
 * This test class demonstrates how the Try interface can be used in practical scenarios:
 * 1. External Service Integration - Shows how Try can simplify error handling when working with external services
 * 2. Resource Management - Demonstrates using Try for proper resource clean-up
 * <p>
 * Each scenario is implemented as a nested class to provide clear separation of concerns
 * and to allow for focused testing of specific behaviours.
 *
 * @author Børre A. Opedal Lunde
 * @since 1.0.0
 */
@DisplayName("Real-World Usage Tests")
class RealWorldUsageTest {

	/**
	 * Simulated external service for user operations.
	 * <p>
	 * This class represents an external service that can throw exceptions,
	 * simulating real-world API calls that may fail.
	 */
	private static class UserService {
		public User findById(int id) throws IOException {
			if (id <= 0) {
				throw new IOException("Invalid user ID");
			}
			return new User(id, "User " + id);
		}

		public List<User> findFriends(User user) throws IOException {
			if (user == null) {
				throw new IOException("User cannot be null");
			}
			return Arrays.asList(
					new User(user.id + 1, "Friend " + (user.id + 1)),
					new User(user.id + 2, "Friend " + (user.id + 2))
			);
		}
	}

	/**
	 * Simple User model class.
	 */
	private static class User {
		private final int id;
		private final String name;

		public User(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * Tests for using Try with external service operations.
	 * <p>
	 * These tests demonstrate how Try can be used to handle errors when working with
	 * external services that may throw exceptions. They show different scenarios:
	 * - Successful chained operations
	 * - Handling failures in the chain
	 * - Recovering from failures
	 */
	@Nested
	@DisplayName("When working with external services")
	class WhenWorkingWithExternalServices {

		private UserService userService;

		@BeforeEach
		void setUp() {
			userService = new UserService();
		}

		/**
		 * Finds a user by ID using the user service.
		 *
		 * @param userId the ID of the user to find
		 * @return the found user
		 * @throws RuntimeException if an IOException occurs
		 */
		private User findUserById(int userId) {
			try {
				return userService.findById(userId);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Finds friends for a user using the user service.
		 *
		 * @param user the user whose friends to find
		 * @return a Try containing the list of friends
		 */
		private Try<List<User>> findFriendsByUser(User user) {
			return Try.of(() -> {
				try {
					return userService.findFriends(user);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}

		/**
		 * Extracts friend names from a list of users.
		 *
		 * @param friends the list of users
		 * @return a list of friend names
		 */
		private List<String> extractFriendNames(List<User> friends) {
			List<String> names = new ArrayList<>();
			for (User friend : friends) {
				names.add(friend.name);
			}
			return names;
		}

		/**
		 * Tests for successful chained operations with external services.
		 */
		@Nested
		@DisplayName("When operations succeed")
		class WhenOperationsSucceed {

			private int userId;
			private Try<List<String>> result;

			@BeforeEach
			void setUp() {
				// Arrange
				userId = 1;

				// Act
				result = Try.of(() -> findUserById(userId))
						.flatMap(WhenWorkingWithExternalServices.this::findFriendsByUser)
						.map(WhenWorkingWithExternalServices.this::extractFriendNames);
			}

			@Test
			@DisplayName("Should result in Success")
			void shouldResultInSuccess() {
				// Assert
				assertTrue(result.isSuccess(), "Result should be a Success when operations succeed");
			}

			@Test
			@DisplayName("Should contain correct number of friends")
			void shouldContainCorrectNumberOfFriends() throws Exception {
				// Assert
				List<String> friendNames = result.get();
				assertEquals(2, friendNames.size(), "Should find exactly 2 friends");
			}

			@Test
			@DisplayName("Should contain correct friend names")
			void shouldContainCorrectFriendNames() throws Exception {
				// Assert
				List<String> friendNames = result.get();
				assertEquals("Friend 2", friendNames.get(0), "First friend should have correct name");
				assertEquals("Friend 3", friendNames.get(1), "Second friend should have correct name");
			}
		}

		/**
		 * Tests for handling failures in chained operations.
		 */
		@Nested
		@DisplayName("When operations fail")
		class WhenOperationsFail {

			private int invalidUserId;
			private Try<List<String>> result;

			@BeforeEach
			void setUp() {
				// Arrange
				invalidUserId = - 1;

				// Act
				result = Try.of(() -> findUserById(invalidUserId))
						.flatMap(WhenWorkingWithExternalServices.this::findFriendsByUser)
						.map(WhenWorkingWithExternalServices.this::extractFriendNames);
			}

			@Test
			@DisplayName("Should result in Failure")
			void shouldResultInFailure() {
				// Assert
				assertTrue(result.isFailure(), "Result should be a Failure when operations fail");
			}

			@Test
			@DisplayName("Should contain appropriate exception")
			void shouldContainAppropriateException() {
				// Assert
				Exception exception = assertThrows(Exception.class, result::get,
						"Getting value from Failure should throw exception");
				assertTrue(exception.getMessage().contains("Invalid user ID"),
						"Exception message should indicate invalid user ID");
			}
		}

		/**
		 * Tests for recovering from failures in chained operations.
		 */
		@Nested
		@DisplayName("When recovering from failures")
		class WhenRecoveringFromFailures {

			private int invalidUserId;
			private Try<List<String>> result;

			@BeforeEach
			void setUp() {
				// Arrange
				invalidUserId = - 1;

				// Act
				result = Try.of(() -> findUserById(invalidUserId))
						.flatMap(WhenWorkingWithExternalServices.this::findFriendsByUser)
						.map(WhenWorkingWithExternalServices.this::extractFriendNames)
						.recover(ex -> Arrays.asList("Default Friend 1", "Default Friend 2"));
			}

			@Test
			@DisplayName("Should result in Success after recovery")
			void shouldResultInSuccessAfterRecovery() {
				// Assert
				assertTrue(result.isSuccess(), "Result should be a Success after recovery");
			}

			@Test
			@DisplayName("Should contain correct number of default friends")
			void shouldContainCorrectNumberOfDefaultFriends() throws Exception {
				// Assert
				List<String> friendNames = result.get();
				assertEquals(2, friendNames.size(), "Should have exactly 2 default friends");
			}

			@Test
			@DisplayName("Should contain correct default friend names")
			void shouldContainCorrectDefaultFriendNames() throws Exception {
				// Assert
				List<String> friendNames = result.get();
				assertEquals("Default Friend 1", friendNames.get(0), "First default friend should have correct name");
				assertEquals("Default Friend 2", friendNames.get(1), "Second default friend should have correct name");
			}
		}
	}

	/**
	 * Tests for using Try with resource management.
	 * <p>
	 * These tests demonstrate how Try can be used to ensure proper resource clean-up,
	 * similar to try-with-resources but with functional composition capabilities.
	 */
	@Nested
	@DisplayName("When managing resources")
	class WhenManagingResources {

		private AtomicInteger resourceClosed;
		private Try<String> result;

		@BeforeEach
		void setUp() {
			// Arrange
			resourceClosed = new AtomicInteger(0);

			// Act
			result = Try.of(() -> {
				try (AutoCloseable ignored = () -> resourceClosed.incrementAndGet()) {
					return "Resource used successfully";
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}

		@Test
		@DisplayName("Should result in Success")
		void shouldResultInSuccess() {
			// Assert
			assertTrue(result.isSuccess(), "Result should be a Success when resource is used properly");
		}

		@Test
		@DisplayName("Should return correct result")
		void shouldReturnCorrectResult() throws Exception {
			// Assert
			assertEquals("Resource used successfully", result.get(),
					"Result should contain the value from successful resource usage");
		}

		@Test
		@DisplayName("Should close resource exactly once")
		void shouldCloseResourceExactlyOnce() {
			// Assert
			assertEquals(1, resourceClosed.get(), "Resource should be closed exactly once");
		}
	}
}
