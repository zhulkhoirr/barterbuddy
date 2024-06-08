admin = require('firebase-admin');

const userPostsHandler = async (request, h) => {
    const userId = request.params.userId;

      try {
          const db = admin.firestore();
          const userRef = db.collection('users').doc(userId);
          const postsSnapshot = await userRef.collection('posts').get();

          if (postsSnapshot.empty) {
              return h.response({ success: true, posts: [] }).code(200);
          }

          const posts = [];
          postsSnapshot.forEach(doc => {
              posts.push(doc.data());
          });

          return h.response({ posts }).code(200);
      } catch (error) {
          return h.response({ error: error.message }).code(500);
      }
  
};

module.exports = userPostsHandler;