const admin = require("../firebase");

const interestPostHandler = async (request, h) => {
  const userId = request.params.userId;
  const postId = request.params.postId;

  try {
    const db = admin.firestore();
    const userRef = db.collection("users").doc(userId);
    const postRef = db.collection("posts").doc(postId);

    await db.runTransaction(async (transaction) => {
      const userDoc = await transaction.get(userRef);
      const postDoc = await transaction.get(postRef);

      if (!userDoc.exists || !postDoc.exists) {
        throw new Error("User or Post not found");
      }

      const userInterests = userDoc.data().interested_posts || [];
      if (!userInterests.includes(postId)) {
        transaction.update(userRef, {
          interested_posts: admin.firestore.FieldValue.arrayUnion(postId),
        });

        transaction.update(postRef, {
          interest_count: admin.firestore.FieldValue.increment(1),
        });
      }
    });
    return h.response({ success: true }).code(201);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = interestPostHandler;
