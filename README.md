# Project Hybrid Store — Java OOP + MySQL

A starter e-commerce website with a storefront, product manager, shopping bag, and demo checkout.

## Requirements
- Java 17+
- Maven 3.8+
- XAMPP with MySQL running (Apache is not required for this Java app)

## 1. Create the database
Open phpMyAdmin at `http://localhost/phpmyadmin` and run `database.sql`.
The app also uses `createDatabaseIfNotExist=true`, but creating the database manually is recommended.

## 2. Configure MySQL
Open `src/main/resources/application.properties`. The default XAMPP MySQL setup is:
- username: `root`
- password: blank

If your MySQL password is different, update `spring.datasource.password`.

## 3. Run the website
Open a terminal in this project folder and run:
```bash
mvn spring-boot:run
```
Then visit:
- Storefront: http://localhost:8080
- Product manager: http://localhost:8080/admin/products
- Shopping bag: http://localhost:8080/cart

On first launch, Hibernate creates/updates the product and order tables.

## Admin login (new)
- Admin page: `http://localhost:8080/admin/products`
- Login page: `http://localhost:8080/login`
- Local default username: `admin`
- Local default password: `ChangeThisBeforeLaunch123!`

These defaults are for local testing only. Change them before use and never deploy with known default credentials. For a Windows PowerShell terminal, set credentials for the current terminal before starting Maven:
```powershell
$env:STORE_ADMIN_USERNAME="your-admin-name"
$env:STORE_ADMIN_PASSWORD="use-a-long-unique-password-here"
mvn -U org.springframework.boot:spring-boot-maven-plugin:3.4.3:run
```
The current admin account is stored in application memory, so it is a single configured admin account, not a database-backed user-management system. Add persistent users/roles before a multi-staff launch.

## Add products
Open **Manage Products**, complete the form, and press **Save Product**. New products are saved to MySQL and appear in the store.

## Important before using with real customers
This is a functional learning prototype, not a verified official Project Hybrid website. Get the brand owner's permission to use their name, logo, images, and products. Before public launch:
- Protect `/admin/products` with authentication and authorization.
- Add CSRF protection, input validation, rate limiting, secure session cookies, HTTPS, backups, and privacy/terms pages.
- Add a real payment provider (this starter does not collect payments). For GCash and other e-wallets, create a merchant account with a supported Philippine payment gateway (for example, PayMongo), then implement server-side payment-intent creation, signed webhook verification, and reconciliation. Never put secret API keys in HTML or JavaScript, and never mark an order paid based only on the browser redirect.
- Configure real delivery rates, stock/refund workflows, email notifications, and order management.
- Replace sample product imagery with images you own or are licensed to use.
- Review Philippine consumer, privacy, tax, and e-commerce requirements with a qualified professional.

## Project structure
- `model/Product.java` — product entity (OOP model stored in MySQL)
- `model/StoreOrder.java` — order entity
- `repository/` — database repositories
- `controller/StoreController.java` — storefront, cart, product manager, checkout logic
- `templates/` — Thymeleaf HTML pages
- `static/style.css` — responsive storefront design
