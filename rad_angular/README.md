# Le Gourmet — interface Angular

Voir le [README principal](../README.md) pour le démarrage et le déploiement.

```bash
npm install
npm start          # http://localhost:4200 (API attendue sur http://localhost:8081)
npm run build      # build de production dans dist/rad_angular/browser
```

Structure de `src/app` :

- `core/` : modèles, appels API, authentification (JWT), guards, formatage, notifications
- `ui/` : composants réutilisables (icônes, fenêtre modale, notifications et confirmations)
- `layout/` : coquille de l’application (menu latéral, barre supérieure)
- `features/` : une page par fonctionnalité (chargée à la demande)
