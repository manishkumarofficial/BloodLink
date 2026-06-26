package com.bloodlink.app

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

/**
 * Configuration Service managing connection initialization to real Firebase backend services
 * such as Firebase Auth, Cloud Firestore, Cloud Messaging, and Firebase Storage.
 */
object FirebaseConfigService {
    private const val TAG = "FirebaseConfigService"

    private var _isInitialized = false
    val isInitialized: Boolean
        get() = _isInitialized

    private var appContext: Context? = null

    @Synchronized
    fun initialize(context: Context) {
        Log.i(TAG, "initialize called: context = $context")
        if (appContext == null) {
            appContext = context.applicationContext
        }
        
        val ctx = appContext ?: context
        try {
            if (FirebaseApp.getApps(ctx).isEmpty()) {
                FirebaseApp.initializeApp(ctx)
                Log.i(TAG, "FirebaseApp initialized successfully via standard resource loading (google-services.json).")
            } else {
                Log.i(TAG, "FirebaseApp already initialized.")
            }
            _isInitialized = true
        } catch (t: Throwable) {
            Log.e(TAG, "Critical Fatal: Failed to initialize FirebaseApp: ${t.message}", t)
            _isInitialized = false
        }
    }

    /**
     * Retrieves the instance of FirebaseAuth safely.
     */
    val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (t: Throwable) {
            Log.e(TAG, "Error getting FirebaseAuth instance: ${t.message}", t)
            null
        }

    /**
     * Retrieves the instance of FirebaseFirestore safely.
     */
    val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (t: Throwable) {
            Log.e(TAG, "Error getting FirebaseFirestore instance: ${t.message}", t)
            null
        }

    /**
     * Retrieves the instance of FirebaseMessaging safely.
     */
    val messaging: FirebaseMessaging?
        get() = try {
            FirebaseMessaging.getInstance()
        } catch (t: Throwable) {
            Log.e(TAG, "Error getting FirebaseMessaging instance: ${t.message}", t)
            null
        }

    /**
     * Retrieves the instance of FirebaseStorage safely.
     */
    val storage: FirebaseStorage?
        get() = try {
            FirebaseStorage.getInstance()
        } catch (t: Throwable) {
            Log.e(TAG, "Error getting FirebaseStorage instance: ${t.message}", t)
            null
        }

    /**
     * Runs a Firestore write with comprehensive logging as requested by audit guidelines.
     */
    suspend fun <T> runLoggedWrite(
        collectionName: String,
        documentId: String,
        data: T,
        writeBlock: suspend () -> Unit
    ) {
        try {
            // 9. Log FirebaseOptions before write
            try {
                val app = FirebaseApp.getInstance()
                Log.d(TAG, "[FIRESTORE WRITE] Firebase Project ID: ${app.options.projectId}")
                Log.d(TAG, "[FIRESTORE WRITE] Firebase App ID: ${app.options.applicationId}")
                Log.d(TAG, "[FIRESTORE WRITE] Firebase API Key: ${app.options.apiKey}")
            } catch (t: Throwable) {
                Log.e(TAG, "[FIRESTORE WRITE] Failed to log FirebaseApp options: ${t.message}", t)
            }

            // 10. Log details before writing
            if (collectionName == "users") {
                Log.d(TAG, "[FIRESTORE WRITE] Writing User Document:")
                Log.d(TAG, "  - Collection name: $collectionName")
                Log.d(TAG, "  - Document ID: $documentId")
                Log.d(TAG, "  - Complete user object: $data")
            } else {
                Log.d(TAG, "[FIRESTORE WRITE] Writing Document:")
                Log.d(TAG, "  - Collection name: $collectionName")
                Log.d(TAG, "  - Document ID: $documentId")
            }

            // Execute actual write block
            writeBlock()
        } catch (e: Exception) {
            // 11. Print complete stack trace without replacing or shortening
            Log.e(TAG, "[FIRESTORE WRITE ERROR] Complete stack trace of exception in collection '$collectionName', document '$documentId':")
            e.printStackTrace()
            throw e
        }
    }
}
