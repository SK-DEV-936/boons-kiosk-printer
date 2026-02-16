# Troubleshooting Guide

Common issues and solutions for the Printer Gateway.

## Server Issues

### 1. Server Not Reachable
**Symptoms**: `Connection Refused` or timeout when acting `http://<IP>:8686/health`.

**Solutions**:
- **Check Notification**: Ensure the "Boons Printer Gateway" notification is visible in the status bar. If not, restart the app.
- **Network Isolation**: If testing from a computer, ensure both the Android device and computer are on the **same Wi-Fi network**.
- **Firewall**: Some public/corporate Wi-Fi networks block peer-to-peer communication. Try using a mobile hotspot or a private router.
- **Emulator**: If using an emulator, you must map the port. Run: `adb forward tcp:8686 tcp:8686` and access via `localhost:8686`.

### 2. Port Address In Use
**Symptoms**: Error log `BindException: Address already in use`.

**Solutions**:
- The service tries to restart cleanly, but if the app crashed, the port might be held.
- **Force Stop** the app from Android Settings and restart it.
- Restart the Android device.

## Printer Issues

### 1. "Printer not connected" Error
**Symptoms**: Use receives `{"error": "Printer not connected"}` when calling `/print`.

**Solutions**:
- The Gateway relies on the *foreground app's* connection state.
- **Open the App**: Ensure the app is running and you have explicitly clicked **Connect** on the main screen.
- **Check Status**: look for "Connected" status in the app UI.
- **Cable/Power**: Verify the printer is powered on and cables are secure.

### 3. "Failed to Fetch" (Same Device Testing)

If you are running the **App** and the **Browser (Test Page)** on the **same Android device**:

*   **Issue**: Android apps pause network activities when in the background to save battery.
*   **Solution 1 (Recommended)**: Use **Split Screen Mode**. Open the Printer App on top and Chrome on the bottom. This keeps the app active.
*   **Solution 2**: Disable Battery Optimization for the app (`Settings > Apps > Boons Printer > Battery > Unrestricted`).
*   **Issue**: `localhost` might not resolve correctly in Chrome on Android.
*   **Solution**: Use `http://127.0.0.1:8686` instead of `http://localhost:8686`.

### 2. Gibberish Printing
**Symptoms**: Printer outputs random characters instead of text.

**Solutions**:
- **Baud Rate**: If using Serial connection, ensure the baud rate matches the printer's dip switch settings (commonly 9600 or 115200).
- **Command Set**: This gateway sends raw text. If your printer requires specific command codes (ESC/POS, TSPL), the current implementation might need modification to accept raw bytes or specific formats.

## Logs

To debug issues, use **Logcat** in Android Studio:

```bash
adb logcat -s PrinterService PrinterServer
```

This will filter logs specifically for the Gateway service and server implementation.
