import { initializeApp } from "firebase/app";
import { deleteObject, getDownloadURL, getStorage, ref, uploadBytes } from "firebase/storage";
import { v4 as uuid } from "uuid";
import { getAuth } from "firebase-admin/auth";

import admin from "firebase-admin";

import * as serviceAccount from "./keep-this-private-but-not-in-this-project.json";

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount as admin.ServiceAccount)
});

const firebaseConfig = {
  apiKey: process.env.FIREBASE_API_KEY,
  authDomain: process.env.FIREBASE_AUTH_DOMAIN,
  projectId: process.env.FIREBASE_PROJECT_ID,
  storageBucket: process.env.FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.FIREBASE_APP_ID,
};

const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

const firebase = {
  uploadFile: async (data: string) => {
    // Convert data to file object
    const res = await fetch(data);
    const blob = await res.blob();
    const file = new File([blob], `${uuid()}.jpeg`, { type: "image/jpeg" });
    // Upload file
    const uploadRef = ref(storage, file.name);
    const snapshot = await uploadBytes(uploadRef, file);
    const downloadURL = await getDownloadURL(snapshot.ref);
    return downloadURL;
  },

  deleteFile: async (url: string) => {
    const deleteRef = ref(storage, url);
    deleteObject(deleteRef);
  },
};

export { getAuth };
export default firebase;
