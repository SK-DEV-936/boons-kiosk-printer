# Task: Build Standalone Printer Gateway

- [x] **Setup Printer Gateway Project** <!-- id: 0 -->
    - [x] Locate `Kiosk-Printer` project
    - [x] Create `docs` folder
    - [x] Create Implementation Plan (`docs/nano_server_implementation.md`)
    - [x] Verify build (./gradlew assembleDebug)

- [x] **Implement Nano Server** <!-- id: 1 -->
    - [x] Add `nanohttpd` dependency
    - [x] Create `PrinterServer.kt` (Port 8686)
    - [x] Create `PrinterServerService.kt` (Foreground Service, Sticky)
    - [x] Register Service in `AndroidManifest.xml`
    - [x] Implement `POST /print` endpoint

- [/] **Verification (Browser Only)** <!-- id: 2 -->
    - [ ] Install Gateway App on Simulator/Device
    - [ ] Start Service
    - [ ] Test `GET /health` via Chrome
    - [ ] Test `POST /print` via Chrome Console

- [x] **Documentation** <!-- id: 3 -->
    - [x] Create Architecture and Summary Document
    - [x] Create Setup Guide (`docs/setup_guide.md`)
    - [x] Create `architecture_and_summary.md`
- [x] Create `api_reference.md`
- [x] Create `setup_guide.md`
- [x] Create `troubleshooting.md`
- [x] Create `printer-test.html` browser tester (with formatted receipt support)
- [x] Implement Mock Mode for Simulator E2E Testing
- [/] **Verify and Troubleshoot Connection**
    - [x] Fix Android 14 App Crash (Downgraded targetSdk)
    - [x] Verify App Launch on Simulator
    - [x] Verify E2E Flow with Mock Mode
    - [ ] Enable Cleartext Traffic for Local Network Access
    - [ ] Verify Kiosk Hardware Connection from Laptop
