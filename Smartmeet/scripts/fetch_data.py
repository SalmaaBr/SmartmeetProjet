import requests
import pandas as pd

# Step 1: Authenticate and get the token
login_url = "http://localhost:8082/api/auth/signin"
login_data = {
    "username": "salma12345",
    "password": "#Salma12345"
}
try:
    login_response = requests.post(login_url, json=login_data)
    login_response.raise_for_status()
except requests.exceptions.HTTPError as e:
    print(f"Login failed: {e}")
    exit(1)

# Extract and verify the token
response_data = login_response.json()
print("Login response:", response_data)
token = response_data.get("accessToken")
if not token:
    print("Error: No token found in response")
    exit(1)
print("JWT Token:", token)

# Step 2: Set up headers and URLs
headers = {
    "Authorization": f"Bearer {token}",
    "Content-Type": "application/json"
}
documents_url = "http://localhost:8082/Document/ReadAllDocuments"
likes_url = "http://localhost:8082/Document/getAllDocumentLikes"

# Step 3: Fetch documents
try:
    response_documents = requests.get(documents_url, headers=headers, timeout=30)
    response_documents.raise_for_status()
except requests.exceptions.HTTPError as e:
    print(f"Failed to fetch documents: {e}")
    exit(1)

documents_data = response_documents.json()
documents_df = pd.DataFrame(documents_data)
print("Documents fetched:", documents_df)

# Step 4: Fetch all document likes separately
try:
    response_likes = requests.get(likes_url, headers=headers, timeout=30)
    response_likes.raise_for_status()
except requests.exceptions.HTTPError as e:
    print(f"Failed to fetch document likes: {e}")
    exit(1)

likes_data = response_likes.json()
print("Raw likes data:", likes_data)  # Debug print

# Process the likes to extract userId
for like in likes_data:
    if 'user_userid' in like:
        like['userId'] = like['user_userid']
        del like['user_userid']
    elif 'user' in like and 'id' in like['user']:
        like['userId'] = like['user']['id']
        del like['user']
    elif 'user' in like and 'userId' in like['user']:
        like['userId'] = like['user']['userId']
        del like['user']
    else:
        print(f"Warning: Missing user information for like_id {like.get('likeId')}. Setting userId to None.")
        like['userId'] = None

likes_df = pd.DataFrame(likes_data)
print("Likes extracted:", likes_df)

# Step 5: Save data to CSV
documents_df.to_csv(
    "C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/documents.csv",
    index=False
)
likes_df.to_csv(
    "C:/Users/Mariem/Downloads/wetransfer_front-zip_2025-04-14_2013/Spring/Smartmeet/scripts/document_likes.csv",
    index=False
)

print("Data fetched successfully!")