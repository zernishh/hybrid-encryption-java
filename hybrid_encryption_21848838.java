import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class hybrid_encryption_21848838 {

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] encryptAES(String plaintext, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public static String decryptAES(byte[] ciphertext, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static void displayBothFormats(String label, byte[] data) {
        System.out.println(label + " (Raw Bytes): " + Arrays.toString(data));
        System.out.println(label + " (Hex): " + bytesToHex(data));
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String plaintext = "TOP SECRET";
        System.out.println("\n[0] Original Plaintext (English): " + plaintext);
        System.out.println("[0] Plaintext Bytes: " + Arrays.toString(plaintext.getBytes(StandardCharsets.UTF_8)));
        System.out.println("[0] Plaintext (Hex): " + bytesToHex(plaintext.getBytes(StandardCharsets.UTF_8)));

        SecretKey aesKey = generateAESKey();
        IvParameterSpec iv = generateIV();

        displayBothFormats("\n[1] Generated AES Key", aesKey.getEncoded());
        displayBothFormats("[2] Generated IV", iv.getIV());

        byte[] encryptedBytes = encryptAES(plaintext, aesKey, iv);
        displayBothFormats("\n[3] Encrypted Ciphertext", encryptedBytes);

        System.out.println("\nAvailable Prime Number Options:");
        System.out.println("1. Small (for testing): p=10007, q=10009");
        System.out.println("2. Medium: p=1000003, q=1000033");
        System.out.println("3. Large: p=1000000007, q=1000000009");
        System.out.println("4. Custom (enter your own primes)");
        System.out.print("Choose option (1-4): ");
        int primeOption = scanner.nextInt();
        scanner.nextLine();

        BigInteger p, q;
        switch (primeOption) {
            case 1:
                p = new BigInteger("10007");
                q = new BigInteger("10009");
                break;
            case 2:
                p = new BigInteger("1000003");
                q = new BigInteger("1000033");
                break;
            case 3:
                p = new BigInteger("1000000007");
                q = new BigInteger("1000000009");
                break;
            case 4:
                System.out.print("Enter prime p: ");
                p = new BigInteger(scanner.nextLine());
                System.out.print("Enter prime q: ");
                q = new BigInteger(scanner.nextLine());
                break;
            default:
                System.out.println("Invalid option, using small primes");
                p = new BigInteger("10007");
                q = new BigInteger("10009");
        }

        System.out.println("\nAvailable Public Exponent Options:");
        System.out.println("1. 3 (fast but less secure)");
        System.out.println("2. 17 (balanced)");
        System.out.println("3. 65537 (recommended)");
        System.out.println("4. Custom (enter your own exponent)");
        System.out.print("Choose option (1-4): ");
        int expOption = scanner.nextInt();
        scanner.nextLine();

        BigInteger e;
        switch (expOption) {
            case 1:
                e = new BigInteger("3");
                break;
            case 2:
                e = new BigInteger("17");
                break;
            case 3:
                e = new BigInteger("65537");
                break;
            case 4:
                System.out.print("Enter public exponent e: ");
                e = new BigInteger(scanner.nextLine());
                break;
            default:
                System.out.println("Invalid option, using 65537");
                e = new BigInteger("65537");
        }

        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger d = e.modInverse(phi);

        System.out.println("\n[4] Public Key (e,n): (" + e + ", " + n + ")");
        System.out.println("[5] Private Key (d,n): (" + d + ", " + n + ")");

        String aesKeyHex = bytesToHex(aesKey.getEncoded());
        StringBuilder encryptedKeyBuilder = new StringBuilder();
        for (char c : aesKeyHex.toCharArray()) {
            BigInteger charValue = BigInteger.valueOf((int) c);
            BigInteger encryptedChar = charValue.modPow(e, n);
            encryptedKeyBuilder.append(encryptedChar).append(" ");
        }
        String encryptedKey = encryptedKeyBuilder.toString().trim();
        System.out.println("\n[6] RSA Encrypted AES Key: " + encryptedKey);

        StringBuilder decryptedKeyBuilder = new StringBuilder();
        String[] encryptedChars = encryptedKey.split(" ");
        for (String ec : encryptedChars) {
            BigInteger encryptedChar = new BigInteger(ec);
            BigInteger decryptedChar = encryptedChar.modPow(d, n);
            decryptedKeyBuilder.append((char) decryptedChar.intValue());
        }
        String decryptedKeyHex = decryptedKeyBuilder.toString();
        System.out.println("\n[7] Decrypted AES Key (Hex): " + decryptedKeyHex);

        byte[] decryptedKeyBytes = hexToBytes(decryptedKeyHex);
        displayBothFormats("[7a] Decrypted AES Key", decryptedKeyBytes);

        SecretKey finalAESKey = new SecretKeySpec(decryptedKeyBytes, "AES");

        String decryptedText = decryptAES(encryptedBytes, finalAESKey, iv);
        System.out.println("\n[8] Decrypted Plaintext (English): " + decryptedText);
        System.out.println("[8] Decrypted Plaintext Bytes: " + Arrays.toString(decryptedText.getBytes(StandardCharsets.UTF_8)));
        System.out.println("[8] Decrypted Plaintext (Hex): " + bytesToHex(decryptedText.getBytes(StandardCharsets.UTF_8)));
    }
}
