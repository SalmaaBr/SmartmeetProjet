import pandas as pd
import numpy as np

# Pour des résultats reproductibles
np.random.seed(42)

# Fonction pour générer des étiquettes initiales basées sur des critères de dépression
def generate_initial_labels(df):
    # Crise de dépression si : SAD ou STRESSED, stress_level >= 3, support_need = Oui
    conditions = (
        (df['emotional_state'].isin(['SAD', 'STRESSED'])) & 
        (df['stress_level'] >= 3) & 
        (df['support_need'] == 'Oui')
    )
    labels = conditions.astype(int)
    # Ajouter un bruit réduit (5%)
    noise = np.random.rand(len(labels)) < 0.05
    labels[noise] = 1 - labels[noise]
    return labels

# Fonction pour générer la base de données
def generate_mentalhealth_dataset(num_groups=200):
    data = []
    
    # Générer des données pour chaque "groupe" (simulant un utilisateur)
    for _ in range(num_groups):
        num_submissions = np.random.randint(3, 11)  # 3 à 10 soumissions par groupe
        for _ in range(num_submissions):
            response_moment = np.random.choice(['Avant', 'Pendant', 'Apres'])
            stress_level = np.random.randint(1, 6)
            emotional_state = np.random.choice(['HAPPY', 'SAD', 'STRESSED', 'RELAXED', 'NEUTRAL'], p=[0.2, 0.3, 0.2, 0.2, 0.1])
            support_need = np.random.choice(['Oui', 'Non'], p=[0.4, 0.6])
            data.append({
                'response_moment': response_moment,
                'stress_level': stress_level,
                'emotional_state': emotional_state,
                'support_need': support_need
            })
    
    # Créer DataFrame
    df = pd.DataFrame(data)
    
    # Générer des étiquettes
    df['mental_health_crisis'] = generate_initial_labels(df)
    
    # Sauvegarder en CSV
    df.to_csv('mentalhealth_dataset.csv', index=False)
    print(f"✅ Dataset généré et sauvegardé dans 'mentalhealth_dataset.csv' ({len(df)} lignes).")
    
    return df

# Exécution principale
if __name__ == "__main__":
    df = generate_mentalhealth_dataset()
    print("\n📊 Aperçu des 5 premières lignes :")
    print(df[['response_moment', 'stress_level', 'emotional_state', 'support_need', 'mental_health_crisis']].head())
    print("\n📈 Résumé des données :")
    print(df[['stress_level', 'mental_health_crisis']].describe())