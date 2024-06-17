
from flask import Flask, request, jsonify
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = Flask(__name__)

# Load the data
posts_df = pd.read_csv('posts.csv')

# Preprocess the data
features_selection = ['title', 'description']
for feature in features_selection:
    posts_df[feature] = posts_df[feature].fillna('').str.title()

# Combine features into a single column
posts_df['combined_features'] = posts_df['title'] + ' ' + posts_df['description']

# Create the TF-IDF vectorizer and feature vectors
vectorizer = TfidfVectorizer(analyzer='word', stop_words='english')
feature_vector = vectorizer.fit_transform(posts_df['combined_features'])

def search_posts(input_barang, top_n=5):
    # Transform the input using the same vectorizer
    input_vector = vectorizer.transform([input_barang])

    # Calculate cosine similarity
    similarity = cosine_similarity(input_vector, feature_vector)

    # Sort the results based on similarity scores
    similarity_scores = list(enumerate(similarity[0]))
    sorted_similar_posts = sorted(similarity_scores, key=lambda x: x[1], reverse=True)

    # Get the top N results
    search_results = []
    for post in sorted_similar_posts[:top_n]:
        index = post[0]
        search_results.append({
            'title': posts_df.iloc[index]['title'],
            'description': posts_df.iloc[index]['description']
        })

    return search_results

@app.route('/search', methods=['POST'])
def search():
    data = request.get_json()
    if not data or 'input_barang' not in data:
        return jsonify({'error': 'input_barang is required'}), 400

    input_barang = data['input_barang']

    try:
        results = search_posts(input_barang, top_n=5)
        return jsonify({'search_results': results})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)