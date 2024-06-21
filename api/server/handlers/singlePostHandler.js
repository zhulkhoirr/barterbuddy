const admin = require("../firebase");

const singlePostHandler = async (request, h) => {
  const postId = request.params.postId;
  try {
    const db = admin.firestore();
    const postDoc = await db.collection('posts').doc(postId).get();

    if (!postDoc.exists) {
        return h.response({ error: 'User not found' }).code(404);
    }

    const postData = postDoc.data();

    return h.response({ ...postData }).code(200);
} catch (error) {
    return h.response({ error: error.message }).code(500);
}
};

module.exports = singlePostHandler;
