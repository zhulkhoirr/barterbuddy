const admin = require("../firebase");
const moment = require("moment");

const sendMessageHandler = async (request, h) => {
  const { senderId, receiverId, message } = request.payload;

  if (!senderId || !receiverId || !message) {
    return h.response({ error: "All fields are required" }).code(400);
  }

  try {
    const db = admin.firestore();
    const messageRef = db.collection("chats").doc();

    let now = moment();
    let time = now.format();
    await messageRef.set({
      senderId,
      receiverId,
      message,
      timestamp: time,
    });

    return h.response({ success: true }).code(201);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = sendMessageHandler;
