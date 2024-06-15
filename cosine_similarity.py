
from sqlalchemy import create_engine
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import difflib

input_barang = input('Nama Barang : ').strip().title()


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

# Mencari kombinasi title dan description yang paling mirip dengan input pengguna
posts_df['combined_title_description'] = posts_df['title'] + ' ' + posts_df['description']
list_combined = posts_df['combined_title_description'].tolist()

# Menggunakan ambang batas yang lebih rendah untuk pencocokan
find_close_match = difflib.get_close_matches(input_barang, list_combined, n=1, cutoff=0.19)

if not find_close_match:
    print("No close match found for the search term.")
    sorted_similar_posts = []
else:
    close_match = find_close_match[0]
    post_index = posts_df[posts_df['combined_title_description'] == close_match].index.values[0]
    similarity_score = list(enumerate(similarity[post_index]))
    sorted_similar_posts = sorted(similarity_score, key=lambda x: x[1], reverse=True)


    print("Search result for: {}\n".format(input_barang))

i = 1
for post in sorted_similar_posts:
    index = post[0]
    title = posts_df.iloc[index]['title']
    description = posts_df.iloc[index]['description']
    if i <= 10:
        print('{}. Title: {}\n   Description: {}\n'.format(i, title, description))
        i += 1