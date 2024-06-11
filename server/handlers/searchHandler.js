const admin = require("firebase-admin");

const searchHandler = async (request, h) => {
  let { keyword } = request.query;
  keyword = keyword.toLowerCase();

  try {
    const userId = request.params.userId;
    const db = admin.firestore();

    const userRef = db.collection("users").doc(userId);
    await userRef.update({
      search_histories: admin.firestore.FieldValue.arrayUnion({
        keyword: keyword,
      }),
    });

    const postsQuerySnapshot = await db.collectionGroup("posts").get();

    const posts = [];
    postsQuerySnapshot.forEach((doc) => {
      const postData = doc.data();
      const title = postData.title.toLowerCase();
      if (title.includes(keyword)) {
        posts.push({
          // userId: doc.ref.parent.parent.id,
          // postId: doc.id,
          ...postData,
        });
      }
    });

    return h.response({ posts }).code(200);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = searchHandler;
