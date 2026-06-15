# Identity Lab

Identity & Access Management (IAM) Learning Project

A hands-on project for learning and implementing modern identity protocols and authorization standards, including JWT, OAuth 2.0, OpenID Connect (OIDC), and FAPI.

The goal is to gradually evolve this project from a simple JWT authentication system into a standards-compliant Authorization Server.

## Tech Stack

* Java 21
* Spring Boot 3
* Spring Security 6
* PostgreSQL
* Spring Data JPA
* JWT (JJWT)

## Features

### Authentication

* User Registration
* User Login
* BCrypt Password Encoding
* JWT Access Token
* Refresh Token

### Authorization

* Role-Based Access Control (RBAC)
* ROLE_USER
* ROLE_ADMIN
* Spring Security Authorization

### OAuth2 & OIDC

* OAuth2 Authorization Code Flow
* PKCE (RFC 7636)
* ID Token
* UserInfo Endpoint
* Refresh Token Rotation (Basic)
* Scope-based Authorization

## Roadmap

### Foundation

* [x] Spring Boot Initialization
* [x] PostgreSQL Integration
* [x] User Registration
* [x] User Login

### JWT

* [x] JWT Authentication
* [x] JWT Filter
* [x] Refresh Token
* [x] SecurityContext Integration

### Authorization

* [x] RBAC
* [x] ROLE_USER
* [x] ROLE_ADMIN
* [x] Admin Endpoint Protection

### OAuth 2.0

* [x] Client Registration
* [x] Authorization Code Flow
* [x] Authorization Code Persistence
* [x] Access Token Issuance
* [x] Refresh Token Issuance

### PKCE

* [x] Code Verifier
* [x] Code Challenge
* [x] S256 Support
* [ ] PKCE Enforcement

### OpenID Connect

* [x] ID Token
* [x] UserInfo Endpoint
* [ ] OpenID Scope Validation
* [ ] OIDC Discovery Endpoint
* [ ] JWKS Endpoint

### FAPI

* [ ] FAPI Baseline
* [ ] Issuer Validation
* [ ] Audience Validation
* [ ] PAR (Pushed Authorization Requests)
* [ ] JARM
* [ ] MTLS

## Learning Goal

Build a mini identity platform from scratch and gradually evolve it into an OAuth2 / OIDC / FAPI compliant Authorization Server.

## Future Work

* OAuth 2.1
* Dynamic Client Registration
* Token Introspection
* Token Revocation
* OIDC Discovery
* JWKS Endpoint
* FAPI Baseline Compliance
* Spring Authorization Server Migration
