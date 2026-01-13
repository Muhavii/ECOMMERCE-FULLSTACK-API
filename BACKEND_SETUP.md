http://localhost:8080/h2-consolehttp://localhost:8080/h2-consolehttp://localhost:8080/h2-console# Backend Setup Complete! 🎉

## What I've Added

### 1. **AdminController** (`src/main/java/com/ecommerce/controller/AdminController.java`)
New REST endpoints for admin operations:

#### Products Management
- **POST** `/api/admin/products` - Add new product
  ```json
  {
    "name": "Product Name",
    "description": "Description",
    "price": 99.99,
    "stock": 50,
    "discount": 10
  }
  ```

- **GET** `/api/admin/products` - Get all products (for admin view)
- **PUT** `/api/admin/products/{id}` - Update product details
- **DELETE** `/api/admin/products/{id}` - Delete product (soft delete)

#### Orders Management
- **GET** `/api/admin/orders` - Get all orders
- **GET** `/api/admin/orders/{id}` - Get specific order
- **PUT** `/api/admin/orders/{id}` - Update order status
  ```json
  {
    "status": "CONFIRMED"
  }
  ```
  Available statuses: `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`

#### Dashboard Stats
- **GET** `/api/admin/stats` - Get dashboard statistics
  - Total products count
  - Total orders count
  - Total revenue
  - Pending orders count

### 2. **Product Model Updates**
Added `discount` field to support special offers:
```java
private Integer discount = 0;
```

### 3. **Order Model Updates**
Updated OrderStatus enum to match frontend:
```
PENDING → CONFIRMED → SHIPPED → DELIVERED
```

### 4. **OrderRepository Updates**
Added method:
```java
List<Order> findByStatus(Order.OrderStatus status);
```

## Next Steps

### Step 1: Create an Admin User
Since the endpoints are protected with `@PreAuthorize("hasRole('ADMIN')")`, you need an admin user:

```java
// Run this in a database script or create an admin endpoint
INSERT INTO users (username, email, password, full_name, role, active, created_at)
VALUES ('admin', 'admin@shopvibe.com', 'hashed_password', 'Admin User', 'ADMIN', true, NOW());
```

### Step 2: Run the Backend
```bash
cd /Users/a/Documents/Ecommerce_apk
mvn spring-boot:run
```

Backend runs on: **http://localhost:8080**

### Step 3: Run the Frontend
```bash
cd /Users/a/Documents/Ecommerce_apk/frontend
npm run dev
```

Frontend runs on: **http://localhost:5173**

### Step 4: Test Admin Functionality

1. **Login as Admin:**
   - Navigate to http://localhost:5173
   - Click "Login"
   - Enter: `username: admin` and your password
   - An "Admin Panel" button should appear in navbar

2. **Add Products:**
   - Click "Admin Panel"
   - Fill in product details
   - Click "Add Product"

3. **Manage Orders:**
   - View all orders
   - Click status buttons to update order status
   - Watch the status change in real-time

## Security Notes

⚠️ **Important:**
- All admin endpoints require `ADMIN` role
- JWT token must be included: `Authorization: Bearer {token}`
- Frontend automatically includes the token from localStorage
- Password should be properly hashed (bcrypt) in production

## Database Schema

The following tables are automatically created:

- **users** - User accounts with roles
- **products** - Products with discount field
- **orders** - Order records with status tracking
- **order_items** - Individual items in orders
- **cart_items** - Shopping cart items

## Frontend Integration

The frontend already has:
- Admin login detection
- "Admin Panel" button in navbar for admins
- Product creation form
- Order management interface
- Status update buttons
- Real-time data fetching

## Troubleshooting

**Issue: "Admin role not found"**
- Make sure the user has `role = 'ADMIN'` in the database

**Issue: 403 Forbidden on admin endpoints**
- Check JWT token is valid
- Verify user role is ADMIN

**Issue: Database tables not created**
- Check `application.properties` has `spring.jpa.hibernate.ddl-auto=create-drop`
- Restart Spring Boot application

## API Examples

### Add Product
```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "stock": 10,
    "discount": 15
  }'
```

### Update Order Status
```bash
curl -X PUT http://localhost:8080/api/admin/orders/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"status": "SHIPPED"}'
```

### Get Dashboard Stats
```bash
curl http://localhost:8080/api/admin/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## What's Ready to Go!

✅ Admin Controller with all endpoints
✅ Product discount support
✅ Order status management
✅ Dashboard statistics
✅ Role-based access control
✅ Frontend integration
✅ Backend compilation successful

You're all set! Start the backend and frontend to test the admin panel. 🚀
