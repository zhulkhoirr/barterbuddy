const admin = require("../firebase");
const stream = require("stream");

const postHandler = async (request, h) => {
  const userId = request.params.userId;
  const { title, description, type, status } = request.payload;
const moment = require('moment');

const postHandler = async (request, h) => {
  const userId = request.params.userId;
  const { title, description, status} = request.payload;

  const imageFile = request.payload.image;

  if (!imageFile) {
    return h.response({ error: "Image file is required" }).code(400);
  }

  try {
    const db = admin.firestore();
    // const userRef = db.collection("users").doc(userId);
    const postRef = db.collection("posts").doc();

    const id = postRef.id;

    const bucket = admin.storage().bucket();
    const fileName = `${userId}/posts/${id}-${Date.now()}-${
      imageFile.hapi.filename
    }`;
    const file = bucket.file(fileName);

    const passthroughStream = new stream.PassThrough();
    passthroughStream.end(imageFile._data);

    await new Promise((resolve, reject) => {
      passthroughStream
        .pipe(
          file.createWriteStream({
            metadata: {
              contentType: imageFile.hapi.headers["content-type"],
            },
          })
        )
        .on("error", reject)
        .on("finish", resolve);
    });

    const imageUrl = `https://storage.googleapis.com/${bucket.name}/${fileName}`;

    let now = moment();
    let createdAt = now.format();
    let updatedAt = now.format();
    
    await postRef.set({
      id,
      title,
      image: imageUrl,
      description,
      type,
      status,
      created_at: createdAt,
      updated_at: updatedAt,
      user_id: userId,
    });

    return h.response({ success: true, id }).code(201);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = postHandler;
