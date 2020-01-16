package com.chavessummer.websocket.service;

import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@Service
public class WebSocketStompSessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketStompSessionService.class);

    private WebSocketProperties webSocketProperties;

    private WebSocketStompSessionHandler sessionHandler;

    private StompSession stompSession;
    
    @Value("${server.ssl.enabled:false}")
    private Boolean sslEnabled;
    @Value("${server.ssl.key-store}")
    private String trustStore;
    @Value("${server.ssl.key-store-password}")
    private String trustStorePassword;

    public WebSocketStompSessionService(WebSocketProperties webSocketProperties, SimpMessageSendingOperations messagingTemplate) {
        this.webSocketProperties = webSocketProperties;
        sessionHandler = new WebSocketStompSessionHandler(messagingTemplate, this);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void getConnect() throws InterruptedException, ExecutionException {
        new Thread(new Runnable() {
            @Override
            public void run() {
            	try {
					Thread.sleep(webSocketProperties.getInitialConnect());
				} catch (InterruptedException e1) {
					LOGGER.error(e1.getMessage(), e1);
				}
                boolean keep = true;
                while (keep) {
                    try {
                        stompSession = connect().get();
                        if (stompSession.isConnected()) {
                            keep = false;
                        }
                    } catch (Exception e) {
                        LOGGER.error("", e);
                        try {
                            Thread.sleep(webSocketProperties.getRetryTimeout());
                        } catch (InterruptedException e2) {
                            LOGGER.error("", e2);
                        }
                    }
                }
            }
        }).start();
    }

    public void send(String topic, Object o) {
        stompSession.send(topic, o);
    }

    private ListenableFuture<StompSession> connect() throws NoSuchAlgorithmException {
        List<Transport> transports = new ArrayList<>(2);
        StandardWebSocketClient wsClient = new StandardWebSocketClient();

        if (sslEnabled)
        {
	        SSLContext sc = SSLContext.getInstance("TLS");
	        try {
	        
		        KeyStore ks = KeyStore.getInstance("JKS");
		        ks.load(new URL(trustStore).openStream(), trustStorePassword.toCharArray());
		
		        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		        kmf.init(ks, trustStorePassword.toCharArray());
		        KeyManager[] kms = kmf.getKeyManagers();
		
		        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		        tmf.init(ks);
		        
		        TrustManager[] trustAllCerts = new TrustManager[] {new X509ExtendedTrustManager() {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		
		            @Override
		            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString)
		                    throws CertificateException {
		                // TODO Auto-generated method stub
		            }
		
		            @Override
		            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString)
		                    throws CertificateException {
		                // TODO Auto-generated method stub
		            }
	
					@Override
					public void checkClientTrusted(X509Certificate[] arg0, String arg1, Socket arg2)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}
	
					@Override
					public void checkClientTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}
	
					@Override
					public void checkServerTrusted(X509Certificate[] arg0, String arg1, Socket arg2)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}
	
					@Override
					public void checkServerTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}
		        }};
	
		        sc.init(kms, trustAllCerts, new java.security.SecureRandom());
		        sc.getDefaultSSLParameters().setEndpointIdentificationAlgorithm(null);
		        
	        } catch (Exception ex){
	        	ex.printStackTrace();
	        }
	        
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };
	        
	        // set the  allTrusting verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        
	        wsClient.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sc);
        }
        
        transports.add(new WebSocketTransport(wsClient));
        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        return stompClient.connect(webSocketProperties.getMasterUrl(), sessionHandler);
    }

}
