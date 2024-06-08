const admin = require('firebase-admin');

const allPostsHandler = async (request, h) => {
    try {
        const db = admin.firestore();
        const postsSnapshot = await db.collectionGroup('posts').get();

        const posts = [];
        postsSnapshot.forEach(doc => {
            const postData = doc.data();
            const userId = doc.ref.parent.parent.id;
            posts.push({ userId, ...postData });
        });

        return h.response({ posts }).code(200);
    } catch (error) {
        return h.response({ error: error.message }).code(500);
    }
};

module.exports = allPostsHandler;