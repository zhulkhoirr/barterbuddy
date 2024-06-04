const Hapi = require("@hapi/hapi");
const admin = require("./firebase");
const axios = require("axios");

const init = async () => {
  const server = Hapi.server({
    port: 3000,
    host: "localhost",
    routes: {
      cors: {
        origin: ["*"],
      },
    },
  });

  server.route({
    method: "POST",
    path: "/register",
    handler: async (request, h) => {
      const { username, email, password, confirmPassword, city } =
        request.payload;

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
        });

        return h.response({ success: true }).code(201);
      } catch (error) {
        return h.response({ error: error.message }).code(500);
      }
    },
  });

  server.route({
    method: "POST",
    path: "/login",
    handler: async (request, h) => {
      const { email, password } = request.payload;

      try {
        const apiKey = "AIzaSyC6uLvZ8po2RvOY1ZtX8PIJsb54X1z5NBw";
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
    },
  });

  await server.start();
  console.log("Server running on %s", server.info.uri);
};

process.on("unhandledRejection", (err) => {
  console.log(err);
  process.exit(1);
});

init();
