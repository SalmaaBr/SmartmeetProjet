import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import pickle
import sys
import logging
import matplotlib.pyplot as plt

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/recommendation_log.txt"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger()

# Load data
logger.info("Loading document and likes data.")
documents_df = pd.read_csv("C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/documents.csv")
likes_df = pd.read_csv("C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/document_likes.csv")

# Load precomputed embeddings
logger.info("Loading precomputed embeddings.")
with open("C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/document_embeddings.pkl", "rb") as f:
    data = pickle.load(f)
    document_ids = data['ids']
    document_embeddings = data['embeddings']
logger.info(f"Loaded embeddings for {len(document_ids)} documents.")

# Create a mapping from document ID to embedding
embedding_dict = dict(zip(document_ids, document_embeddings))

# Verify that all documents in documents_df have embeddings
missing_embeddings = [doc_id for doc_id in documents_df['id'] if doc_id not in embedding_dict]
if missing_embeddings:
    logger.error(f"Missing embeddings for documents: {missing_embeddings}")
    raise ValueError(f"Embeddings missing for documents: {missing_embeddings}")

# Ensure document_embeddings aligns with documents_df
document_embeddings_ordered = []
for doc_id in documents_df['id']:
    document_embeddings_ordered.append(embedding_dict[doc_id])
document_embeddings = np.array(document_embeddings_ordered)
logger.info(f"Aligned document embeddings shape: {document_embeddings.shape}")

# Function to get recommendations for a user
def get_recommendations(user_id, top_n=3):
    logger.info(f"Generating recommendations for user ID: {user_id}")
    # Get documents liked by the user
    liked_docs = likes_df[likes_df['userId'] == user_id]['documentId'].values
    if len(liked_docs) == 0:
        logger.warning("User has not liked any documents. Recommending popular documents.")
        popular_docs = likes_df['documentId'].value_counts().head(top_n)
        recommended_docs = documents_df[documents_df['id'].isin(popular_docs.index)][['id', 'name', 'documentTheme']]
        # Add popularity scores for visualization
        recommended_docs['popularity'] = recommended_docs['id'].map(popular_docs)
        return recommended_docs

    # Get embeddings for liked documents
    liked_embeddings = []
    for doc_id in liked_docs:
        if doc_id in embedding_dict:
            liked_embeddings.append(embedding_dict[doc_id])
        else:
            logger.warning(f"No embedding found for liked document ID: {doc_id}")

    if not liked_embeddings:
        logger.warning("No embeddings found for liked documents. Recommending popular documents.")
        popular_docs = likes_df['documentId'].value_counts().head(top_n)
        recommended_docs = documents_df[documents_df['id'].isin(popular_docs.index)][['id', 'name', 'documentTheme']]
        recommended_docs['popularity'] = recommended_docs['id'].map(popular_docs)
        return recommended_docs

    # Compute the average embedding for liked documents
    logger.info("Computing user profile by averaging embeddings of liked documents.")
    liked_embeddings = np.array(liked_embeddings)
    user_profile = np.mean(liked_embeddings, axis=0)

    # Compute cosine similarity between user profile and all documents
    logger.info("Computing cosine similarity between user profile and all documents.")
    similarities = cosine_similarity([user_profile], document_embeddings)[0]

    # Verify lengths match
    if len(similarities) != len(documents_df):
        logger.error(f"Length of similarities ({len(similarities)}) does not match number of documents ({len(documents_df)})")
        raise ValueError(f"Length of similarities ({len(similarities)}) does not match number of documents ({len(documents_df)})")

    # Add similarity scores to documents_df
    documents_df['similarity'] = similarities

    # Exclude already liked documents
    recommended_docs = documents_df[~documents_df['id'].isin(liked_docs)]

    # Sort by similarity score and get top N
    logger.info(f"Selecting top {top_n} recommendations.")
    recommended_docs = recommended_docs.sort_values(by='similarity', ascending=False)
    recommended_docs = recommended_docs.head(top_n)[['id', 'name', 'documentTheme', 'similarity']]

    logger.info("Recommendations generated successfully.")
    return recommended_docs

# Main execution
if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python recommendation_model.py <user_id>")
        sys.exit(1)

    user_id = int(sys.argv[1])
    recommended_docs = get_recommendations(user_id)
    if isinstance(recommended_docs, str):
        print(recommended_docs)
    else:
        # Convert to dictionary for printing
        recommendations = recommended_docs.to_dict(orient='records')
        print(recommendations)

        # Visualize recommendations only if there are recommendations
        if not recommended_docs.empty:
            plt.figure(figsize=(8, 6))
            if 'similarity' in recommended_docs.columns:
                # Plot similarity scores if available
                plt.bar(recommended_docs['name'], recommended_docs['similarity'], color='skyblue')
                plt.ylabel("Similarity Score")
            else:
                # Plot popularity scores if fallback was used
                plt.bar(recommended_docs['name'], recommended_docs['popularity'], color='lightcoral')
                plt.ylabel("Popularity (Number of Likes)")
            plt.title(f"Recommendations for User {user_id}")
            plt.xlabel("Document Name")
            plt.xticks(rotation=45)
            plt.tight_layout()
            plt.show()
        else:
            logger.info(f"No recommendations to visualize for user {user_id}.")
            print(f"No recommendations available for user {user_id}. Consider liking some documents or adding more documents to the system.")