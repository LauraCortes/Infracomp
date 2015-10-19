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
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
	String startDates = "31-08-2015";
	String finDates = "31-08-2018";
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA","BC");
	
	SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	keyGen.initialize(1024, random);
	

	KeyPair pair = keyGen.generateKeyPair();
setPriv(pair.getPrivate());
setPub(pair.getPublic());
	
	SignatureAlgorithm jesus ;
	jesus = new SignatureAlgorithm();
	
	BigInteger sum = BigInteger.valueOf(0);
	for(int i = 2; i < 5000; i++) {
	    if (i%5 == 0) {
	        sum = sum.add(BigInteger.valueOf(i));
	    }
	}
	
	Date startDate = sdf.parse(startDates);                // time from which certificate is valid
	Date expiryDate = sdf.parse(finDates);               // time after which certificate is not valid
	BigInteger serialNumber =sum;       // serial number for certificate
	KeyPair keyPair =  keyGen.generateKeyPair(); // EC public/private key pair
	

	X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
	X500Principal              dnName = new X500Principal("CN=Test CA Certificate");
	certGen.setSerialNumber(serialNumber);
	certGen.setIssuerDN(dnName);
	certGen.setNotBefore(startDate);
	certGen.setNotAfter(expiryDate);
	certGen.setSubjectDN(dnName);                       // note: same as issuer
	certGen.setPublicKey(keyPair.getPublic());
	certGen.setSignatureAlgorithm("SHA1withRSA");
	X509Certificate cert= certGen.generate(keyPair.getPrivate(), "BC");
	
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
