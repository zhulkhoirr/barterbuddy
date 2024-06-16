const admin = require("../firebase");

const getChatHistoryHandler = async (request, h) => {
  const { userId1, userId2 } = request.query;

  if (!userId1 || !userId2) {
    return h.response({ error: "Both user IDs are required" }).code(400);
  }

  try {
    const db = admin.firestore();
    const chatSnapshot = await db
      .collection("chats")
      .where("senderId", "in", [userId1, userId2])
      .where("receiverId", "in", [userId1, userId2])
      .orderBy("timestamp", "asc")
      .get();

    const messages = chatSnapshot.docs.map((doc) => doc.data());

    return h.response({ messages }).code(200);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = getChatHistoryHandler;
