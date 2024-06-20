# Access Key Manager

![Access Key Manager Logo](/path/to/logo.png) <!-- Replace with your logo -->

Welcome to the Access Key Manager project repository. This web application is designed to manage access keys securely for Micro-Focus Inc.'s multitenant school management platform. Schools can purchase and manage access keys through this application.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Endpoints](#endpoints)
- [Contributors](#contributors)
- [License](#license)

## Overview

The Access Key Manager is a Spring Boot web application that facilitates the management of access keys. It supports user authentication, access key generation, role-based access control, and integration endpoints for school management software.

## Features

- **User Authentication**:
    - Sign up and log in with email and password.
    - Account verification and password reset functionality.
- **Access Key Management**:
    - CRUD operations for managing access keys (active, expired, revoked).
    - Only one active key per user at any time.
- **Role-based Access Control**:
    - Differentiates between School IT Personnel and Micro-Focus Admin roles.
    - Admins can manually revoke access keys.
- **Integration Endpoint**:
    - Endpoint for school management software to check the status of active keys.

## Technologies Used

- Java 11
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- Spring Data JPA
- Thymeleaf
- Bootstrap
- MySQL
- Maven
- Swagger

## Prerequisites

Before running the application, ensure you have the following installed:

- Java Development Kit (JDK) 11 or higher
- Maven
- MySQL Server
- IDE (IntelliJ IDEA, Eclipse, etc.)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/your/repository.git
cd access-key-manager
