# ai/bert_sentiment.py
import sys
import json
from transformers import pipeline

# Charger le pipeline de sentiment multiclasse
classifier = pipeline("text-classification", model="bhadresh-savani/distilbert-base-uncased-emotion")

def analyze_sentiment(text):
    if not text.strip():
        return {"error": "Veuillez entrer un texte valide."}
    
    # Prédiction
    results = classifier(text, top_k=1)[0]
    label = results["label"].upper()
    score = results["score"]

    # Mapper les émotions vers des sentiments personnalisés si nécessaire
    sentiment_map = {
        "JOY": "POSITIVE",
        "SADNESS": "NEGATIVE",
        "ANGER": "NEGATIVE",
        "FEAR": "NEGATIVE",
        "LOVE": "LOVE",
        "SURPRISE": "NEUTRAL"
    }
    
    sentiment = sentiment_map.get(label, "NEUTRAL")
    
    return {
        "sentiment": sentiment,
        "confidence": score
    }

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(json.dumps({"error": "Aucun message fourni."}))
        sys.exit(1)
    
    message = sys.argv[1]
    result = analyze_sentiment(message)
    print(json.dumps(result))