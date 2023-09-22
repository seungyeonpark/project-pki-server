package pki.certificate;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import pki.common.Constants;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class AutoSignCertificate {

    public X509Certificate getCertificate(byte[] certificateRequestInfo) throws IOException, OperatorCreationException, CertificateException {

        Security.addProvider(new BouncyCastleProvider());
        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(certificateRequestInfo);

        /** 1. Basic Fields **/
        // 1-1. serial number
        BigInteger serialNumber = BigInteger.valueOf((new SecureRandom().nextLong() & Long.MAX_VALUE) % Long.MAX_VALUE);

        // 1-2. issuer
        X500Name issuerName = new X500Name(Constants.issuerDN);

        // 1-3. subject
        X500Name subjectName = pkcs10CertificationRequest.getSubject();

        // 1-4. startDate, endDate
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTimeMillis));
        calendar.add(Calendar.YEAR, Constants.validity);

        Date startDate = new Date(currentTimeMillis);
        Date endDate = new Date(calendar.getTimeInMillis());

        /** 2. Extension Fields **/

        /** 3. Certificate Signing Info **/
        ContentSigner contentSigner = new JcaContentSignerBuilder(Constants.signatureAlg).setProvider("BC").build(Constants.issuerKeyPair.getPrivate());

        /** 4. X509Certificate **/
        // 4-1. certificate builder
        JcaX509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
                issuerName,
                serialNumber,
                startDate,
                endDate,
                subjectName,
                Constants.issuerKeyPair.getPublic()
        );

        // 4-2. certificate holder
        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        // 4-3. certificate
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certificateHolder);

        return certificate;
    }
}