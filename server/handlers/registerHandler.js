const admin = require("firebase-admin");

const registerHandler = async (request, h) => {
  const { username, email, password, confirmPassword, city } = request.payload;

  if (password !== confirmPassword) {
    return h.response({ error: "Passwords do not match" }).code(400);
  }

  try {
    const userRecord = await admin.auth().createUser({
      email: email,
      password: password,
    });

    const db = admin.firestore();
    await db.collection("users").doc(userRecord.uid).set({
      username,
      email,
      city,
      profile_img: "",
      interested_posts: [],
      search_histories: [],
    });

    return h.response({ success: true }).code(201);
  } catch (error) {
    return h.response({ error: error.message }).code(500);
  }
};

module.exports = registerHandler;
