package db

import java.io.FileInputStream

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.{FirebaseApp, FirebaseOptions}

class FirebaseSetup {
  // to check if the firebaseApp has existed
  FirebaseApp.getApps().stream().filter(a => a.getName == FirebaseApp.DEFAULT_APP_NAME).findFirst().orElseGet(
      () => {
         val serviceAccount = new FileInputStream("D:\\yulia\\firstbus\\telebot\\adminapi\\cusreview.json")
        val credentials = GoogleCredentials.fromStream(serviceAccount)
        val options = new FirebaseOptions.Builder()
          .setCredentials(credentials)
          .build()

        FirebaseApp.initializeApp(options, FirebaseApp.DEFAULT_APP_NAME)
      }
    )
    val db = FirestoreClient.getFirestore()
    
}