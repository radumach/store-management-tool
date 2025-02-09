# Store Management Tool

A Spring Boot-based API for managing products in a store.

## Features
- Add a product
- Find a product by ID
- Update product price
- Role-based access control (USER, ADMIN)
- Basic authentication
- Error handling and logging

## Running the Application
1. Clone the repository.
2. Run `mvn clean install`.
3. Start the application with `mvn spring-boot:run`.

## Testing
- Unit tests: `mvn test`
- Functional tests: Run `ProductControllerIntegrationTest`

## API Endpoints
- `POST /login` - login with username and password JSON `{"username": "user_name", "password": "user_password"}`
- `POST /admin/add-user` - Add a new user (ADMIN only)
- `PUT /admin/change-username` - change a username for an existing user (ADMIN only)
- `GET /user/{username}` - Find a user by USERNAME (USER, ADMIN)
- `PUT /user/change-password` - Change the password of an existing user (USER only)
- `POST /api/products` - Add a product (ADMIN only)
- `GET /api/products/{id}` - Find a product by ID (USER, ADMIN)
- `PUT /api/products/{id}/price` - Update product price (ADMIN only)
- `GET /api/products` - Get all products (USER, ADMIN)

## Authentication
- User: `user/userpassword` (USER role)
- Admin: `admin/adminpassword` (ADMIN role)