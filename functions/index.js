//import firebase functions modules
const functions = require('firebase-functions');
//import admin module
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


// Listens for new messages added to messages/:pushId
exports.trampNotific = functions.database.ref('/trampoos/{countryUid}/Individiual/users/{userId}/{pushId}')

    .onCreate(event => {

    // Create a notification
                const payload = {
                    notification: {
                        title:"Shelter ",
                        body: "Someone need your help!",
                        sound: "default"
                    },
                };

              //Create an options object that contains the time to live for the notification and the priority
                const options = {
                    priority: "high",
                    timeToLive: 60 * 60 * 24
                };


                return admin.messaging().sendToTopic("pushNotifications", payload, options);

    });