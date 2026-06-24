# Missing Backend Features Report
**BloodLink Android Application**

While the client-side system and Firestore database linkages are now fully integrated and operational, bridging the application to full production scale requires the implementation of several auxiliary server-side services. This document outlines the gaps, workflows, and suggested systems to transition these elements into enterprise production.

---

## 1. Automated SMS Gateway / OTP Service (OTP Verification)
* **Current Mock**: The client-side application generates a 6-digit random token and displays it in a "Simulated Gateway" container or matches with a fallback "123456" mock code for preview and automated testing.
* **Production Recommendation**:
  * Integrate an external API gateway (e.g., **Twilio**, **Telesign**, or **Firebase Phone Authentication**).
  * Use **Cloud Functions for Firebase** to generate, hashing-store, and trigger the SMS delivery logic securely to prevent client-side credential exposure.
  * **Workflow**:
    1. Client posts mobile number to API.
    2. Server generates secure token and dispatches Twilio trigger.
    3. Client supplies verification input; server evaluates and marks authenticated session.

---

## 2. Dynamic Distance Calculations & Geographical Queries
* **Current Mock**: Blood requests and camps display preset distance strings (e.g. `1.2 km away`, `2.4 mi`).
* **Production Recommendation**:
  * Real-time GPS coordinates are stored in the user profile and campsite databases.
  * Integrate **Google Maps Places / Geocoding SDK** on the client to obtain clean device coordinates.
  * Implement **Geohashing indices** in firestore (using libraries such as `geofire` or geoqueries inside Cloud Functions) to compute visual distance offsets dynamically on the fly based on the client's current device latitude/longitude coordinates.

---

## 3. Cryptographically Secure QR Scanning
* **Current Mock**: Camp registrations encode data in a plaintext pipe-separated format: `CAMP_ID:1|REG_ID:CR-101|DONOR_ID:donor_2...`
* **Production Recommendation**:
  * Encrypt QR code payloads using asymmetric cryptographic signatures (RSA / ECC) or short-lived token tokens (JWT).
  * The scanning terminal validates the server-signed token against the public key to prevent rogue actors from generating forged donation passes.

---

## 4. Server-Driven Push Notification Dispatcher
* **Current Mock**: Notifications are generated and logged as raw documents within the client-side updates.
* **Production Recommendation**:
  * Set up **Firebase Cloud Messaging (FCM)**.
  * Deploy background triggers on Firestore database changes (e.g. `onDocumentCreated` on `blood_requests`) using Node.js/TypeScript in Cloud Functions.
  * Dynamically push notification alerts directly to the device notifications shade for all eligible nearby donors who match the requested blood group.
