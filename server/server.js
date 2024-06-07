require("dotenv").config();
const Hapi = require("@hapi/hapi");
const admin = require("./firebase");
const axios = require("axios");
const stream = require("stream");
const init = async () => {
  const server = Hapi.server({
    port: 3000,
    host: "0.0.0.0",
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
    },
  });

  server.route({
    method: "GET",
    path: "/user/{userId}",
    handler: async (request, h) => {
      const userId = request.params.userId;

      try {
        const db = admin.firestore();
        const userDoc = await db.collection("users").doc(userId).get();

        if (!userDoc.exists) {
          return h.response({ error: "User not found" }).code(404);
        }

        const userData = userDoc.data();

        return h.response({ userId: userId, ...userData }).code(200);
      } catch (error) {
        return h.response({ error: error.message }).code(500);
      }
    },
  });

  server.route({
    method: "POST",
    path: "/user/{userId}/post",
    options: {
      payload: {
        output: "stream",
        parse: true,
        allow: "multipart/form-data",
        multipart: true,
      },
    },
    handler: async (request, h) => {
      const userId = request.params.userId;
      const { title, description, status, created_at, updated_at } =
        request.payload;

      const imageFile = request.payload.image;

      if (!imageFile) {
        return h.response({ error: "Image file is required" }).code(400);
      }

      try {
        const db = admin.firestore();
        const userRef = db.collection("users").doc(userId);
        const postRef = userRef.collection("posts").doc();

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

        await postRef.set({
          id,
          title,
          image: imageUrl,
          description,
          status,
          created_at,
          updated_at,
        });

        return h.response({ success: true, id }).code(201);
      } catch (error) {
        return h.response({ error: error.message }).code(500);
      }
    },
  });

  server.route({
    method: "GET",
    path: "/user/{userId}/posts",
    handler: async (request, h) => {
      const userId = request.params.userId;

      try {
        const db = admin.firestore();
        const userRef = db.collection("users").doc(userId);
        const postsSnapshot = await userRef.collection("posts").get();

        if (postsSnapshot.empty) {
          return h.response({ success: true, posts: [] }).code(200);
        }

        const posts = [];
        postsSnapshot.forEach((doc) => {
          posts.push(doc.data());
        });

        return h.response({ posts }).code(200);
      } catch (error) {
        return h.response({ error: error.message }).code(500);
      }
    },
  });

  server.route({
    method: "GET",
    path: "/posts",
    handler: async (request, h) => {
      try {
        const db = admin.firestore();
        const postsSnapshot = await db.collectionGroup("posts").get();

        const posts = [];
        postsSnapshot.forEach((doc) => {
          const postData = doc.data();
          const userId = doc.ref.parent.parent.id;
          posts.push({ userId, ...postData });
        });

        return h.response({ posts }).code(200);
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
