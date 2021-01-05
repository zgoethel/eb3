package net.jibini.eb;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point and basic configuration.
 * 
 * @author Zach Goethel
 */
@SpringBootApplication
public class EasyButton
{
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public EasyButtonConfig config;
	
	/**
	 * Application initialization.
	 * 
	 * Depending on configuration, this may disable certificate validation at a global level due to APIs' neglected or
	 * self-signed SSL certificates.
	 */
	@PostConstruct
	public void init()
	{
		log.info("Loading primary configuration settings");
		config = EasyButtonConfig.loadOrDefault(new EasyButtonConfig());
		
		if (config.getDisableCertCheck())
		{
			log.warn("Certificate checking is disabled; neglected and self-signed SSL certificates will be accepted");
			
			try
			{
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, TRUST_ALL_CERTS, new SecureRandom());
	
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (Throwable ignored)
			{ }
		}
	}
	
	/**
	 * Trust managers which accept all certificates.
	 */
	private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]
	{ 
			new X509TrustManager()
			{
				@Override
				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
		
				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType)
				{ }
		
				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType)
				{ }
			}
	};
	
	/**
	 * Entry-point; boots the Spring application.
	 * 
	 * @param args Command-line arguments
	 */
	public static void main(String[] args)
	{
		SpringApplication.run(EasyButton.class, args);
	}
}
