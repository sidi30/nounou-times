#!/bin/bash

# Générer la clé privée
openssl genpkey -algorithm RSA -out privateKey.pem -pkeyopt rsa_keygen_bits:2048

# Générer la clé publique correspondante
openssl rsa -pubout -in privateKey.pem -out publicKey.pem

# Déplacer les clés dans le bon répertoire
mv privateKey.pem publicKey.pem src/main/resources/
