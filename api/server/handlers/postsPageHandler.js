const admin = require("../firebase");

const postsPageHandler = async (request, h) => {
  try {
    const db = admin.firestore();
    const page = parseInt(request.query.page) || 1;
    const size = parseInt(request.query.size) || 10;

    const postsRef = db.collection("posts").orderBy('updated_at', 'desc');

    let query = postsRef.limit(size);

    if (page > 1) {
      const previousPageSnapshot = await postsRef.limit((page - 1) * size).get();
      if (!previousPageSnapshot.empty) {
        const lastDoc = previousPageSnapshot.docs[previousPageSnapshot.docs.length - 1];
        query = postsRef.startAfter(lastDoc).limit(size);
      }
    }

    const snapshot = await query.get();

    if (snapshot.empty) {
      return h.response({ posts: [] }).code(200);
    }

    const posts = [];
    snapshot.forEach(doc => {
      const postData = doc.data();
      posts.push({ ...postData, id: doc.id });
    });

    return h.response({ posts }).code(200);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = postsPageHandler;
