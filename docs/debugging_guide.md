# Debugging Guide for Kiosk Printer Gateway

If the Printer Gateway is not working on the real Kiosk device, follow these steps to capture logs and identify the issue.

## 1. Prerequisites

- Ensure **USB Debugging** is enabled on the Kiosk device.
- Connect the Kiosk device to your computer via USB.
- Ensure `adb` is installed and the device is recognized:
  ```bash
  adb devices
  ```

## 2. Capture Logs

We have created a script to automatically capture relevant logs.

1.  Open your terminal in the project directory:
    ```bash
    cd "/Users/sanjivankumar/boons-work/Printer Work/Kiosk-Printer"
    ```
2.  Make the script executable:
    ```bash
    chmod +x collect_logs.sh
    ```
3.  Run the script:
    ```bash
    ./collect_logs.sh
    ```
4.  **Reproduce the Issue**:
    - While the script is running, try to send a print job to the Kiosk.
    - Wait for the failure to occur.
5.  **Stop Logging**:
    - Press `Ctrl+C` in the terminal to stop the script.
6.  **Check Output**:
    - The logs are saved to `printer_logs.txt`.
    - Open this file and look for "PrinterServer" or "Error" messages.

## 3. Common Error Messages

| Log Message | Meaning | Action |
| :--- | :--- | :--- |
| `Printer not connected` | The app believes no printer is connected. | Ensure the app is open and "Connect" was clicked. Check USB cables. |
| `Connection refused` | The server isn't running or port is blocked. | Restart the app. Check if another app uses port 8686. |
| `Body Parse Error` | The JSON sent was malformed. | Validate your JSON payload. |
| `BindException` | Port 8686 is already in use. | Force stop the app and restart it. |

## 4. Share Logs

If you cannot resolve the issue, please share the `printer_logs.txt` file with the development team.
