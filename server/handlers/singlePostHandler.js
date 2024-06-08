const admin = require('firebase-admin');

const singlePostHandler = async (request, h) => {
    const postId = request.params.postId;

    try {
      const db = admin.firestore();
      const postsSnapshot = await db.collectionGroup('posts').where('id', '==', postId).get();

      if (postsSnapshot.empty) {
        return h.response({ error: 'Post not found' }).code(404);
      }

      const post = postsSnapshot.docs[0].data();
      const userId = postsSnapshot.docs[0].ref.parent.parent.id;

      return h.response({ userId, ...post }).code(200);
    } catch (error) {
      return h.response({ error: error.message }).code(500);
    }
};

module.exports = singlePostHandler;