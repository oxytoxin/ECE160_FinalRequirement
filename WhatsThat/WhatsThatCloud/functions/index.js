const functions = require("firebase-functions");
const admin = require('firebase-admin');
admin.initializeApp();


// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

exports.helloWorld = functions.https.onRequest((request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase!");
});

exports.addUserToFirestore = functions.auth.user().onCreate((user)=>{
    var usersRef = admin.firestore().collection("users")
    return usersRef.doc(user.uid).set({
        "displayName" : user.displayName,
        "email" : user.email
    });
});