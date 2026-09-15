# Pedidos360 Security - Versión 3.0 (Final)

Proyecto completo de evaluación para Pedidos360 con arquitectura de seguridad basada en Microsoft Entra ID (Azure AD), Frontend Angular y Microservicio BFF en Spring Boot.

## Estructura del Proyecto

- `frontend-pedidos360/`: Aplicación web en Angular con integración MSAL (login, dashboard e interceptor de tokens JWT).
- `ms-pedidos360-bff/`: Microservicio BFF en Spring Boot configurado con Spring Security OAuth2 Resource Server y validación de scopes (`OT.Create`).

## Requisitos

- Node.js (v18+)
- Java 21
- Maven

## Ejecución

### Frontend (Angular)
```bash
cd frontend-pedidos360
npm install
npm start
```

### Backend (Spring Boot BFF)
```bash
cd ms-pedidos360-bff
./mvnw spring-boot:run
```
