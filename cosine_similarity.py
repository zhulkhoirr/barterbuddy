
from flask import Flask, request, jsonify
from sqlalchemy import create_engine
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import difflib

app = Flask(__name__)

# Load data and initialize model outside the route to avoid reloading on each request
posts_df = pd.read_csv('posts.csv')

# Menggabungkan fitur yang diperlukan dari dataset 'posts'
features_selection = ['title', 'description']

# Mengisi nilai kosong dengan string kosong
for feature in features_selection:
    posts_df[feature] = posts_df[feature].fillna('').str.title()

posts_df['combined_features'] = posts_df['title'] + ' ' + posts_df['description']
feature_vector = TfidfVectorizer(analyzer='word', stop_words='english').fit_transform(posts_df['combined_features'])

# Menghitung kesamaan kosinus antara vektor
similarity = cosine_similarity(feature_vector)

# Endpoint untuk mencari barang
@app.route('/search', methods=['POST'])
def search():
    data = request.get_json()
    if not data or 'nama_barang' not in data:
        return jsonify({'error': 'nama_barang is required'}), 400

    input_barang = data['nama_barang'].strip().title()

    # Mencari kombinasi title dan description yang paling mirip dengan input pengguna
    posts_df['combined_title_description'] = posts_df['title'] + ' ' + posts_df['description']
    list_combined = posts_df['combined_title_description'].tolist()

    # Menggunakan ambang batas yang lebih rendah untuk pencocokan
    find_close_match = difflib.get_close_matches(input_barang, list_combined, n=1, cutoff=0.19)

    if not find_close_match:
        return jsonify({'error': 'No close match found for the search term.'}), 404
    else:
        close_match = find_close_match[0]
        post_index = posts_df[posts_df['combined_title_description'] == close_match].index.values[0]
        similarity_score = list(enumerate(similarity[post_index]))
        sorted_similar_posts = sorted(similarity_score, key=lambda x: x[1], reverse=True)

        search_results = []
        i = 1
        for post in sorted_similar_posts:
            index = post[0]
            title = posts_df.iloc[index]['title']
            description = posts_df.iloc[index]['description']
            if i <= 10:
                search_results.append({'title': title, 'description': description})
                i += 1

        return jsonify({'search_results': search_results})

if __name__ == '__main__':
    app.run(debug=True)
