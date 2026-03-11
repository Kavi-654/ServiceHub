# ServiceHub

A clean-architecture Java backend platform that connects service requesters with service providers across multiple categories like plumbing, electrical work, carpentry, and more.

## Overview

ServiceHub is a service marketplace platform where users can post service requests and providers can submit competitive offers. The platform manages the complete workflow from request creation to service completion and rating.

### Key Features

- **Dual User Roles**: Users can be both service requesters and service providers
- **Multiple Service Categories**: Support for 10+ service categories including plumbing, electrical, carpentry, cleaning, and more
- **Competitive Bidding**: Multiple providers can submit offers for a single service request
- **Provider Profiles**: Business profiles with ratings and reviews
- **Smart Workflow**: Automatic rejection of competing offers when one is accepted
- **Rating System**: Users can rate completed services, automatically updating provider ratings
- **Session Management**: Secure authentication with BCrypt password hashing

## Architecture

ServiceHub follows a clean layered architecture pattern:

```
Servlets → Handlers → DAOs → QueryGenerator/QueryExecutor → Database
```

- **Servlets**: Handle HTTP requests and responses
- **Handlers**: Business logic layer
- **DAOs**: Data access layer with database operations
- **QueryGenerator**: Universal query builder for all SQL operations
- **QueryExecutor**: Executes queries with proper resource management
- **Models**: POJO classes representing database entities

## Database Schema

The application uses MySQL with the following core tables:

- **CUser**: User accounts with provider status
- **Category**: Service categories
- **Business**: Provider business profiles
- **Service_Request**: Service requests posted by users
- **Transaction**: Provider offers/applications
- **Rating**: Service ratings and reviews

