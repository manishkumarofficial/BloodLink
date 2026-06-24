package com.bloodlink.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun testFirebaseInitialization() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    
    // Explicitly initialize config service
    FirebaseConfigService.initialize(context)
    
    // Assert Firestore can be retrieved without throwing or failing
    try {
      val firestore = FirebaseConfigService.firestore
      println("Test: firestore instance = $firestore")
      assertNotNull("Firestore should not be null", firestore)
    } catch (e: Throwable) {
      println("Test caught exception during Firestore retrieval:")
      e.printStackTrace()
      throw e
    }
  }
}
