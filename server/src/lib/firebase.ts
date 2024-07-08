import { initializeApp } from "firebase/app";
import { deleteObject, getDownloadURL, getStorage, ref, uploadBytes } from "firebase/storage";
import { v4 as uuid } from "uuid";
import { getAuth as AdmGetAuth } from "firebase-admin/auth";
import { getMessaging } from "firebase-admin/messaging";

//ADMIN
import admin from "firebase-admin";

import * as serviceAccount from "./keep-this-private-but-not-in-this-project.json";

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount as admin.ServiceAccount)
});


//APP
const firebaseConfig = {
  apiKey: "AIzaSyCzBAIo1d2xkRGl8fIQpMHu9iIVmfw1nsI",
  authDomain: "tenderus-611c8.firebaseapp.com",
  projectId: "tenderus-611c8",
  storageBucket: "tenderus-611c8.appspot.com",
  messagingSenderId: "585038214587",
  appId: "1:585038214587:web:58a13de448d8f24fc1809e"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

//Storage
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



export { AdmGetAuth, getMessaging };

export default firebase;
