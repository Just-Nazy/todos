
# Todo
Basic todo project for teaching purpose only 

# Requirements
<ul>
 <li>maven comfigured to be able to run mvn, or use wrapper</li>
  <li> Java 17</li>
</ul>

# Run unit tests
```sh
mvn test -Put
```

# Run integration tests
```sh
mvn test -Pit
```

# Run the microservice
 --- 
```sh
mvn spring-boot:run
```

## Acces to endpoints
http://localhost:8080/cicd/api/todos

## Code erreurs ZAP
0	✅ Succès. Aucun problème critique trouvé, ou scan réussi sans erreurs.
1	⚠️ Avertissements détectés (en fonction du fichier de config utilisé ou des règles par défaut).
2	⚠️ Erreurs détectées, par exemple vulnérabilités sévères.
3	❌ Erreur fatale (ex : cible inaccessible, erreur d’argument, problème d’environnement, etc.).

## Option -a ZAP
Active aussi les règles Alpha, qui sont expérimentales mais peuvent détecter des failles supplémentaires. Cela augmente la couverture, mais aussi le risque de faux positifs.

## Contact

Dr. SENE - <a href="mailto:senei@ept.sn">senei@ept.sn</a>