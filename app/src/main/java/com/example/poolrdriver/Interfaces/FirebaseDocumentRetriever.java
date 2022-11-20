package com.example.poolrdriver.Interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public interface FirebaseDocumentRetriever {

    void onSuccess(Task<QuerySnapshot> snapshotTask);
    void onError(String e);
}
