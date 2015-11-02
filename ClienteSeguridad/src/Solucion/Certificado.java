package Solucion;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.crypto.tls.SignatureAlgorithm;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.noneDSA;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class Certificado
{
	X509Certificate certificado ;
	private PrivateKey priv ;
	private PublicKey pub ;


	public Certificado() throws Exception
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

		keyGen.initialize(1024);

		KeyPair pair = keyGen.generateKeyPair();
		setPriv(pair.getPrivate());
		setPub(pair.getPublic());

		SignatureAlgorithm jesus ;
		jesus = new SignatureAlgorithm();

		BigInteger sum = BigInteger.valueOf(0);
		for(int i = 2; i < 5000; i++) 
		{
			if (i%5 == 0) 
			{
				sum = sum.add(BigInteger.valueOf(i));
			}
		}
       
		BigInteger serialNumber =sum;       // serial number for certificate
		
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X500Principal              dnName = new X500Principal("CN=Test Certificate");
		certGen.setSerialNumber(serialNumber);
		certGen.setIssuerDN(dnName);
		certGen.setNotBefore(new Date(System.currentTimeMillis()-20000));
		certGen.setNotAfter(new Date(System.currentTimeMillis()+20000));
		certGen.setSubjectDN(dnName);                       // note: same as issuer
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA1withRSA");
		certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature
				| KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(
				KeyPurposeId.id_kp_serverAuth));

		certGen.addExtension(X509Extensions.SubjectAlternativeName, false, new GeneralNames(
				new GeneralName(GeneralName.rfc822Name, "test@test.test")));
		X509Certificate cert= certGen.generateX509Certificate(pair.getPrivate(), "BC");

		certificado = cert;

	}


	public PrivateKey getPriv() {
		return priv;
	}


	public void setPriv(PrivateKey priv) {
		this.priv = priv;
	}


	public PublicKey getPub() {
		return pub;
	}


	public void setPub(PublicKey pub) {
		this.pub = pub;
	}

}
