require('dotenv').config();
const admin = require('firebase-admin');
const axios = require('axios');

const loginHandler = async (request, h) => {
    const { email, password } = request.payload;

    try {
      const apiKey = process.env.API_KEY;
      const url = `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${apiKey}`;

      const response = await axios.post(url, {
        email: email,
        password: password,
        returnSecureToken: true,
      });

      const idToken = response.data.idToken;

      const decodedToken = await admin.auth().verifyIdToken(idToken);
      const customToken = await admin
        .auth()
        .createCustomToken(decodedToken.uid);

      return h.response({ success: true, token: customToken }).code(200);
    } catch (error) {
      return h.response({ error: error.message }).code(500);
    }
  };

 module.exports = loginHandler;