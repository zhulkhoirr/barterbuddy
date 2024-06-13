import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from flask import Flask, request, jsonify
import os
from tensorflow.keras.models import load_model

app = Flask(__name__)

posts_df = pd.read_csv('datasets/csv/posts.csv')
users_df = pd.read_csv('datasets/csv/users.csv')
interests_df = pd.read_csv('datasets/csv/interests.csv')

merged_df = interests_df.merge(posts_df, left_on='post_id', right_on='id', suffixes=('_interest', '_post'))
merged_df = merged_df.merge(users_df, left_on='user_id_interest', right_on='id', suffixes=('_post', '_user'))
merged_df = merged_df[['user_id_interest', 'post_id', 'title', 'description', 'type', 'status', 'city']]
merged_df['content'] = merged_df['title'] + ' ' + merged_df['description'] + ' ' + merged_df['type'] + ' ' + merged_df['status']
merged_df = merged_df[['user_id_interest', 'post_id', 'content', 'city']]

tfidf_vectorizer = TfidfVectorizer()
tfidf_matrix = tfidf_vectorizer.fit_transform(merged_df['content'])
tfidf_df = pd.DataFrame(tfidf_matrix.toarray(), columns=tfidf_vectorizer.get_feature_names_out())
tfidf_df['user_id'] = merged_df['user_id_interest'].values
tfidf_df['post_id'] = merged_df['post_id'].values

n_users = len(users_df)
n_posts = len(posts_df)
tfidf_df = tfidf_df[tfidf_df['user_id'] < n_users]
tfidf_df = tfidf_df[tfidf_df['post_id'] < n_posts]

post_id_mapping = {post_id: i for i, post_id in enumerate(posts_df['id'])}
posts_df['mapped_post_id'] = posts_df['id'].map(post_id_mapping)

model = load_model('model/model.h5')

def recommend_posts_for_user(user_id, top_n=5):
    user_ids = np.array([user_id] * n_posts)
    post_ids = np.array([post_id_mapping[pid] for pid in posts_df['id']])

    predictions = model.predict([user_ids, post_ids])

    top_post_indices = predictions.flatten().argsort()[-top_n:][::-1]

    recommended_posts = posts_df.iloc[top_post_indices]
    recommended_posts = recommended_posts.merge(users_df, left_on='user_id', right_on='id', suffixes=('_post', '_user'))

    return recommended_posts[['id_post', 'title', 'description', 'type', 'city', 'username']]

# @app.route('/', methods=['GET']) #aku masih bingung ngambil idnya darimana
@app.route('/', methods=['POST'])
def recommend():
    # user_id = 1
    data = request.get_json()
    user_id = data.get('user_id')
    if user_id is None:
        return jsonify({"error": "user_id is required"}), 400

    try:
        user_id = int(user_id)
    except ValueError:
        return jsonify({"error": "user_id must be an integer"}), 400

    recommendations = recommend_posts_for_user(user_id, top_n=5)
    return jsonify(recommendations.to_dict(orient='records'))

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=int(os.environ.get('PORT', 8080)))
