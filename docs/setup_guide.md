# Setup Guide: Printer Gateway

This guide covers how to set up, build, and run the `Kiosk-Printer` project with the new Printer Gateway feature.

## Prerequisites

- **Android Studio**: Latest stable version (Koala or newer recommended).
- **JDK 17**: Required for building the project.
- **Android SDK**: API Level 34 (Android 14) or compatible.
- **Physical Android Device**: Preferred for testing USB/Bluetooth printer connections. Emulators can be used for network testing but have limitations with hardware peripherals.

## Installation

1.  **Clone/Download the Repository**
    Ensure you have the latest version of the `Kiosk-Printer` project.

2.  **Open in Android Studio**
    - Launch Android Studio.
    - Select **Open** and navigate to the project root directory.
    - Allow Gradle to sync. This may take a few minutes as it downloads dependencies.

## Building and Running

1.  **Build the Project**
    - Open the **Build** menu > **Make Project**.
    - Alternatively, run `./gradlew assembleDebug` in the terminal.

2.  **Run on Device**
    - Connect your Android device via USB.
    - Click the **Run** button (green play icon) in Android Studio.
    - Select your connected device.

## Using the Printer Gateway

Once the app is running on your device:

1.  **Service Startup**:
    The service starts automatically when the app launches. You should see a persistent notification titled **"Boons Printer Gateway"** in the notification drawer.

2.  **Verify Server Status**:
    - Open a web browser on the **same device** or a computer on the **same local network**.
    - Navigate to: `http://<DEVICE_IP>:8686/health`
    - You should see: `{"status": "alive"}`

3.  **Connect to a Printer**:
    - In the app UI, select your connection type (USB, Bluetooth, Network).
    - Scan for devices if using Bluetooth/USB.
    - Click **Connect**.
    - **Crucial**: The gateway will fail to print if the app is not connected to a physical printer.

4.  **Send a Print Job**:
    - Use `curl` or Postman to send a POST request to `http://<DEVICE_IP>:8686/print` with a JSON body containing `{"content": "Test"}`.

## Permissions

The app requires the following permissions, which are declared in `AndroidManifest.xml`:

- `android.permission.INTERNET`: For the local HTTP server.
- `android.permission.FOREGROUND_SERVICE`: To keep the server running in the background.
- `android.permission.BLUETOOTH`/`BLUETOOTH_ADMIN`/`BLUETOOTH_SCAN`/`BLUETOOTH_CONNECT`: For Bluetooth printers.
- `android.permission.USB_PERMISSION`: Handled dynamically for USB printers.
