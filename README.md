# JWT Authentication with Refresh Token Rotation and Redis Storage

### Overview

This project implements a robust authentication system using JWT (JSON Web Tokens) with Refresh Token Rotation and token storage in Redis. It provides a secure, scalable solution for managing user sessions by issuing access tokens that expire after a short period and refresh tokens that allow users to obtain new access tokens without requiring them to log in again.

The system is designed to:
*	Issue JWT access tokens and refresh tokens for authentication.
*	Implement refresh token rotation to ensure security by invalidating old refresh tokens when a new one is issued.
*	Use Redis as a fast, in-memory database to store tokens temporarily and invalidate them when necessary.

### Key Features

The following guides illustrate how to use some features concretely:

*   JWT-based Authentication: The application uses JWT for access tokens, which are used to authenticate API requests. Access tokens are short-lived, typically expiring after a predefined duration (e.g., 15 minutes).
*	Refresh Token Rotation: The system supports refresh token rotation, meaning each time a refresh token is used to obtain a new access token, the old refresh token is invalidated. This adds an extra layer of security, preventing the reuse of compromised refresh tokens.
*	Redis for Token Storage: Tokens are stored in Redis, which is an in-memory data structure store, ensuring fast access and retrieval. Redis allows for efficient management of token expiration, invalidation, and access control.
*	Access Token and Refresh Token Invalidations: When a user requests a new access token using a refresh token, both the access token and the refresh token are invalidated in the database to ensure that old tokens cannot be used again.

### Why Use Redis for Token Storage?

*   Fast Performance: Redis is an in-memory store, meaning it can read and write data much faster than traditional databases. This is crucial for authentication workflows, where speed and scalability are essential.
*	Session Management: Redis supports expiration times for keys, which is ideal for managing JWTs with expiry times. We store both access tokens and refresh tokens in Redis to manage user sessions efficiently.
*	Security: By storing tokens in Redis, we can easily invalidate them whenever necessary (e.g., during refresh token rotation). This ensures that old tokens can’t be reused even if someone attempts to steal them.

### Token Invalidations

Whenever a user requests a new access token via the refresh token endpoint, the application:
1.	Invalidates the old refresh token and access token from Redis.
2.	Generates and saves a new access token and a new refresh token.
3.	Issues the new tokens to the user.

This mechanism prevents the reuse of old tokens, making it more secure and ensuring that compromised tokens cannot be used.

### Why Invalidate Tokens?
*   **Enhanced Security:** Invalidating old tokens ensures that if a refresh token is compromised, it cannot be used to issue a new access token. Refresh token rotation prevents attackers from obtaining access indefinitely.
*	**Token Lifecycle Management:** Tokens are designed to expire after a certain period. Invalidating them after use ensures that users cannot continue using old tokens once their session has expired, which is especially important in high-security applications.

### How the Application Works
1.  **Login:** The user logs in using their credentials (username and password). Upon successful login, the server generates an access token and a refresh token. The refresh token is stored in Redis with a key corresponding to the user’s username.
2.	**Token Expiry:** Access tokens are short-lived. When an access token expires, the user sends the refresh token to a refresh token endpoint to get a new access token.
3.	**Refresh Token Rotation:** When the user sends a refresh token, the server validates the refresh token. If valid, the server issues a new access token and a new refresh token, invalidates the old ones, and saves the new tokens in Redis.

### Project Structure
*   JwtUtils: Utility class for generating and validating JWT access and refresh tokens.
*	TokenRepository: A repository responsible for interacting with Redis to store and invalidate tokens.
*	AuthService: Handles the authentication logic, including token generation and refresh token validation.
*	AuthController: Exposes endpoints for login, register, and refreshing tokens.

### Getting Started
*   JDK 21 or higher
*	Redis server (local or remote instance)
*   Mysql server (local or remote instance)
*	Maven for dependency management

### API Endpoints
*	POST /auth/login: Logs in a user and returns an access token and refresh token.
*	POST /auth/refresh-token: Accepts a refresh token and returns a new access token and refresh token.
*	POST /auth/register: Registers a new user.
*   Get  /user: To get user info (test access token)
