# Citations : application Android

Application pour réviser des citations de français : import Excel, révision « Je connais / Je ne connais pas », exercice « Compléter » et jauge du jour à paliers. La jauge s'affiche aussi dans une **notification permanente** du téléphone, remise à zéro chaque jour à minuit.

Les citations restent sur le téléphone. Une connexion internet n'est utile que pour les polices d'écriture, l'application fonctionne sans.

## Obtenir l'APK

### Option A : Android Studio (sur ordinateur)
1. Installez Android Studio, puis choisissez « Open » et sélectionnez ce dossier.
2. Attendez la fin de la synchronisation Gradle.
3. Menu **Build > Build APK(s)**. Le fichier est créé dans `app/build/outputs/apk/debug/app-debug.apk`.
4. Pour tester directement, branchez le téléphone (débogage USB activé) et cliquez sur « Run ».

### Option B : GitHub, sans rien installer
1. Créez un dépôt GitHub et envoyez-y le contenu de ce dossier (le dossier `.github` compris).
2. Ouvrez l'onglet **Actions**, lancez « Compiler l'APK » et attendez environ 5 minutes.
3. Téléchargez l'artefact `citations-apk`, qui contient `app-debug.apk`.

## Installer sur le téléphone
Copiez l'APK sur le téléphone et ouvrez-le. Android demande d'autoriser l'installation depuis cette source : acceptez.

## Notification permanente
- Au premier lancement, **autorisez les notifications**.
- La notification affiche le nombre de citations révisées aujourd'hui, une barre de progression jusqu'à 50 et le palier atteint. Elle se met à jour dès que vous répondez à une citation.
- Depuis Android 13, vous pouvez la balayer pour la faire disparaître. Elle revient à la prochaine réponse, à minuit ou au prochain lancement de l'application.
- Si la notification disparaît souvent, désactivez l'optimisation de la batterie pour Citations (Paramètres > Applications > Citations > Batterie).

## Modifier l'application
- Les écrans sont dans `app/src/main/assets/index.html`.
- Les paliers (5, 10, 20, 35, 50) sont définis à deux endroits : `TIERS` dans `index.html` et `TIERS` dans `app/src/main/java/fr/citations/app/Gauge.kt`. Changez-les aux deux endroits.
