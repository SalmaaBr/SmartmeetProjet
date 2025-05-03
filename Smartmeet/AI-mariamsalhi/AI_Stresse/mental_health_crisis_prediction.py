import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from imblearn.over_sampling import SMOTE
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score

# Prétraitement des données
def preprocess_data(df, for_prediction=False):
    # Ajouter des caractéristiques dérivées
    df['sad_count'] = (df['emotional_state'] == 'SAD').astype(int)
    df['support_need_count'] = (df['support_need'] == 'Oui').astype(int)
    
    X = df.drop('mental_health_crisis', axis=1, errors='ignore')
    if not for_prediction:
        y = df['mental_health_crisis']
    else:
        y = None
    
    X = pd.get_dummies(X, drop_first=True)
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X) if not for_prediction else scaler.transform(X)
    return X_scaled, y, scaler, X.columns.tolist()

# Entraîner et évaluer le modèle
def train_model(df):
    X, y, scaler, feature_names = preprocess_data(df)
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)  # Test size augmenté
    smote = SMOTE(random_state=42)
    X_train, y_train = smote.fit_resample(X_train, y_train)
    model = RandomForestClassifier(n_estimators=100, max_depth=10, class_weight='balanced', random_state=42)  # Régularisation
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
    
    # Ajouter des caractéristiques dérivées
    df_encoded['sad_count'] = (df['emotional_state'] == 'SAD').astype(int)
    df_encoded['support_need_count'] = (df['support_need'] == 'Oui').astype(int)
    
    # S'assurer que toutes les colonnes sont présentes
    for col in feature_names:
        if col not in df_encoded.columns:
            df_encoded[col] = 0
    df_encoded = df_encoded[feature_names]
    
    X = scaler.transform(df_encoded)
    X_mean = X.mean(axis=0).reshape(1, -1)  # Moyenne des trois soumissions
    prediction = model.predict(X_mean)[0]
    proba = model.predict_proba(X_mean)[0][1]
    
    # Validation : exiger au moins deux soumissions avec SAD ou STRESSED
    negative_emotions = df['emotional_state'].isin(['SAD', 'STRESSED']).sum()
    if negative_emotions < 2:
        prediction = 0
        proba = min(proba, 0.3)
        print("⚠️ Ajustement : Prédiction corrigée car moins de deux émotions négatives (SAD/STRESSED).")
    
    return prediction, proba

# Main
if __name__ == "__main__":
    # Charger le dataset
    df = pd.read_csv('mentalhealth_dataset.csv')
    print("Aperçu des données chargées :")
    print(df[['response_moment', 'stress_level', 'emotional_state', 'support_need', 'mental_health_crisis']].head())
    
    # Entraîner le modèle
    model, scaler, feature_names = train_model(df)
    
    # Exemple de trois soumissions récentes
    latest_entries = [
        {'response_moment': 'Pendant', 'stress_level': 3, 'emotional_state': 'STRESSED', 'support_need': 'Non'},
        {'response_moment': 'Apres', 'stress_level': 1, 'emotional_state': 'HAPPY', 'support_need': 'Non'},
        {'response_moment': 'Avant', 'stress_level': 4, 'emotional_state': 'SAD', 'support_need': 'Oui'}
    ]
    
    prediction, proba = predict_from_three_entries(model, scaler, feature_names, latest_entries)
    
    print("\n🧠 Résultat de la prédiction :")
    print("Crise de dépression détectée ✅" if prediction == 1 else "Pas de crise de dépression ❌")
    print(f"Probabilité de crise : {proba:.2f}")