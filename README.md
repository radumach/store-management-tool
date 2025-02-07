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
- `POST /api/products` - Add a product (ADMIN only)
- `GET /api/products/{id}` - Find a product by ID (USER, ADMIN)
- `PUT /api/products/{id}/price` - Update product price (ADMIN only)
- `GET /api/products` - Get all products (USER, ADMIN)

## Authentication
- User: `user/password` (USER role)
- Admin: `admin/admin` (ADMIN role)