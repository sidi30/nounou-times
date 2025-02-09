#!/bin/bash
mkdir -p src/main/resources
openssl genpkey -algorithm RSA -out src/main/resources/privateKey.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/privateKey.pem -out src/main/resources/publicKey.pem
