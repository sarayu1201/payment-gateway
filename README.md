# Payment Gateway - Multi-Method Processing System

A production-ready payment gateway application similar to Razorpay and Stripe, built with Java Spring Boot. This system handles merchant onboarding, payment order management, multi-method payment processing (UPI and Cards), and provides a hosted checkout page.

## Features

- **Merchant Onboarding**: Complete merchant management with API credentials
- **Order Management**: Create and track payment orders
- **Multi-Method Payment Processing**: Support for UPI (Virtual Payment Address) and Card payments
- **Payment Validation**: Luhn algorithm for card validation, VPA format validation for UPI
- **Hosted Checkout Page**: Customer-facing payment form
- **RESTful API**: Complete API for merchants to integrate payment processing
- **Database Persistence**: PostgreSQL database with proper schema design
- **Docker Deployment**: Full containerization with docker-compose

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA
- **Database**: PostgreSQL 15
- **Frontend**: React (separate applications for dashboard and checkout)
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven

## Project Structure

```
payment-gateway/
├── docker-compose.yml          # Docker orchestration
├── README.md                   # This file
├── .env.example                # Environment configuration template
├── backend/                    # Java Spring Boot backend
│   ├── pom.xml                # Maven configuration
│   ├── Dockerfile             # Backend container setup
│   └── src/                   # Source code
│       └── main/
│           ├── java/com/gateway/
│           │   ├── PaymentGatewayApplication.java
│           │   ├── config/
│           │   ├── controllers/
│           │   ├── models/
│           │   ├── repositories/
│           │   ├── services/
│           │   └── dto/
│           └── resources/
│               ├── application.properties
│               └── schema.sql
├── frontend/                   # React dashboard (Merchant UI)
│   ├── Dockerfile
│   ├── package.json
│   └── src/
│       ├── pages/
│       ├── components/
│       └── App.jsx
└── checkout/                   # React checkout page (Customer UI)
    ├── Dockerfile
    ├── package.json
    └── src/
        ├── pages/
        ├── components/
        └── App.jsx
```

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Git

### Quick Start

```bash
# Clone the repository
git clone https://github.com/sarayu1201/payment-gateway.git
cd payment-gateway

# Start all services
docker-compose up -d

# Verify services are running
docker-compose ps
```

### Accessing the Services

- **API Server**: http://localhost:8000
- **Dashboard (Merchant UI)**: http://localhost:3000
- **Checkout Page**: http://localhost:3001
- **Database**: postgresql://localhost:5432/payment_gateway
  - User: `gateway_user`
  - Password: `gateway_pass`

## Test Merchant Credentials

A test merchant is automatically seeded on application startup:

- **Email**: `test@example.com`
- **API Key**: `key_test_abc123`
- **API Secret**: `secret_test_xyz789`
- **Merchant ID**: `550e8400-e29b-41d4-a716-446655440000`

## API Endpoints

### Health Check
- `GET /health` - Health check endpoint (no auth required)

### Order Management
- `POST /api/v1/orders` - Create a new payment order
- `GET /api/v1/orders/{order_id}` - Retrieve order details

### Payment Processing
- `POST /api/v1/payments` - Create and process a payment
- `GET /api/v1/payments/{payment_id}` - Retrieve payment status

### Test Endpoints
- `GET /api/v1/test/merchant` - Get test merchant info (no auth required)

## API Authentication

All protected endpoints require:
- `X-Api-Key`: Merchant's API key
- `X-Api-Secret`: Merchant's API secret

## Database Schema

### Merchants Table
- `id` (UUID): Unique identifier
- `name` (String): Merchant name
- `email` (String, unique): Email address
- `api_key` (String, unique): API key for authentication
- `api_secret` (String): API secret
- `webhook_url` (Text, optional): Webhook URL for notifications
- `is_active` (Boolean): Active status
- `created_at`, `updated_at`: Timestamps

### Orders Table
- `id` (String): "order_" + 16 alphanumeric characters
- `merchant_id` (UUID): Foreign key to merchants
- `amount` (Integer): Amount in smallest currency unit (paise)
- `currency` (String): Currency code (default: INR)
- `receipt` (String, optional): Receipt identifier
- `notes` (JSON, optional): Additional metadata
- `status` (String): Order status (created, completed, etc.)
- `created_at`, `updated_at`: Timestamps

