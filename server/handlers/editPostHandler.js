const admin = require("../firebase");
const stream = require("stream");

const editPostHandler = async (request, h) => {
  const { userId, postId } = request.params;
  const { title, description, status, updated_at } = request.payload;

  const imageFile = request.payload.image;

  try {
    const db = admin.firestore();
    const postRef = db
      .collection("users")
      .doc(userId)
      .collection("posts")
      .doc(postId);

    const postDoc = await postRef.get();
    if (!postDoc.exists) {
      return h.response({ error: "Post not found" }).code(404);
    }

    const postData = postDoc.data();
    let imageUrl = postData.image;

    if (imageFile) {
      const bucket = admin.storage().bucket();
      const oldImageFileName = `${postData.image.split("/").pop()}`;
      const oldFile = bucket.file(`${userId}/posts/${oldImageFileName}`);

      try {
        await oldFile.delete();
      } catch (error) {
        if (error.code === 404) {
          console.log(
            `Old image file not found in Firebase Storage: ${oldImageFileName}`
          );
        } else {
          throw error;
        }
      }

      const newFileName = `${userId}/posts/${postId}-${Date.now()}-${
        imageFile.hapi.filename
      }`;
      const newFile = bucket.file(newFileName);

      const passthroughStream = new stream.PassThrough();
      passthroughStream.end(imageFile._data);

      await new Promise((resolve, reject) => {
        passthroughStream
          .pipe(
            newFile.createWriteStream({
              metadata: {
                contentType: imageFile.hapi.headers["content-type"],
              },
            })
          )
          .on("error", reject)
          .on("finish", resolve);
      });

      imageUrl = `https://storage.googleapis.com/${bucket.name}/${newFileName}`;
    }
    updated_at = new Date().toISOString();
    await postRef.update({
      title,
      image: imageUrl,
      description,
      status,
      updated_at,
    });

    return h.response({ success: true }).code(200);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = editPostHandler;
