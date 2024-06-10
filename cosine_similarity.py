from sqlalchemy import create_engine
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import difflib

input_barang = input('Nama Barang : ')


search_history_df = pd.read_csv('search_history.csv')
posts_df = pd.read_csv('posts.csv')

# Menggabungkan fitur yang diperlukan dari dataset 'posts'
features_selection = ['title', 'description']


for feature in features_selection:
    posts_df[feature] = posts_df[feature].fillna('')


features_combination = posts_df[features_selection[0]] + ' ' + posts_df[features_selection[1]]


feature_vector = TfidfVectorizer(analyzer='word', stop_words='english').fit_transform(features_combination)


similarity = cosine_similarity(feature_vector)

# Mencari judul yang paling mirip dengan input pengguna
list_title = posts_df['title'].tolist()
find_close_match = difflib.get_close_matches(input_barang, list_title)
if not find_close_match:
    print("No close match found for the search term.")
else:
    close_match = find_close_match[0]
    post_index = posts_df[posts_df['title'] == close_match].index.values[0]
    similarity_score = list(enumerate(similarity[post_index]))
    sorted_similar_posts = sorted(similarity_score, key=lambda x: x[1], reverse=True)

    # Menampilkan hasil pencarian
    print("Search result for: {}\n".format(input_barang))

i = 1
for post in sorted_similar_posts:
    index = post[0]
    title = posts_df.iloc[index]['title']
    if i <= 20:
        print('{}. {}'.format(i, title))
        i += 1
