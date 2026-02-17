# Kiosk Print Gateway: Architecture & Summary

## **Executive Summary**
We have successfully implemented a standalone **Printer Gateway** within the `Kiosk Print` Android application. This feature allows external applications (such as a React Native app or a web browser) to trigger print jobs on a connected POS printer by sending simple HTTP requests to a local server running on the Android device.

This solution bypasses the complexities of direct React Native integration by leveraging the existing, robust native Android printer SDK (`POSPrinter`) and exposing it via a standard HTTP interface.

## **System Architecture**

### **High-Level Overview**
The system consists of an embedded HTTP server (`NanoHTTPD`) running within a **Android Foreground Service**. This ensures the server remains active even when the app is in the background. The server listens on port **8686** and accepts JSON payloads for printing.

### **Component Diagram**

```mermaid
graph TD
    Client[External Client<br>(React Native / Browser)] -->|HTTP POST /print| Server[NanoHTTPD Server<br>(Port 8686)]
    Server -->|Parse JSON| Logic[PrinterServer Logic]
    Logic -->|Check Connection| App[App Context]
    App -->|Get Connection| POS[POSConnect]
    POS -->|Send Commands| Printer[Physical Printer]
    
    subgraph Android_App ["Kiosk Print Android App"]
        Service[PrinterServerService<br>(Foreground Service)] -->|Lifecycle Management| Server
        Activity[MainActivity] -->|Starts Service &<br>Manages Connection| App
    end
```

### **Core Components**

1.  **`PrinterServerService` (Foreground Service)**
    *   **Role**: Manages the lifecycle of the HTTP server.
    *   **Behavior**: Runs as a foreground service with a persistent notification ("Kiosk Print Gateway") to prevent the Android system from killing it.
    *   **Resilience**: Configured as `START_STICKY`, meaning the system will attempt to recreate it if it is killed.

2.  **`PrinterServer` (NanoHTTPD Implementation)**
    *   **Role**: Handles incoming HTTP requests.
    *   **Port**: `8686`
    *   **Security**: Implements CORS (Cross-Origin Resource Sharing) to allow requests from any origin (essential for browser-based clients).

3.  **`MainActivity`**
    *   **Role**: Initializes the application and starts the `PrinterServerService` upon launch.
    *   **Connection**: Manages the physical connection (USB/Bluetooth/Network) to the printer via the `POSConnect` library.

## **API Reference**

### **1. Check Health**
Verifies that the server is running and reachable.

*   **Endpoint**: `GET /health`
*   **Response**:
    ```json
    {
      "status": "alive"
    }
    ```

### **2. Print Content**
Sends raw text content to be printed.

*   **Endpoint**: `POST /print`
*   **Content-Type**: `application/json`
*   **Body**:
    ```json
    {
      "content": "Hello, World!\nThis is a test print.\n"
    }
    ```
*   **Response (Success)**:
    ```json
    {
      "success": true
    }
    ```
*   **Response (Error)**:
    ```json
    {
      "error": "Printer not connected"
    }
    ```

## **Implementation Details**

### **Key Files Created/Modified**
*   **`app/build.gradle`**: Added `nanohttpd` dependency.
*   **`PrinterServer.kt`**: Implemented the server logic, request parsing, and printer command execution.
*   **`PrinterServerService.kt`**: Implemented the Android Service, Notification Channel, and lifecycle management.
*   **`AndroidManifest.xml`**: Registered the service and requested `FOREGROUND_SERVICE` permission.
*   **`MainActivity.kt`**: Added logic to start the service on app startup.

### **Next Steps**
1.  **Testing**: Verify the endpoints using the provided Postman collection or a browser.
2.  **Client Integration**: Update the client application (React Native) to send HTTP requests to `http://localhost:8686/print`.
