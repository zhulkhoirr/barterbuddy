require('dotenv').config();
const admin = require('firebase-admin');

const loginHandler = async (request, h) => {
  const { idToken } = request.payload;

  try {
      const decodedToken = await admin.auth().verifyIdToken(idToken);
      const uid = decodedToken.uid;

      return h.response({ success: true, userId: uid }).code(200);
  } catch (error) {
      return h.response({ error: error.message }).code(500);
  }
};

module.exports = loginHandler;
