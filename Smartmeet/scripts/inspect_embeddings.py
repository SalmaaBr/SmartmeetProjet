import pickle  # Add this import at the top

# Load the pickle file in binary mode
with open("C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/document_embeddings.pkl", "rb") as f:
    data = pickle.load(f)

# Inspect the contents
print("Loaded data:", data)
print("Document IDs:", data['ids'])
print("Embeddings shape:", data['embeddings'].shape)
print("Type of embeddings:", type(data['embeddings']))