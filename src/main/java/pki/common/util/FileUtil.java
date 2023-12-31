package pki.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;

public class FileUtil {

    public static void saveCertificate(String path, Certificate certificate) throws IOException, CertificateEncodingException {
        String projectRoot = System.getProperty("user.dir");
        String filePath = projectRoot + File.separator + path;

        FileOutputStream fo = new FileOutputStream(filePath);
        fo.write(certificate.getEncoded());
        fo.close();
    }

    public static void saveCertificateList(String filePath, List<Certificate> certificateList) throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException {
        saveCertificateList(filePath, certificateList, "");
    }

    public static void saveCertificateList(String filePath, List<Certificate> certificateList, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        for (Certificate certificate : certificateList) {
            ks.setCertificateEntry(createAlias(certificate), certificate);
        }

        FileOutputStream fo = new FileOutputStream(filePath);
        ks.store(fo, password.toCharArray());
        fo.close();
    }

    private static String createAlias(Certificate certificate) throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(certificate.getEncoded());
        String alias = Base64.getEncoder().encodeToString(digest);
        return alias;
    }
}
