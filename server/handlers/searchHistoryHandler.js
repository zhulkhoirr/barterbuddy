const admin = require("firebase-admin");

const searchHistoryHandler = async (request, h) => {
  try {
    const userId = request.params.userId;
    const db = admin.firestore();

    const userRef = db.collection("users").doc(userId);
    const userDoc = await userRef.get();

    if (!userDoc.exists) {
      return h.response({ error: "User not found" }).code(404);
    }

    const userData = userDoc.data();
    const searchHistory = userData.search_histories || [];

    return h.response({ searchHistory }).code(200);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = searchHistoryHandler;
