# Spring Authorization Server Comparison

This document compares the custom implementation in **Identity Lab** with **Spring Authorization Server (SAS)** to understand the design decisions behind the official implementation.

---

# 1. Client

| Identity Lab | Spring Authorization Server |
| ------------ | --------------------------- |
| ClientEntity | RegisteredClient            |

## Identity Lab

The custom implementation stores OAuth2 client information in `ClientEntity`.

Main responsibilities:

* Store `client_id`
* Store `client_secret`
* Store `redirect_uri`
* Store `scope`
* Store `certificate_subject` for mTLS

The implementation mainly focuses on understanding OAuth2 concepts and the Authorization Code Flow.

---

## Spring Authorization Server

Spring Authorization Server models a client as a `RegisteredClient`.

Besides the basic client information, it also supports:

* Multiple redirect URIs
* Multiple scopes
* Multiple client authentication methods
* Multiple authorization grant types
* Client settings
* Token settings

Example:

* `client_secret_basic`
* `client_secret_post`
* `private_key_jwt`
* `tls_client_auth`

---

## Why SAS is Better

A production OAuth2 client is much more than a database record.

It also contains:

* Security policy
* Supported grant types
* Token lifetime
* Consent requirements
* Authentication methods

Therefore SAS introduces `RegisteredClient` instead of a simple JPA entity.

---

# 2. Authorization

| Identity Lab                           | Spring Authorization Server |
| -------------------------------------- | --------------------------- |
| AuthorizationCodeEntity + RefreshToken | OAuth2Authorization         |

## Identity Lab

Authorization information is stored separately.

Examples:

* AuthorizationCodeEntity
* RefreshToken
* JWT

Each object represents one part of the authorization process.

---

## Spring Authorization Server

SAS stores the whole authorization process in one object:

`OAuth2Authorization`

It contains:

* Authorization Code
* Access Token
* Refresh Token
* ID Token
* Attributes
* Metadata
* State
* Principal Name

---

## Why SAS is Better

Instead of scattering authorization state across multiple tables and objects, SAS keeps the complete authorization lifecycle together.

This makes revocation, introspection and token management much easier.

---

# 3. Client Repository

| Identity Lab     | Spring Authorization Server |
| ---------------- | --------------------------- |
| ClientRepository | RegisteredClientRepository  |

## Identity Lab

Uses Spring Data JPA to query OAuth2 clients.

Examples:

* findByClientId()
* findByCertificateSubject()

---

## Spring Authorization Server

Uses `RegisteredClientRepository`.

Implementations:

* InMemoryRegisteredClientRepository
* JdbcRegisteredClientRepository

The storage mechanism can be replaced without changing business logic.

---

# 4. Authorization Service

| Identity Lab      | Spring Authorization Server |
| ----------------- | --------------------------- |
| Custom Repository | OAuth2AuthorizationService  |

## Identity Lab

Authorization Code and Refresh Token are managed manually.

Developers decide:

* How to save
* How to revoke
* How to expire

---

## Spring Authorization Server

Uses:

`OAuth2AuthorizationService`

Implementations:

* InMemoryOAuth2AuthorizationService
* JdbcOAuth2AuthorizationService

The service manages the complete authorization lifecycle.

---

# 5. Authorization Consent

| Identity Lab    | Spring Authorization Server       |
| --------------- | --------------------------------- |
| Not implemented | OAuth2AuthorizationConsentService |

## Identity Lab

Authorization Code is issued immediately after user authentication.

There is no consent page.

---

## Spring Authorization Server

Supports OAuth2 Consent.

The server remembers:

* User
* Client
* Approved scopes

Future authorization requests can skip the consent page.

---

# 6. Token Generation

| Identity Lab | Spring Authorization Server |
| ------------ | --------------------------- |
| JwtService   | OAuth2TokenGenerator        |

## Identity Lab

JWT generation is implemented manually.

Responsibilities:

* Access Token
* ID Token
* Signature
* Claims

---

## Spring Authorization Server

Token generation is delegated to:

* JwtGenerator
* OAuth2AccessTokenGenerator
* OAuth2RefreshTokenGenerator

These generators are composed through:

`DelegatingOAuth2TokenGenerator`

---

# 7. Endpoint Implementation

| Identity Lab     | Spring Authorization Server |
| ---------------- | --------------------------- |
| REST Controllers | Spring Security Filters     |

## Identity Lab

Endpoints are implemented as Controllers.

Examples:

* /oauth2/token
* /oauth2/authorize
* /oauth2/introspect

---

## Spring Authorization Server

Endpoints are implemented through Spring Security.

Request Flow:

```
HTTP Request
        ↓
Security Filter
        ↓
AuthenticationConverter
        ↓
AuthenticationProvider
        ↓
AuthorizationService
        ↓
TokenGenerator
        ↓
HTTP Response
```

Controllers are almost unnecessary.

---

# 8. Overall Architecture

Identity Lab focuses on learning OAuth2 principles by implementing the protocol step by step.

Spring Authorization Server focuses on extensibility, standard compliance, and production readiness.

The custom implementation helped understand:

* OAuth2
* OpenID Connect
* PKCE
* PAR
* JARM
* mTLS
* Token lifecycle

Spring Authorization Server demonstrates how these features are implemented in a production-grade framework.

---

# Learning Outcome

By implementing an Authorization Server from scratch and then studying Spring Authorization Server, the project provides both:

* A deep understanding of OAuth2/OIDC/FAPI protocols.
* Knowledge of how enterprise-grade Authorization Servers are designed and implemented.
