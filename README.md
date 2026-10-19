# Hybrid Encryption Demonstration

An educational Java command-line project that demonstrates hybrid encryption end to end. It uses AES to encrypt a plaintext message, manually derives RSA key values from selected parameters, uses RSA modular arithmetic to protect the AES session key, rebuilds that key, and verifies successful decryption.

## What it demonstrates

- Random AES-128 key generation and IV generation with `SecureRandom`
- AES-CBC encryption and decryption with PKCS#5 padding
- Byte-array and hexadecimal representations of cryptographic data
- RSA modulus, Euler's totient, public exponent, and modular-inverse private exponent calculations
- RSA encryption and recovery of the AES session key representation
- The hybrid-encryption pattern: symmetric encryption for data and asymmetric cryptography for key protection

## Run locally

Requires a Java Development Kit (JDK) 8 or later.

```bash
javac hybrid_encryption_21848838.java
java hybrid_encryption_21848838
```

## Educational-security note

This is a learning demonstration, not a production encryption system. It deliberately uses small, user-selectable RSA primes and prints key material so that the cryptographic steps are visible. Production systems should use a securely generated RSA key pair, RSA-OAEP to protect key material, and authenticated encryption such as AES-GCM; private keys and symmetric keys must never be displayed or committed.

