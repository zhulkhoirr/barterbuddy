const admin = require('firebase-admin');

const searchHistoryHandler = async (request, h) => {
    try {
        const userId = request.params.userId;
        const db = admin.firestore();
  
        const searchHistoryRef = db.collection('users').doc(userId).collection('search_history');
        const searchHistorySnapshot = await searchHistoryRef.get();
  
        const searchHistory = searchHistorySnapshot.docs.map(doc => {
          const data = doc.data();
          const timestamp = new Date(data.timestamp._seconds * 1000); 
          return {
            keyword: data.keyword,
            timestamp: timestamp.toISOString() 
          };
        });
  
        return h.response({ searchHistory }).code(200);
      } catch (error) {
        return h.response({ error: error.message }).code(500);
      }
};

module.exports = searchHistoryHandler;