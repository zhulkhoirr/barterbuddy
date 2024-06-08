const admin = require("../firebase");
const stream = require("stream");
const Boom = require("@hapi/boom");

const editProfileHandler = async (request, h) => {
  const { userId } = request.params;
  const { username, email, city } = request.payload;
  const profile_img = request.payload.profile_img;

  try {
    const db = admin.firestore();
    const userRef = db.collection("users").doc(userId);

    const userDoc = await userRef.get();
    if (!userDoc.exists) {
      return h.response({ error: "User not found" }).code(404);
    }

    const userData = userDoc.data();
    let profileImageUrl = userData.profile_img;

    if (profile_img) {
      const bucket = admin.storage().bucket();

      // Delete old profile image if it exists
      if (profileImageUrl) {
        const oldImageFileName = profileImageUrl.split("/").pop();
        const oldFile = bucket.file(`${userId}/profile/${oldImageFileName}`);

        try {
          await oldFile.delete();
        } catch (error) {
          if (error.code !== 404) {
            throw error;
          }
        }
      }

      // Upload new profile image
      const newFileName = `${userId}/profile/${Date.now()}-${
        profile_img.hapi.filename
      }`;
      const newFile = bucket.file(newFileName);

      const passthroughStream = new stream.PassThrough();
      passthroughStream.end(profile_img._data);

      await new Promise((resolve, reject) => {
        passthroughStream
          .pipe(
            newFile.createWriteStream({
              metadata: {
                contentType: profile_img.hapi.headers["content-type"],
              },
            })
          )
          .on("error", reject)
          .on("finish", resolve);
      });

      profileImageUrl = `https://storage.googleapis.com/${bucket.name}/${newFileName}`;
    }

    await userRef.update({
      username,
      email,
      city,
      profile_img: profileImageUrl,
    });

    return h.response({ success: true }).code(200);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = editProfileHandler;
