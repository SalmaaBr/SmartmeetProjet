from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from transformers import pipeline
import logging
import re
import nltk
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords

# Configurer le logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

# Désactiver les logs verbeux de transformers
logging.getLogger("transformers").setLevel(logging.ERROR)

# Télécharger les ressources NLTK avec gestion des erreurs
try:
    nltk.data.find('tokenizers/punkt_tab')
except LookupError:
    logger.info("Téléchargement de punkt_tab...")
    nltk.download('punkt_tab', quiet=True)
try:
    nltk.data.find('corpora/stopwords')
except LookupError:
    logger.info("Téléchargement de stopwords...")
    nltk.download('stopwords', quiet=True)

# Initialiser FastAPI
app = FastAPI()

# Charger le modèle une seule fois (modèle GoEmotions)
try:
    classifier = pipeline(
        "text-classification",
        model="SamLowe/roberta-base-go_emotions",
        tokenizer="SamLowe/roberta-base-go_emotions",
        return_all_scores=True
    )
    logger.debug("Modèle GoEmotions chargé avec succès")
except Exception as e:
    logger.error(f"Erreur lors du chargement du modèle: {str(e)}")
    raise

# Modèle de données pour la requête
class SentimentRequest(BaseModel):
    message: str

# Fonction de prétraitement du texte
def preprocess_text(text: str) -> str:
    # Remplacer les emojis par des descriptions (simplifié)
    emoji_map = {
        '😊': 'happy',
        '😢': 'sad',
        '😣': 'disappointed',
        '😡': 'angry',
        '😍': 'love'
    }
    for emoji, word in emoji_map.items():
        text = text.replace(emoji, f' {word} ')
    
    # Convertir en minuscules et supprimer les caractères spéciaux
    text = re.sub(r'[^\w\s]', '', text.lower().strip())
    
    # Tokenisation et suppression des stop words
    try:
        tokens = word_tokenize(text)
        stop_words = set(stopwords.words('english'))
        tokens = [t for t in tokens if t not in stop_words]
    except Exception as e:
        logger.error(f"Erreur lors du prétraitement: {str(e)}")
        tokens = text.split()  # Fallback simple si NLTK échoue
    
    # Normaliser les espaces
    text = ' '.join(tokens)
    return text

# Endpoint pour analyser le sentiment
@app.post("/analyze-sentiment")
async def analyze_sentiment(request: SentimentRequest):
    message = request.message
    if not message.strip():
        logger.error("Message vide reçu")
        raise HTTPException(status_code=400, detail="Veuillez entrer un texte valide.")

    # Prétraiter le texte
    processed_message = preprocess_text(message)
    logger.debug(f"Message prétraité: {processed_message}")

    try:
        # Effectuer la prédiction
        results = classifier(processed_message)
        logger.debug(f"Résultats bruts: {results}")

        # Extraire les top émotions
        emotions = sorted(results[0], key=lambda x: x['score'], reverse=True)[:3]  # Top 3 émotions
        top_emotion = emotions[0]
        label = top_emotion['label'].upper()
        score = top_emotion['score']

        # Mapper les émotions aux sentiments
        sentiment_map = {
            "ADMIRATION": "POSITIVE",
            "AMUSEMENT": "HAPPY",
            "APPROVAL": "POSITIVE",
            "CARING": "LOVE",
            "DESIRE": "EXCITED",
            "EXCITEMENT": "EXCITED",
            "GRATITUDE": "HAPPY",
            "JOY": "HAPPY",
            "LOVE": "LOVE",
            "OPTIMISM": "POSITIVE",
            "PRIDE": "POSITIVE",
            "ANGER": "ANGRY",
            "DISAPPOINTMENT": "DISAPPOINTED",
            "DISAPPROVAL": "DISAPPOINTED",
            "FEAR": "AFRAID",
            "SADNESS": "SAD",
            "ANNOYANCE": "ANGRY",
            "CONFUSION": "CONFUSED",
            "SURPRISE": "SURPRISED",
            "NEUTRAL": "NEUTRAL",
            "CURIOSITY": "CONFUSED",
            "REALIZATION": "SURPRISED",
            "EMBARRASSMENT": "DISAPPOINTED",
            "GRIEF": "SAD",
            "NERVOUSNESS": "AFRAID",
            "REMORSE": "SAD",
            "RELIEF": "HAPPY"
        }

        sentiment = sentiment_map.get(label, "NEUTRAL")
        logger.debug(f"Émotion détectée: {label}, Sentiment mappé: {sentiment}, Score: {score}")

        # Construire la réponse avec plusieurs sentiments
        response = {
            "sentiment": sentiment,
            "confidence": score,
            "top_emotions": [
                {
                    "emotion": emotion['label'].upper(),
                    "sentiment": sentiment_map.get(emotion['label'].upper(), "NEUTRAL"),
                    "confidence": emotion['score']
                }
                for emotion in emotions
            ]
        }

        return response

    except Exception as e:
        logger.error(f"Erreur lors de l'analyse du sentiment: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Erreur lors de l'analyse du sentiment: {str(e)}")