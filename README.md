🚗 Society Parking Slot Reservation System <br>

A secure and intelligent parking management system built with Java, Spring Boot, and Spring Security.
This system enables residents and admins to manage parking slots efficiently, automate bookings, send email notifications, and view analytics — all from a unified dashboard. <br><br>

🌟 Features <br>
👤 User Features <br>

Register and login securely using Spring Security <br>

View available parking slots in real-time <br>

Book and cancel parking slots easily <br>

Book parking slots for guests <br>

View personal booking history and upcoming reservations <br>

Receive email notifications for booking confirmations and cancellations <br><br>

🛠️ Admin Features <br>

Manage user accounts (activate/deactivate users) <br>

Add, edit, or remove parking slots <br>

Approve or reject user bookings <br>

View detailed analytics of slot usage, bookings, and cancellations <br>

Monitor guest bookings and user activity logs <br><br>

📊 Analytics & Email Service <br>

Real-time analytics dashboard for admins <br>

Booking trends and occupancy reports <br>

Automated email notifications for users and guests <br><br>

🧩 Tech Stack <br>

Backend: Java, Spring Boot, Spring Security <br>

Frontend: Thymeleaf, HTML, CSS, Bootstrap <br>

Database: MySQL <br>

Tools: Maven, IntelliJ IDEA / Eclipse, Postman <br><br>

⚙️ Project Structure <br>

Controllers:
AdminController.java, BookingController.java, DefaultSuccessController.java, HomeController.java, LoginController.java, LogoutController.java, UserController.java <br><br>

DTOs:
BookingRequestDto.java, LoginDto.java, UserRegistrationDto.java <br><br>

Entities:
Booking.java, ParkingSlot.java, User.java <br><br>

Repositories:
BookingRepository.java, ParkingSlotRepository.java, UserRepository.java <br><br>

Services:
AnalyticsService.java, BookingService.java, EmailService.java, ParkingSlotService.java <br><br>

🚀 How to Run Locally <br>

1️⃣ Clone the repository



<br>

2️⃣ Open the project in IntelliJ IDEA or Eclipse <br>

3️⃣ Configure the MySQL Database in application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/parkingdb
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

<br>

4️⃣ Build and run the project

mvn spring-boot:run

<br>

5️⃣ Access the app in your browser:
👉 http://localhost:8080
 <br><br>

 ##Screenshots
 <br><br>
<img width="696" height="577" alt="image" src="https://github.com/user-attachments/assets/4f05535b-f415-41d4-a1ef-b5378786a058" />

<br><br>


<img width="1737" height="844" alt="image" src="https://github.com/user-attachments/assets/4c0282c7-f2d5-498e-88dd-7bdeada6ef8e" />

<br><br>
<img width="1646" height="722" alt="image" src="https://github.com/user-attachments/assets/da033a71-9b70-4808-bd90-2a704d5afdee" />


<br><br>
<img width="1677" height="603" alt="image" src="https://github.com/user-attachments/assets/e4eda773-3a1b-4ac2-a111-924d75d438b9" />

<br><br>
<img width="1669" height="818" alt="image" src="https://github.com/user-attachments/assets/807d4186-8558-4cf1-bb35-275586532cd8" />

<br><br>

<img width="1647" height="827" alt="image" src="https://github.com/user-attachments/assets/08288ddc-db37-406f-b3e0-886287f2c15d" />

<br><br>

<img width="1679" height="821" alt="image" src="https://github.com/user-attachments/assets/84a92d74-443e-4d05-bc93-499bf0d89edc" />




