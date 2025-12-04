const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendChatNotification = onDocumentCreated(
    "groups/{groupId}/messages/{messageId}",
    async (event) => {

        if (!event.data) {
            return console.log("Evento sin datos. Cancelando.");
        }

        const messageData = event.data.data();
        const groupId = event.params.groupId;
        const { senderId, message: text, senderName } = messageData;

        if (!senderId) {
             return console.log('Message has no sender ID. Aborting.');
        }
        const usersSnapshot = await admin.firestore().collection("users").get();

        const uniqueTokens = new Set();

        usersSnapshot.forEach(doc => {
            const userData = doc.data();
            if (doc.id !== senderId && userData.fcmToken) {
                uniqueTokens.add(userData.fcmToken);
            }
        });

        const tokens = Array.from(uniqueTokens);

        if (tokens.length === 0) {
            return console.log("No valid tokens found to send to.");
        }

        const messagePayload = {
            tokens: tokens,
            notification: {
                title: `Nuevo mensaje de ${senderName}`,
                body: text.length > 50 ? text.substring(0, 50) + "..." : text,
            },
            data: {
                groupId: groupId,
                click_action: "FLUTTER_NOTIFICATION_CLICK"
            },
            android: {
                notification: {
                    clickAction: "FLUTTER_NOTIFICATION_CLICK"
                }
            }
        };

        try {
             const response = await admin.messaging().sendEachForMulticast(messagePayload);
             console.log(response.successCount + ' messages were sent successfully');
             return null;
        } catch (error) {
             console.error('Error sending message:', error);
             return null;
        }
    });