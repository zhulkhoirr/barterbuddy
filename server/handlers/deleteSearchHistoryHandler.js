const admin = require('firebase-admin');

const deleteSearchHistoryHandler = async (request, h) => {
    const { userId, entryId } = request.params;

    try {
      const db = admin.firestore();
      const searchHistoryRef = db.collection('users').doc(userId).collection('search_history').doc(entryId);

      await searchHistoryRef.delete();

      return h.response({ success: true }).code(200);
    } catch (error) {
      return h.response({ error: error.message }).code(500);
    }
};

module.exports = deleteSearchHistoryHandler;