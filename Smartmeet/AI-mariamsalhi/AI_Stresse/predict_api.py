import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from imblearn.over_sampling import SMOTE
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score
import joblib
from flask import Flask, request, jsonify

app = Flask(__name__)

# Prétraitement des données
def preprocess_data(df, for_prediction=False, scaler=None, feature_names=None):
    df['sad_count'] = (df['emotional_state'] == 'SAD').astype(int)
    df['support_need_count'] = (df['support_need'] == 'Oui').astype(int)
    
    X = df.drop('mental_health_crisis', axis=1, errors='ignore')
    if not for_prediction:
        y = df['mental_health_crisis']
    else:
        y = None
    
    X = pd.get_dummies(X, drop_first=True)
    if for_prediction:
        # S'assurer que les colonnes correspondent à feature_names
        for col in feature_names:
            if col not in X.columns:
                X[col] = 0
        X = X[feature_names]
        X_scaled = scaler.transform(X)
    else:
        scaler = StandardScaler()
        X_scaled = scaler.fit_transform(X)
    return X_scaled, y, scaler, X.columns.tolist()

# Entraîner et évaluer le modèle
def train_model(df):
    X, y, scaler, feature_names = preprocess_data(df)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)
    smote = SMOTE(random_state=42)
    X_train, y_train = smote.fit_resample(X_train, y_train)
    model = RandomForestClassifier(n_estimators=100, max_depth=10, class_weight='balanced', random_state=42)
    model.fit(X_train, y_train)
    
    # Évaluer le modèle
    y_pred = model.predict(X_test)
    print("\n📊 Métriques d'évaluation sur les données de test :")
    print(f"Accuracy : {accuracy_score(y_test, y_pred):.2f}")
    print(f"Precision : {precision_score(y_test, y_pred):.2f}")
    print(f"Recall : {recall_score(y_test, y_pred):.2f}")
    print(f"F1-Score : {f1_score(y_test, y_pred):.2f}")
    
    return model, scaler, feature_names

# Prédire à partir de trois soumissions
def predict_from_three_entries(model, scaler, feature_names, entries):
    df = pd.DataFrame(entries)
    df_encoded = pd.get_dummies(df)
    
    df_encoded['sad_count'] = (df['emotional_state'] == 'SAD').astype(int)
    df_encoded['support_need_count'] = (df['support_need'] == 'Oui').astype(int)
    
    for col in feature_names:
        if col not in df_encoded.columns:
            df_encoded[col] = 0
    df_encoded = df_encoded[feature_names]
    
    X = scaler.transform(df_encoded)
    X_mean = X.mean(axis=0).reshape(1, -1)
    prediction = model.predict(X_mean)[0]
    proba = model.predict_proba(X_mean)[0][1]
    
    negative_emotions = df['emotional_state'].isin(['SAD', 'STRESSED']).sum()
    if negative_emotions < 2:
        prediction = 0
        proba = min(proba, 0.3)
        print("⚠️ Ajustement : Prédiction corrigée car moins de deux émotions négatives (SAD/STRESSED).")
    
    return prediction, proba

# API Flask
@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()
        if not data or len(data) != 3:
            return jsonify({'error': 'Veuillez fournir exactement trois soumissions.'}), 400
        
        # Charger le modèle, scaler et feature_names
        model = joblib.load('mental_health_model.pkl')
        scaler = joblib.load('scaler.pkl')
        feature_names = joblib.load('feature_names.pkl')
        
        # Prédire
        prediction, proba = predict_from_three_entries(model, scaler, feature_names, data)
        
        return jsonify({
            'crisis_detected': bool(prediction),
            'crisis_probability': float(proba),
            'message': 'Crise de dépression détectée' if prediction else 'Pas de crise de dépression'
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Main : Entraîner et sauvegarder le modèle
if __name__ == "__main__":
    # Charger le dataset
    df = pd.read_csv('mentalhealth_dataset.csv')
    print("Aperçu des données chargées :")
    print(df[['response_moment', 'stress_level', 'emotional_state', 'support_need', 'mental_health_crisis']].head())
    
    # Entraîner le modèle
    model, scaler, feature_names = train_model(df)
    
    # Sauvegarder le modèle, scaler et feature_names
    joblib.dump(model, 'mental_health_model.pkl')
    joblib.dump(scaler, 'scaler.pkl')
    joblib.dump(feature_names, 'feature_names.pkl')
    print("✅ Modèle, scaler et feature_names sauvegardés.")
    
    # Lancer l'API Flask
    app.run(host='0.0.0.0', port=5001, debug=True)