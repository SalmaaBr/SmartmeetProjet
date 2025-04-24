import pandas as pd
import numpy as np
from transformers import BertTokenizer, BertModel
import torch
import pickle
import logging
import time

# Set up logging to both console and file
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/training_log.txt"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger()

# Start training process
logger.info("Starting the training process for the recommendation model.")

# Load data
logger.info("Loading document data from CSV.")
documents_df = pd.read_csv("C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/documents.csv")
logger.info(f"Loaded {len(documents_df)} documents.")

# Handle missing values in name and description
logger.info("Preprocessing document data: handling missing values.")
documents_df['name'] = documents_df['name'].astype(str).fillna('')
documents_df['description'] = documents_df['description'].astype(str).fillna('')

# Combine name and description for better context
logger.info("Combining name and description fields for semantic analysis.")
documents_df['text'] = documents_df['name'] + " " + documents_df['description']

# Load pre-trained BERT model and tokenizer
logger.info("Loading pre-trained BERT model and tokenizer (bert-base-uncased).")
start_time = time.time()
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
model = BertModel.from_pretrained('bert-base-uncased')
model.eval()
logger.info(f"BERT model loaded in {time.time() - start_time:.2f} seconds.")

# Function to get BERT embeddings for a text
def get_bert_embedding(text):
    logger.info(f"Generating embedding for text: {text[:50]}...")
    start_time = time.time()
    # Tokenize and encode the text
    inputs = tokenizer(text, return_tensors="pt", max_length=512, truncation=True, padding=True)

    # Get embeddings
    with torch.no_grad():
        outputs = model(**inputs)
        # Use the [CLS] token embedding (first token)
        embedding = outputs.last_hidden_state[:, 0, :].squeeze().numpy()

    logger.info(f"Embedding generated in {time.time() - start_time:.2f} seconds.")
    return embedding

# Compute embeddings for all documents
logger.info("Starting embedding generation for all documents.")
embeddings = []
for idx, text in enumerate(documents_df['text']):
    logger.info(f"Processing document {idx + 1}/{len(documents_df)} (ID: {documents_df.iloc[idx]['id']})")
    embedding = get_bert_embedding(text)
    embeddings.append(embedding)
    logger.info(f"Embedding shape: {embedding.shape}")

# Convert to numpy array
logger.info("Converting embeddings to numpy array.")
embeddings = np.array(embeddings)
logger.info(f"Final embeddings shape: {embeddings.shape}")

# Save embeddings and document IDs
logger.info("Saving embeddings to file.")
with open("C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/document_embeddings.pkl", "wb") as f:
    pickle.dump({'ids': documents_df['id'].values, 'embeddings': embeddings}, f)

logger.info("Training process completed successfully! Embeddings saved.")