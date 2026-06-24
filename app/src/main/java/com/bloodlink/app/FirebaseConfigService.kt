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

    private var _isFallback = false
    val isFallback: Boolean
        get() {
            if (_isFallback) return true
            return try {
                val app = FirebaseApp.getInstance()
                val projectId = app.options.projectId ?: ""
                val apiKey = app.options.apiKey ?: ""
                val appId = app.options.applicationId ?: ""
                projectId.contains("placeholder", ignoreCase = true) ||
                        apiKey.contains("placeholder", ignoreCase = true) ||
                        apiKey == "PLACEHOLDER_API_KEY" ||
                        appId.contains("placeholder", ignoreCase = true) ||
                        appId == "PLACEHOLDER_APP_ID"
            } catch (t: Throwable) {
                true
            }
        }

    private var appContext: Context? = null

    private fun getEmergencyContext(): Context? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread")
            val activityThread = currentActivityThreadMethod.invoke(null)
            val getApplicationMethod = activityThreadClass.getMethod("getApplication")
            val ctx = getApplicationMethod.invoke(activityThread) as? Context
            Log.d(TAG, "getEmergencyContext: retrieved context = $ctx")
            ctx
         } catch (e: Exception) {
            Log.w(TAG, "getEmergencyContext: failed to get context: ${e.message}")
            null
        }
    }

    private fun isFirebaseAppInitialized(context: Context): Boolean {
        return try {
            val app = FirebaseApp.getInstance()
            Log.d(TAG, "isFirebaseAppInitialized: FirebaseApp.getInstance() returned $app")
            true
        } catch (e: IllegalStateException) {
            Log.d(TAG, "isFirebaseAppInitialized: FirebaseApp is not initialized (IllegalStateException): ${e.message}")
            false
        } catch (t: Throwable) {
            Log.d(TAG, "isFirebaseAppInitialized: FirebaseApp check throw: ${t.message}")
            false
        }
    }

    private fun ensureInitialized() {
        val ctx = appContext ?: getEmergencyContext()
        Log.d(TAG, "ensureInitialized: ctx = $ctx, appContext = $appContext")
        if (ctx != null) {
            val isInit = isFirebaseAppInitialized(ctx)
            Log.d(TAG, "ensureInitialized: isFirebaseAppInitialized checked = $isInit")
            if (!isInit) {
                _isInitialized = false
                initialize(ctx)
            } else {
                _isInitialized = true
            }
        } else {
            Log.w(TAG, "ensureInitialized: ERROR - Both appContext and emergencyContext are NULL!")
        }
    }

    @Synchronized
    fun initialize(context: Context) {
        _isFallback = false
        Log.i(TAG, "initialize called: context = $context, lifecycle appContext = $appContext")
        if (appContext == null) {
            appContext = context.applicationContext
            Log.d(TAG, "initialize: appContext set to $appContext")
        }
        
        val ctx = appContext ?: context
        val isInit = isFirebaseAppInitialized(ctx)
        Log.d(TAG, "initialize: isInit = $isInit")
        if (isInit) {
            _isInitialized = true
            Log.i(TAG, "initialize: Default FirebaseApp already initialized, setting flag to true and skipping.")
            return
        }

        _isInitialized = false
        println("FirebaseConfigService: Starting FirebaseConfigService initialization...")

        var apiKey = ""
        var projectId = ""
        var appId = ""
        var storageBucket = ""
        var databaseUrl = ""
        var gcmSenderId = ""

        // Safe reads from BuildConfig to completely eliminate any ClassNotFound/NoClassDef/NoSuchField issues
        try {
            apiKey = BuildConfig.FIREBASE_API_KEY
        } catch (t: Throwable) {
            println("FirebaseConfigService: Could not read FIREBASE_API_KEY from BuildConfig: ${t.message}")
        }
        try {
            projectId = BuildConfig.FIREBASE_PROJECT_ID
        } catch (t: Throwable) {
            println("FirebaseConfigService: Could not read FIREBASE_PROJECT_ID from BuildConfig: ${t.message}")
        }
        try {
            appId = BuildConfig.FIREBASE_APPLICATION_ID
        } catch (t: Throwable) {
            println("FirebaseConfigService: Could not read FIREBASE_APPLICATION_ID from BuildConfig: ${t.message}")
        }
        try {
            storageBucket = BuildConfig.FIREBASE_STORAGE_BUCKET
        } catch (t: Throwable) {
            println("FirebaseConfigService: Could not read FIREBASE_STORAGE_BUCKET from BuildConfig: ${t.message}")
        }
        try {
            databaseUrl = BuildConfig.FIREBASE_DATABASE_URL
        } catch (t: Throwable) {
            println("FirebaseConfigService: Could not read FIREBASE_DATABASE_URL from BuildConfig: ${t.message}")
        }
        try {
            gcmSenderId = BuildConfig.FIREBASE_GCM_SENDER_ID
        } catch (t: Throwable) {
            println("FirebaseConfigService: Could not read FIREBASE_GCM_SENDER_ID from BuildConfig: ${t.message}")
        }

        val isValidConfig = apiKey.isNotBlank() && apiKey != "PLACEHOLDER_API_KEY" &&
                projectId.isNotBlank() && projectId != "PLACEHOLDER_PROJECT_ID" &&
                appId.isNotBlank() && appId != "PLACEHOLDER_APP_ID"

        println("FirebaseConfigService: Loaded config: isValidConfig=$isValidConfig, apiKeyLength=${apiKey.length}, projectId='$projectId', appId='$appId'")

        if (isValidConfig) {
            try {
                println("FirebaseConfigService: Found valid custom configuration. Building FirebaseOptions...")
                val builder = FirebaseOptions.Builder()
                    .setApiKey(apiKey)
                    .setProjectId(projectId)
                    .setApplicationId(appId)

                if (storageBucket.isNotBlank() && storageBucket != "PLACEHOLDER_STORAGE_BUCKET") {
                    builder.setStorageBucket(storageBucket)
                }
                if (databaseUrl.isNotBlank() && databaseUrl != "PLACEHOLDER_DATABASE_URL") {
                    builder.setDatabaseUrl(databaseUrl)
                }
                if (gcmSenderId.isNotBlank() && gcmSenderId != "PLACEHOLDER_GCM_SENDER_ID") {
                    builder.setGcmSenderId(gcmSenderId)
                }

                FirebaseApp.initializeApp(ctx, builder.build())
                _isInitialized = true
                println("FirebaseConfigService: Firebase SDK initialized successfully using custom credentials for project: $projectId")
                return
            } catch (t: Throwable) {
                if (t.message?.contains("already exists", ignoreCase = true) == true) {
                    _isInitialized = true
                    println("FirebaseConfigService: Firebase SDK already initialized customly (already exists exception).")
                    return
                }
                println("FirebaseConfigService: Failed standard initialization with custom credentials: ${t.message}")
                t.printStackTrace()
            }
        }

        // Try standard resource-based initialization (google-services.json)
        try {
            println("FirebaseConfigService: Attempting default resource-based initialization...")
            val app = FirebaseApp.initializeApp(ctx)
            if (app != null) {
                _isInitialized = true
                println("FirebaseConfigService: Firebase SDK initialized successfully using default google-services.json resources.")
                return
            } else {
                println("FirebaseConfigService: Default resource-based init returned null (missing/invalid resources). Initializing default placeholder FirebaseApp...")
            }
        } catch (t: Throwable) {
            if (t.message?.contains("already exists", ignoreCase = true) == true) {
                _isInitialized = true
                println("FirebaseConfigService: Firebase SDK already initialized standardly (already exists exception).")
                return
            }
            println("FirebaseConfigService: Default resource-based init failed (${t.message}). Initializing default placeholder FirebaseApp...")
        }

        // Guaranteed fallback to default placeholder FirebaseApp to ensure Firebase services can resolve without crashes
        try {
            _isFallback = true
            println("FirebaseConfigService: Fallback initializing default static placeholder...")
            val builder = FirebaseOptions.Builder()
                .setApiKey("AIzaSyBloodLinkPlaceholderApiKey123456")
                .setProjectId("bloodlink-placeholder-id")
                .setApplicationId("1:123456789012:android:abcdef1234567890")
            val app = FirebaseApp.initializeApp(ctx, builder.build())
            if (app != null) {
                _isInitialized = true
                println("FirebaseConfigService: Firebase SDK initialized successfully with placeholder fallback configuration.")
            } else {
                println("FirebaseConfigService: Fallback initializing returned null.")
                _isInitialized = false
            }
        } catch (t: Throwable) {
            _isFallback = true
            if (t.message?.contains("already exists", ignoreCase = true) == true) {
                _isInitialized = true
                println("FirebaseConfigService: Firebase SDK already initialized layout (already exists exception during fallback).")
                return
            }
            println("FirebaseConfigService: Critical Fatal: Failed to initialize placeholder FirebaseApp: ${t.message}")
            t.printStackTrace()
            _isInitialized = false
        }
    }

    /**
     * Retrieves the instance of FirebaseAuth safely.
     */
    val auth: FirebaseAuth?
        get() = try {
            ensureInitialized()
            val inst = FirebaseAuth.getInstance()
            println("FirebaseConfigService: Successfully retrieved FirebaseAuth instance: $inst")
            inst
        } catch (t: Throwable) {
            println("FirebaseConfigService: Error getting FirebaseAuth instance: ${t.message}")
            t.printStackTrace()
            try {
                ensureInitialized()
                val inst2 = FirebaseAuth.getInstance()
                println("FirebaseConfigService: Successfully retrieved FirebaseAuth instance on retry: $inst2")
                inst2
            } catch (t2: Throwable) {
                println("FirebaseConfigService: Secondary auth init failed: ${t2.message}")
                t2.printStackTrace()
                null
            }
        }

    /**
     * Retrieves the instance of FirebaseFirestore safely.
     */
    val firestore: FirebaseFirestore?
        get() = try {
            ensureInitialized()
            val inst = FirebaseFirestore.getInstance()
            println("FirebaseConfigService: Successfully retrieved FirebaseFirestore instance: $inst")
            inst
        } catch (t: Throwable) {
            println("FirebaseConfigService: Error getting FirebaseFirestore instance: ${t.message}")
            t.printStackTrace()
            try {
                ensureInitialized()
                val inst2 = FirebaseFirestore.getInstance()
                println("FirebaseConfigService: Successfully retrieved FirebaseFirestore instance on retry: $inst2")
                inst2
            } catch (t2: Throwable) {
                println("FirebaseConfigService: Secondary firestore init failed: ${t2.message}")
                t2.printStackTrace()
                null
            }
        }

    /**
     * Retrieves the instance of FirebaseMessaging safely.
     */
    val messaging: FirebaseMessaging?
        get() = try {
            ensureInitialized()
            val inst = FirebaseMessaging.getInstance()
            println("FirebaseConfigService: Successfully retrieved FirebaseMessaging instance: $inst")
            inst
        } catch (t: Throwable) {
            println("FirebaseConfigService: Error getting FirebaseMessaging instance: ${t.message}")
            try {
                ensureInitialized()
                val inst2 = FirebaseMessaging.getInstance()
                println("FirebaseConfigService: Successfully retrieved FirebaseMessaging instance on retry: $inst2")
                inst2
            } catch (t2: Throwable) {
                println("FirebaseConfigService: Secondary messaging init failed: ${t2.message}")
                null
            }
        }

    /**
     * Retrieves the instance of FirebaseStorage safely.
     */
    val storage: FirebaseStorage?
        get() = try {
            ensureInitialized()
            val inst = FirebaseStorage.getInstance()
            println("FirebaseConfigService: Successfully retrieved FirebaseStorage instance: $inst")
            inst
        } catch (t: Throwable) {
            println("FirebaseConfigService: Error getting FirebaseStorage instance: ${t.message}")
            try {
                ensureInitialized()
                val inst2 = FirebaseStorage.getInstance()
                println("FirebaseConfigService: Successfully retrieved FirebaseStorage instance on retry: $inst2")
                inst2
            } catch (t2: Throwable) {
                println("FirebaseConfigService: Secondary storage init failed: ${t2.message}")
                null
            }
        }
}