### Payments Table
- `id` (String): "pay_" + 16 alphanumeric characters
- `order_id` (String): Foreign key to orders
- `merchant_id` (UUID): Foreign key to merchants
- `amount` (Integer): Payment amount in paise
- `currency` (String): Currency code
- `method` (String): Payment method (upi, card)
- `status` (String): Status (processing, success, failed)
- `vpa` (String, optional): UPI Virtual Payment Address
- `card_network` (String, optional): Card network (visa, mastercard, amex, rupay)
- `card_last4` (String, optional): Last 4 digits of card
- `error_code`, `error_description` (optional): Error details
- `created_at`, `updated_at`: Timestamps

## Payment Processing Flow

1. **Order Creation**: Merchant creates an order via `/api/v1/orders`
2. **Payment Initiation**: Customer initiates payment via `/api/v1/payments`
3. **Validation**: Payment method validation (Luhn for cards, regex for UPI)
4. **Processing**: Simulated bank processing with 5-10 second delay
5. **Status Update**: Payment marked as success (90% UPI, 95% Cards) or failed
6. **Confirmation**: Customer redirected to success/failure page

## Validation Rules

### VPA Validation (UPI)
- Format: `^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$`
- Examples: `user@paytm`, `john.doe@okhdfcbank`, `user_123@phonepe`

### Card Validation
- **Luhn Algorithm**: Card number must pass Luhn checksum
- **Network Detection**: Based on card number prefix
  - Visa: Starts with 4
  - Mastercard: Starts with 51-55
  - Amex: Starts with 34 or 37
  - RuPay: Starts with 60, 65, or 81-89
- **Expiry**: Must be current month/year or in future
- **CVV**: 3-4 digit security code

## Environment Configuration

Create a `.env` file based on `.env.example`:

```env
DATABASE_URL=postgresql://gateway_user:gateway_pass@postgres:5432/payment_gateway
PORT=8000

# Test merchant (auto-seeded)
TEST_MERCHANT_EMAIL=test@example.com
TEST_API_KEY=key_test_abc123
TEST_API_SECRET=secret_test_xyz789

# Payment simulation
UPI_SUCCESS_RATE=0.90
CARD_SUCCESS_RATE=0.95
PROCESSING_DELAY_MIN=5000
PROCESSING_DELAY_MAX=10000

# Test mode for evaluation
TEST_MODE=false
TEST_PAYMENT_SUCCESS=true
TEST_PROCESSING_DELAY=1000
```

## Building from Source

### Build Backend JAR
```bash
cd backend
mvn clean package
```

### Build Docker Images
```bash
docker-compose build
```

## Development

### Running Locally (without Docker)

1. **Start PostgreSQL**: Ensure PostgreSQL 15 is running
2. **Update application.properties**: Configure database connection
3. **Build & Run Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
4. **Start React Frontend**:
   ```bash
   cd frontend
   npm install
   npm start
   ```
5. **Start Checkout Page**:
   ```bash
   cd checkout
   npm install
   npm start
   ```

## Testing

### Create an Order (cURL)
```bash
curl -X POST http://localhost:8000/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: key_test_abc123" \
  -H "X-Api-Secret: secret_test_xyz789" \
  -d '{
    "amount": 50000,
    "currency": "INR",
    "receipt": "receipt_001",
    "notes": {"customer_name": "John Doe"}
  }'
```

### Process a Payment (cURL - UPI)
```bash
curl -X POST http://localhost:8000/api/v1/payments \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: key_test_abc123" \
  -H "X-Api-Secret: secret_test_xyz789" \
  -d '{
    "order_id": "order_ABC123...",
    "method": "upi",
    "vpa": "user@paytm"
  }'
```

## Error Codes

- `AUTHENTICATION_ERROR` - Invalid API credentials
- `BAD_REQUEST_ERROR` - Validation error
- `NOT_FOUND_ERROR` - Resource not found
- `PAYMENT_FAILED` - Payment processing failed
- `INVALID_VPA` - VPA format invalid
- `INVALID_CARD` - Card validation failed
- `EXPIRED_CARD` - Card expiry date invalid

## Troubleshooting

### Container won't start
```bash
# Check logs
docker-compose logs -f api

# Ensure database is healthy
docker-compose ps
```

### Database connection errors
- Verify PostgreSQL is running: `docker-compose ps postgres`
- Check credentials match in `docker-compose.yml`
- Ensure database exists: `payment_gateway`

### Port conflicts
- Change ports in `docker-compose.yml` if ports 3000, 3001, 8000, 5432 are in use

## Deployment

For production deployment:

1. Update environment variables in `.env`
2. Configure database backups
3. Set up monitoring and logging
4. Enable HTTPS/TLS
5. Implement rate limiting
6. Set up CI/CD pipeline

## License

MIT License

## Contributing

Contributions are welcome! Please follow the existing code structure and add tests for new features.

## Support

For issues, questions, or suggestions, please open an issue in the repository.
