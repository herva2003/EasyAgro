package com.puc.easyagro.services;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageFirebase {

    private static final String TAG = "StorageFirebase";
    private static final String STORAGE_PATH = "images/";

    private FirebaseStorage storage;
    private StorageReference storageReference;

    public StorageFirebase() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(STORAGE_PATH);
    }

    public void uploadImage(Uri imageUri, final OnImageUploadListener listener) {
        StorageReference imageRef = storageReference.child(System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        listener.onSuccess(downloadUri.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e(TAG, "Falha ao obter URL da imagem", e);
                                        listener.onFailure(e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Falha ao carregar a imagem
                        Log.e(TAG, "Falha ao carregar a imagem", e);
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }
}
