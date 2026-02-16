# Printer Gateway API Reference

This document provides detailed information about the REST API endpoints exposed by the Printer Gateway (NanoHTTPD) running within the Android application.

## Base URL

By default, the server runs on port **8686**.
`http://<ANDROID_DEVICE_IP>:8686`

## Authentication
**Header:** `X-Api-Key`
**Value:** `Boons_Secure_Print_Kiosk_2025!`

All `POST /print` requests must include this header.

## Endpoints

### 1. Health Check

Checks if the Printer Gateway service is running and responsive.

- **URL**: `/health`
- **Method**: `GET`
- **Auth**: None
- **Headers**: None

#### Success Response

- **Code**: `200 OK`
- **Content-Type**: `application/json`
- **Body**:
```json
{
  "status": "alive"
}
```

---

---

### 2. Download Logs

Retrieves the application logs for debugging purposes.

- **URL**: `/logs`
- **Method**: `GET`
- **Auth**: None
- **Headers**: None

#### Success Response

- **Code**: `200 OK`
- **Content-Type**: `text/plain`
- **Body**: Raw text content of the application logs (filtered by relevant system tags).

---

### 3. Print Content

Sends text content to the connected printer.

- **URL**: `/print`
- **Method**: `POST`
- **Auth**: None
- **Headers**:
    - `Content-Type: application/json`

#### Request Body

| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `content` | string | Yes | The raw text string to be printed. Supports newline `\n` characters. |

**Example Body:**
```json
{
  "content": "Hello, World!\n\nThis is a test print via the Gateway.\n\n"
}
```

#### Success Response

- **Code**: `200 OK`
- **Content-Type**: `application/json`
- **Body**:
```json
{
  "success": true
}
```

#### Error Responses

**Printer Not Connected:**

- **Code**: `500 Internal Server Error`
- **Body**:
```json
{
  "error": "Printer not connected"
}
```

**Missing Content:**

- **Code**: `500 Internal Server Error`
- **Body**:
```json
{
  "error": "Content is empty"
}
```

**Invalid JSON:**

- **Code**: `500 Internal Server Error`
- **Body**:
```json
{
  "error": "Failed to parse body: ..."
}
```

## CORS Support

The server supports Cross-Origin Resource Sharing (CORS) to allow requests from web browsers.

- **Access-Control-Allow-Origin**: `*`
- **Access-Control-Allow-Methods**: `GET, POST, PUT, DELETE, OPTIONS`
- **Access-Control-Allow-Headers**: `Content-Type, Authorization, X-Requested-With`
