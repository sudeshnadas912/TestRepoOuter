/*******************************************************************************
 * Copyright 2018 TCS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.allianzservice.travelcompanionouterlayercloud.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Configuration
public class SslContextConfiguration {

	private static final Logger LOG = Logger.getLogger(SslContextConfiguration.class);

	@Autowired
	private Environment env;

	private static final String JAVA_KEYSTORE = "jks";
	private static final String CLIENT_TRUSTSTORE = "ssl/truststore.jks";
	private static final String CLIENT_KEYSTORE = "ssl/keystore.jks";

	private final KeyStore clientKeyStore;
	private final KeyStore clientTrustStore;

	private final String keyStorePassword;

	private final SSLContext sslContext;

	public SslContextConfiguration(@Value("${http.client.keystore.password}") final String pwd)
			throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
			KeyManagementException, UnrecoverableKeyException {

		// LOG.info("Class %s loaded.", SslContextConfiguration.class);
		keyStorePassword = pwd;

		clientKeyStore = getStore(CLIENT_KEYSTORE, keyStorePassword.toCharArray());
		clientTrustStore = getStore(CLIENT_TRUSTSTORE, keyStorePassword.toCharArray());

		sslContext = SSLContextBuilder.create().loadKeyMaterial(clientKeyStore, keyStorePassword.toCharArray())
				.loadTrustMaterial(clientTrustStore, null).build();

	}

	@Bean
	public RestTemplate restTemplate(final HttpClient httpclient) {
		final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpclient);
		requestFactory.setReadTimeout(getIntegerValueFromProperty("http.request.read.timeout"));
		requestFactory.setConnectTimeout(getIntegerValueFromProperty("http.request.connect.timeout"));
		requestFactory.setConnectionRequestTimeout(getIntegerValueFromProperty("http.request.connection.timeout"));
		final RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}

	@Bean
	public HttpClient httpClient(final HttpClientBuilder httpClientBuilder) {
		return httpClientBuilder.build();
	}

	@SuppressWarnings("resource")
	@Bean
	public RestTemplate apigeeRestTemplate() {
		// Turn Off SSL Hostname Validation
		final SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
				NoopHostnameVerifier.INSTANCE);
		final CloseableHttpClient httpclient = HttpClients.custom().setSSLContext(sslContext)
				.setSSLSocketFactory(sslSocketFactory).build();
		final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpclient);
		final RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}

	@Bean
	public HttpClientBuilder httpClientBuilder(final HttpClientConnectionManager connectionManager) {
		final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		httpClientBuilder.setConnectionManager(connectionManager);
		httpClientBuilder.setConnectionManagerShared(true);
		httpClientBuilder.setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE);

		final RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder.setRedirectsEnabled(true);

		requestBuilder.setSocketTimeout(getIntegerValueFromProperty("http.socket.timeout"));
		requestBuilder.setConnectTimeout(getIntegerValueFromProperty("http.connection.timeout"));
		requestBuilder.setConnectionRequestTimeout(getIntegerValueFromProperty("http.connection.manager.timeout"));

		httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());
		httpClientBuilder.setSSLContext(sslContext);

		httpClientBuilder.disableAutomaticRetries();
		httpClientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

			@Override
			public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
				return getIntegerValueFromProperty("http.keep.alive.duration");
			}
		});

		return httpClientBuilder;
	}

	@Bean
	public HttpClientConnectionManager connectionManager() throws Exception {
		final Iterable<String> protocols = Splitter.on(",").split(env.getProperty("https.protocols", "TLSv1"));
		Iterable<String> cipherSuites = Splitter.on(",").split(env.getProperty("https.cipherSuites", ""));

		final SocketFactory sf = SSLSocketFactory.getDefault();
		if (sf instanceof SSLSocketFactory) {
			final SSLSocketFactory ssf = (SSLSocketFactory) sf;

			if (cipherSuites == null) {
				cipherSuites = Lists.newArrayList(ssf.getDefaultCipherSuites());
			}
		}

		final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
				Iterables.toArray(protocols, String.class), null, new DefaultHostnameVerifier());

		final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", sslsf).build();

		final PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);

		final Integer maxConnections = getIntegerValueFromProperty("http.maxConnections", 50);
		poolingmgr.setMaxTotal(maxConnections);

		final Integer maxPerRoute = getIntegerValueFromProperty("http.maxPerRoute", 10);
		poolingmgr.setDefaultMaxPerRoute(maxPerRoute);

		poolingmgr.setValidateAfterInactivity(1200 * 1000);

		return poolingmgr;
	};

	// @Bean
	// public SecurityTokenValidator tokenValidator() {
	// return new SecurityTokenValidatorBuilder() //
	// .withCertificateRevocationCheckMode(CertificateRevocationCheckMode.DISABLED)
	// // rootca.allianz.com currently not reachable from AZDCloud
	// .withTrustedTokenSignerDNs( //
	// "CN=psec_oauthprov_jwtsigner_test.jks,OU=Webservice,O=Allianz,C=DE", //
	// Using Test TokenProvider
	// "CN=psec_oauthprov_jwtsigner_prod.jks,OU=Webservice,O=Allianz,C=DE" //
	// Using Prod TokenProvider
	// ).withCertificateProviderURLs("https://apo-06.abs.muc.allianz/wsbase_app/Lookup?cid="
	// // Only use the Certificate Provider in AWS
	// ) //
	// // .withExecutorService(executorService) //
	// .build();
	// }

	/**
	 * KeyStores provide credentials, TrustStores verify credentials.
	 *
	 * Server KeyStores stores the server's private keys, and certificates for
	 * corresponding public keys. Used here for HTTPS connections over
	 * localhost.
	 *
	 * Client TrustStores store servers' certificates.
	 */
	protected KeyStore getStore(final String storeFileName, final char[] password)
			throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

		final KeyStore store = KeyStore.getInstance(JAVA_KEYSTORE);
		final URL url = SslContextConfiguration.class.getClassLoader().getResource(storeFileName);
		final InputStream inputStream = url.openStream();
		try {
			store.load(inputStream, password);
			
		} finally {
			inputStream.close();
		}

		return store;
	}

	private Integer getIntegerValueFromProperty(final String propName) {
		return getIntegerValueFromProperty(propName, 5000);
	}

	private Integer getIntegerValueFromProperty(final String propName, final int defaultValue) {
		final Integer property = env.getProperty(propName, Integer.class, defaultValue);
		// LOG.info("Integer property '%s' with value '%s' has been set",
		// propName, property);
		return property;
	}
}
