const admin = require("../firebase");

const getAllUsers = async () => {
  try {
    const db = admin.firestore();
    const usersRef = db.collection("users");
    const snapshot = await usersRef.get();

    const users = [];
    snapshot.forEach(doc => {
      usersData = doc.data();
      users.push({ id: doc.id, ...usersData });
    });

    return users;
  } catch (error) {
    console.error("Error fetching users:", error);
    throw error;
  }
};

module.exports = getAllUsers;