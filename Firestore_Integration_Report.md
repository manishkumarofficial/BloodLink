# Cloud Firestore Integration Report
**BloodLink Android Application**

## 1. Overview
The BloodLink Android application has been fully converted from a simulated local in-memory dataset to a production-ready real-time **Cloud Firestore** backend. All static mock data, placeholder lists, and local simulation layers have been audited and removed.

The data layers now speak natively to the following collection paths on Cloud Firestore with automatic real-time updates pushed directly back to the Jetpack Compose UI streams.

---

## 2. Integrated Collections & Document Layouts

### 2.1 Users Collection (`/users`)
- **Path**: `/users/{userId}`
- **Structure**:
  - `userId`: Standard authentication identifier.
  - `fullName`: First & Last name.
  - `phoneNumber`: Unique phone index.
  - `email`: Electron Mail address.
  - `role`: Actor profiles ("Donor", "Requester", "Organizer").
  - `bloodGroup`: Standard blood categorization (e.g. `O+`, `A-`).
  - `latitude`/`longitude`: Geographical location vectors.
  - `travelRadius`: Maximum willing distance to donate.
  - `availabilityStatus`: Boolean toggle for emergency requests.
  - `cooldownStatus`: Active medical eligibility tracker (`Cooldown` or `Eligible`).
  - `nextEligibleDate`: Localized date representation of recovery expiry.
  - `divisionStats`: Total lives saved and registered donation counts.
  - `heroLevel`: Gamified progression moniker depending on total points/XP.

### 2.2 Blood Requests Collection (`/blood_requests`)
- **Path**: `/blood_requests/{requestId}`
- **Structure**:
  - `id`: Document ID.
  - `requesterId`: Creator reference.
  - `hospitalName`: Intended surgical medical center.
  - `bloodGroup`: Requested group.
  - `patientStatus`: Degree of severity.
  - `quote`: Personal appeal text.
  - `targetUnits`/`currentUnits`: Allocation milestones.
  - `isCritical`: Immediate priority rating.
  - `isFulfilled`: Auto-fulfillment milestone boolean.

### 2.3 Donation History (`/donation_history`)
- **Path**: `/donation_history/{donationId}`
- **Structure**:
  - `donationId`: Unique barcode identifier (e.g., `BLD-101`).
  - `donorId`: Donor UID link.
  - `donorName`: Cache of donor name for display scanning.
  - `hospitalName`/`requestId`: Donation receipt attribution.
  - `donationDate`/`verificationTimestamp`: System receipt vectors.

### 2.4 Camps & Camp Registrations (`/camps`, `/camp_registrations`)
- **Paths**: `/camps/{campId}` & `/camp_registrations/{registrationId}`
- **Structures**:
  - `FDonationCamp`: Standard mobile camp events with locations and image assets.
  - `FCampRegistration`: Registrants with slot reservations, medical screening status, and encrypted QR payload signatures.

### 2.5 Secondary/Supporting Collections
- **`/camp_attendance`**: Detailed logging of checked-in times.
- **`/notifications`**: Direct payload log records corresponding to actions.
- **`/achievements`**: Log records for unlocked gamified progression achievements.
- **`/user_statistics`**: Real-time counter of donations, streaks, and accumulated experience (XP).
- **`/cooldowns`**: Legal requirements tracking medical recovery cooldowns.
- **`/verification_logs`**: Explict audit logging trace of verified scans.

---

## 3. Real-Time Listener Flows
Utilizing Kotlin Coroutine `callbackFlow`, snapshot listeners automatically observe changes at the database level and pipe them dynamically into state updates:
1. **`listenToActiveRequests()`**: Dispatches live updates to the Request Dashboard when a nearby request is added or fulfilled.
2. **`listenToCamps()`**: Live feed of incoming camps.
3. **`listenToCampRegistrations()`**: Keeps the user's upcoming pass list, ticketing QR codes, and current check-in/donation status up to date.
4. **`listenToUserStatistics()`**: Connects experience counters and levels across screens.

---

## 4. Robust Exception & Error Resolution
All operations are fully wrapped in structured exception blocks to map and report failures:
- **Network Interruptions**: Automatically handled through Offline persistence, logging, and state flows.
- **Security & Authorization Failures**: Checked and logged with explicit TAG identifiers (`BloodRepository` / `FirebaseConfigService`).
- **Duplicate Prevention**: QR logs and barcodes do document existence lookups prior to logging to ensure no double-submissions.
