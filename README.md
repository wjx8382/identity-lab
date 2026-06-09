# Identity Lab

Identity & Access Management (IAM) Learning Project

学习 OAuth2、OIDC、FAPI、JWT、RBAC 等认证授权技术，并逐步实现一个简化版 Authorization Server。

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Security 6
- PostgreSQL
- Spring Data JPA
- JWT (jjwt)

## Features

### Authentication

- User Registration
- User Login
- BCrypt Password Encoding
- JWT Access Token
- Refresh Token

### Authorization

- Role-Based Access Control (RBAC)
- ROLE_USER
- ROLE_ADMIN
- Spring Security Authorization

## Roadmap

### Foundation

- [x] Spring Boot Initialization
- [x] PostgreSQL Integration
- [x] User Registration
- [x] User Login

### JWT

- [x] JWT Authentication
- [x] Refresh Token
- [x] JWT Filter
- [x] SecurityContext Integration

### Authorization

- [x] RBAC
- [x] ROLE_USER
- [x] ROLE_ADMIN
- [x] Admin Endpoint Protection

### OAuth2 & OpenID Connect

- [ ] OAuth2 Authorization Code Flow
- [ ] PKCE
- [ ] OpenID Connect (OIDC)
- [ ] UserInfo Endpoint
- [ ] ID Token

### FAPI

- [ ] FAPI Baseline
- [ ] PKCE Enforcement
- [ ] PAR (Pushed Authorization Requests)
- [ ] JARM
- [ ] MTLS

## Learning Goal

Build a mini identity platform from scratch and gradually evolve it into an OAuth2 / OIDC / FAPI compliant Authorization Server.
