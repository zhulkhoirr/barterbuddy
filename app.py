import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import LabelEncoder
import tensorflow as tf
from tensorflow.keras.models import load_model
from flask import Flask, request, jsonify

app = Flask(__name__)

posts_df = pd.read_csv('datasets/postsData.csv')
users_df = pd.read_csv('datasets/users.csv')

interests = []
for i, user in users_df.iterrows():
    for col in users_df.columns:
        if 'interested_posts__' in col and pd.notna(user[col]):
            interests.append({'user_id': user['id'], 'post_id': user[col]})

interests_df = pd.DataFrame(interests)

interests_df['user_id'] = interests_df['user_id'].astype(str)
posts_df['userId'] = posts_df['userId'].astype(str)
users_df['id'] = users_df['id'].astype(str)

merged_df = interests_df.merge(posts_df, left_on='post_id', right_on='id')
merged_df = merged_df.merge(users_df, left_on='user_id', right_on='id')

merged_df['content'] = (
    merged_df['title'].fillna('') + ' ' + 
    merged_df['description'].fillna('') + ' ' + 
    merged_df['type'].fillna('') + ' ' + 
    merged_df['status'].fillna('')
)

tfidf_vectorizer = TfidfVectorizer()
tfidf_matrix = tfidf_vectorizer.fit_transform(merged_df['content'])
tfidf_df = pd.DataFrame(tfidf_matrix.toarray(), columns=tfidf_vectorizer.get_feature_names_out())
tfidf_df['user_id'] = merged_df['user_id'].values
tfidf_df['post_id'] = merged_df['post_id'].values

user_encoder = LabelEncoder()
post_encoder = LabelEncoder()

tfidf_df['user_id_encoded'] = user_encoder.fit_transform(tfidf_df['user_id'])
tfidf_df['post_id_encoded'] = post_encoder.fit_transform(tfidf_df['post_id'])

posts_df['mapped_post_id'] = post_encoder.transform(posts_df['id'])

model = load_model('model/model.h5')

def recommend_posts_for_user(user_id, top_n=5):
    encoded_user_id = user_encoder.transform([user_id])[0]
    user_ids = np.array([encoded_user_id] * len(posts_df))
    post_ids = posts_df['mapped_post_id'].values

    predictions = model.predict([user_ids, post_ids])

    top_post_indices = predictions.flatten().argsort()[-top_n:][::-1]

    recommended_posts = posts_df.iloc[top_post_indices]

    return recommended_posts['id'].tolist()

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.get_json()
    if not data or 'user_id' not in data:
        return jsonify({'error': 'user_id is required'}), 400

    user_id = data['user_id']

    try:
        recommendations = recommend_posts_for_user(user_id, top_n=5)
        return jsonify({'recommended_post_ids': recommendations})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)