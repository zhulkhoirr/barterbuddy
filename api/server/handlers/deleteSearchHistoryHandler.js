const admin = require("../firebase");

const deleteSearchHistoryHandler = async (request, h) => {
  const { userId, index: entryIndex } = request.params;

  try {
    const db = admin.firestore();
    const userRef = db.collection("users").doc(userId);

    const userDoc = await userRef.get();

    if (!userDoc.exists) {
      return h.response({ error: "User not found" }).code(404);
    }

    let searchHistories = userDoc.data().search_histories || [];

    searchHistories.splice(entryIndex, 1);

    await userRef.update({ search_histories: searchHistories });

    return h.response({ success: true }).code(200);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = deleteSearchHistoryHandler;
