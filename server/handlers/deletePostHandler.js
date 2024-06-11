const admin = require("../firebase");

const deletePostHandler = async (request, h) => {
  const { postId, userId } = request.params;

  try {
    const db = admin.firestore();

    // Hapus postingan dari koleksi "posts"
    const postDocRef = db.collection("posts").doc(postId);
    const postDoc = await postDocRef.get();

    if (!postDoc.exists) {
      return h.response({ error: "Post not found" }).code(404);
    }

    // Hapus gambar dari Firebase Storage
    const postData = postDoc.data();
    const imageFileName = postData.image.split("/").pop();
    const bucket = admin.storage().bucket();
    const file = bucket.file(`${userId}/posts/${imageFileName}`);

    try {
      await file.delete();
    } catch (error) {
      if (error.code === 404) {
        console.log(
          `Image file not found in Firebase Storage: ${imageFileName}`
        );
      } else {
        throw error;
      }
    }

    // Hapus postingan dari koleksi "posts"
    await postDocRef.delete();

    // Dapatkan dokumen-dokumen di koleksi "users" yang memiliki bidang "interested_posts" yang mencakup postId
    const usersQuerySnapshot = await db
      .collection("users")
      .where("interested_posts", "array-contains", postId)
      .get();

    // Untuk setiap dokumen, hapus postId dari bidang "interested_posts"
    const batch = db.batch();
    usersQuerySnapshot.forEach((doc) => {
      const interestedPosts = doc
        .data()
        .interested_posts.filter((id) => id !== postId);
      batch.update(doc.ref, { interested_posts: interestedPosts });
    });

    // Komitkan batch
    await batch.commit();

    return h.response({ success: true }).code(200);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = deletePostHandler;