[View Database Diagram](https://dbdiagram.io/d/696f31bcd6e030a0248bd304)

## Workflow

1. **User Creates Request**: Posts a service need with title, description, category, and priority
2. **Providers Apply**: Multiple providers submit offers with quoted prices and messages
3. **User Reviews Offers**: Compares provider profiles, ratings, and quotes
4. **User Accepts One**: Selects the best provider (automatically rejects others and closes request)
5. **Service Completion**: Provider completes work, user marks as complete
6. **Rating**: User submits rating, which updates the business's average rating

## Tech Stack

- **Backend Framework**: Java Servlets (Jakarta EE)
- **Database**: MySQL
- **Password Hashing**: BCrypt (jBCrypt)
- **Server**: Apache Tomcat / Any Servlet Container

## Prerequisites

- Java 11 or higher
- MySQL 8.0 or higher
- Apache Tomcat 10.x or compatible servlet container


## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Kavi-654/ServiceHub.git
cd ServiceHub
```

### 2. Database Setup

Create a MySQL database and run the table creation queries:

```sql
-- Create database
CREATE DATABASE servicehub;
USE servicehub;

-- Run table creation scripts (see documentation for full SQL)
-- Tables: CUser, Category, Business, Service_Request, Transaction, Rating
```

Insert initial category data:

```sql
INSERT INTO Category (category_name, category_description) VALUES
('Plumbing', 'Water supply, drainage, and pipe-related services'),
('Electrical', 'Electrical wiring, repairs, and installations'),
('Carpentry', 'Wood work, furniture, and home repairs'),
('Cleaning', 'Home and office cleaning services'),
('Painting', 'Interior and exterior painting services'),
('Gardening', 'Lawn care, gardening, and landscaping'),
('AC Repair', 'Air conditioning installation and repair'),
('Appliance Repair', 'Repair of household appliances'),
('Pest Control', 'Pest and termite control services'),
('Moving & Packing', 'House moving and packing services');
```

### 3. Configure Database Connection

Update `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/servicehub
db.username=your_username
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```


### 4. Deploy to Tomcat

- Copy the generated WAR file from `target/` to Tomcat's `webapps/` directory
- Start Tomcat server
- Access the application at `http://localhost:8080/servicehub`

## API Endpoints

### User Management (`/user`)

#### POST Endpoints

- **Register**: `POST /user?action=register`
  - Parameters: `userName`, `email`, `phoneNumber`, `password`, `bio`
  
- **Login**: `POST /user?action=login`
  - Parameters: `email`, `password`
  
- **Logout**: `POST /user?action=logout`

- **Update Profile**: `POST /user?action=updateProfile`
  - Parameters: `userName`, `email`, `phoneNumber`, `bio`
  
- **Become Provider**: `POST /user?action=becomeProvider`

- **Register Business**: `POST /user?action=registerBusiness`
  - Parameters: `name`, `description`, `categoryId`
  
- **Update Business**: `POST /user?action=updateBusiness`
  - Parameters: `businessId`, `name`, `description`, `categoryId`

#### GET Endpoints

- **Get Profile**: `GET /user?action=profile`
- **Get My Business**: `GET /user?action=myBusiness`
- **Get Categories**: `GET /user?action=getCategories`
- **Search Providers**: `GET /user?action=searchProviders&categoryId={id}`

### Service Management (`/service`)

#### POST Endpoints

- **Create Request**: `POST /service?action=createRequest`
  - Parameters: `title`, `description`, `categoryId`, `location`, `priority`
  
- **Close Request**: `POST /service?action=closeRequest`
  - Parameters: `requestId`
  
- **Cancel Request**: `POST /service?action=cancelRequest`
  - Parameters: `requestId`
  
- **Send Offer** (Provider): `POST /service?action=sendOffer`
  - Parameters: `requestId`, `message`, `quotedPrice`
  
- **Accept Offer**: `POST /service?action=acceptOffer`
  - Parameters: `requestId`, `transactionId`
  
- **Reject Offer**: `POST /service?action=rejectOffer`
  - Parameters: `transactionId`
  
- **Mark Complete**: `POST /service?action=markComplete`
  - Parameters: `transactionId`
  
- **Submit Rating**: `POST /service?action=submitRating`
  - Parameters: `transactionId`, `providerId`, `businessId`, `stars`, `review`

#### GET Endpoints

- **View All Requests**: `GET /service?action=viewRequests`
- **View My Requests**: `GET /service?action=myRequests`
- **View Offers**: `GET /service?action=viewOffers&requestId={id}`
- **My Transactions**: `GET /service?action=myTransactions`
- **View Ratings**: `GET /service?action=viewRatings&businessId={id}`

## Project Structure

```
servicehub/
├── src/main/java/com/serviceplatform/
│   ├── servlets/
│   │   ├── UserServlet.java
│   │   └── ServiceServlet.java
│   ├── handlers/
│   │   ├── UserHandler.java
│   │   ├── ServiceRequestHandler.java
│   │   ├── ServiceTransactionHandler.java
│   │   ├── BusinessHandler.java
│   │   ├── RatingHandler.java
│   │   └── CategoryHandler.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── ServiceRequestDAO.java
│   │   ├── ServiceTransactionDAO.java
│   │   ├── BusinessDAO.java
│   │   ├── RatingDAO.java
│   │   └── CategoryDAO.java
│   ├── querygenerator/
│   │   └── QueryGenerator.java
│   ├── queryexecutor/
│   │   └── QueryExecutor.java
│   ├── models/
│   │   ├── User.java
│   │   ├── ServiceRequest.java
│   │   ├── ServiceTransaction.java
│   │   ├── Business.java
│   │   ├── Rating.java
│   │   └── Category.java
│   ├── enums/
│   │   ├── TransactionStatus.java
│   │   ├── ServiceRequestStatus.java
│   │   └── PriorityLevel.java
│   ├── utils/
│   │   ├── DbConnection.java
│   │   ├── PasswordUtil.java
│   │   └── ResponseUtil.java
│   ├── checkers/
│   │   └── UserChecker.java
│   └── exceptions/
│       └── PasswordException.java
├── src/main/resources/
│   └── db.properties
├── src/main/webapp/WEB-INF/
│   └── web.xml
└── pom.xml
```

## Key Design Patterns

### Universal Query Generator

The `QueryGenerator` class provides a single source for all SQL query generation, supporting:

- Insert queries with auto-generated placeholders
- Select queries (single, multiple, with ordering)
- Update queries (single/multiple columns, with timestamps)
- Delete queries
- Count queries
- Aggregate queries (average ratings)
- Complex JOIN queries for fetching related data

### Transaction Management

Critical operations like accepting an offer are handled atomically:

1. Update accepted transaction to `ACCEPTED`
2. Reject all other competing transactions
3. Close the service request

All three steps must succeed or the operation fails.

### Password Security

- Passwords are hashed using BCrypt before storage
- Plain passwords are never stored in the database
- Password validation ensures minimum security requirements

### Session Management

- User sessions are managed with HttpSession
- Session timeout set to 30 minutes
- User ID and basic info stored in session for authentication

## Security Features

- **Password Hashing**: BCrypt with automatic salt generation
- **SQL Injection Prevention**: PreparedStatements throughout
- **Session-based Authentication**: Secure session management
- **Input Validation**: Email and password format validation
- **Resource Management**: Proper closing of database connections and result sets

## Future Enhancements

- RESTful API with JSON responses
- Frontend web interface (React/Angular)
- Mobile app integration
- Real-time notifications
- Payment gateway integration
- Advanced search and filtering
- Provider verification system
- Service scheduling/calendar
- Chat between users and providers
- Photo upload for service requests

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the [MIT License](LICENSE).

## Contact

For questions or support, please contact:
- **Developer**: Kavinayasri M
- **GitHub**: [@Kavi-654](https://github.com/Kavi-654)
- **Project Link**: [https://github.com/Kavi-654/ServiceHub](https://github.com/Kavi-654/ServiceHub)

## Acknowledgments

- Database diagram created with [dbdiagram.io](https://dbdiagram.io)
- BCrypt implementation by jBCrypt
- Built with Jakarta EE Servlets
