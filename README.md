Expense Tracker API
A full-featured backend REST API for personal finance tracking

Built with Spring Boot 3, JWT Authentication, and MySQL.
Designed using clean architecture and enterprise-grade coding practices.
📘 Overview

Expense Tracker API is a backend-only system that allows users to:

Register & log in with JWT authentication

Create categories

Create expenses

Filter & analyze expense data

Delete category safely with transfer rule

Prepare monthly reports (Phase 4)

Designed for personal projects, portfolio, and backend learning.

🏛 Architecture (Clean Layered)


<img width="575" height="383" alt="image" src="https://github.com/user-attachments/assets/c5f4c054-e5e8-4a84-9683-93606c79163d" />


📐 ERD (Entity Relationship Diagram)

<img width="641" height="701" alt="image" src="https://github.com/user-attachments/assets/2d6b06ed-d810-415b-ba12-766f86e00508" />


🔐 Security Flow (JWT)
Client → POST /auth/login → AuthService → JWT token
Client stores JWT in local storage
Client → Request API with Authorization: Bearer <token>

JWT Filter:
1) Extract token
2) Validate signature
3) Set Authentication in SecurityContext
4) Controller receives authenticated user

🚀 Features
🔑 Authentication

Register

Login (JWT)

Stateless sessions

Custom UserDetailsService

Custom JwtAuthenticationFilter

Custom access denied handling

🗂 Category Module

Create / Update / Delete

List categories for user

Delete with Transfer (safest business logic)

💰 Expense Module

Create / Update / Delete

Filter by:

date

date range

category

Ownership protection

Strict validation

📊 Report Module (Coming Soon)

Monthly summary

Spending by category

Top spending category

Chart-ready API

🔎 API Endpoints
🔐 Auth

<img width="808" height="175" alt="image" src="https://github.com/user-attachments/assets/5958cadd-a875-4300-8f01-90340be73e1c" />


📁 Categories

<img width="754" height="340" alt="image" src="https://github.com/user-attachments/assets/e54ec625-1348-4b18-bdc4-f4ca54093bf2" />

💰 Expenses

<img width="701" height="376" alt="image" src="https://github.com/user-attachments/assets/45720fe2-58d3-432b-9fa1-2533f72cbe83" />



🛠 Installation
1. Clone project
git clone https://github.com/qucbao/expense-tracker-api.git
cd expense-tracker-api

2. Configure database

src/main/resources/application.yaml

3. Run
mvn spring-boot:run

🧱 Database Schema

Generated automatically via Hibernate because ddl-auto: update.

📦 Build & Package
mvn clean package

📄 License

MIT License – feel free to use this project for study or portfolio.

✨ Author

Đào Quốc Bảo
Backend Developer (Java / Spring Boot)
GitHub: https://github.com/qucbao
