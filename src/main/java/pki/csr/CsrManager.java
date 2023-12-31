package pki.csr;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

import java.io.*;
import java.security.*;

public class CsrManager {

    public static byte[] getCsr(KeyPair subjectKeyPair, String subjectDn, String csrSigningAlg) throws OperatorCreationException, IOException {
        X500Name subjectName = new X500Name(subjectDn);
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(subjectKeyPair.getPublic().getEncoded());

        JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder(csrSigningAlg);
        ContentSigner signer = signerBuilder.build(subjectKeyPair.getPrivate());

        PKCS10CertificationRequestBuilder csrBuilder = new PKCS10CertificationRequestBuilder(subjectName, subjectPublicKeyInfo);
        PKCS10CertificationRequest csr = csrBuilder.build(signer);

        return csr.getEncoded();
    }
}
