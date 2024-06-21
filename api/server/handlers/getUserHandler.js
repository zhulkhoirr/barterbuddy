const admin = require("../firebase");

const getUserHandler = async (request, h) => {
    const userId = request.params.userId;

        try {
            const db = admin.firestore();
            const userDoc = await db.collection('users').doc(userId).get();

            if (!userDoc.exists) {
                return h.response({ error: 'User not found' }).code(404);
            }

            const userData = userDoc.data();

            return h.response({ userId: userId, ...userData }).code(200);
        } catch (error) {
            return h.response({ error: error.message }).code(500);
        }
};

module.exports = getUserHandler;