# websocket-server-scalable-web (1.0)
How to create a WebSocket Server Scalable with SpringBoot and Stomp

# Requiriments:
1. JDK 1.8
2. Wildfly 10.1
3. Apache Maven

 ![arch](https://github.com/edubritochaves/websocket-server-scalable-web/blob/master/repo/arch.png)
 
# How it works
1. The Clients are connected in a web socket server with a unique hostname. In this case the domain is provided by NGINX.
2. There is in NGINX a balance configuration. This configuration garantees that all connections will be distribuied between all instances of web socket servers.
3. Between all servers there must be a master instance responsible for notifying each other whenever a message is received.
4. The notifications should be come by REST API.  Should be send the topic name and the body message. This body message will be send for all clients subscribed in the topic informed regardless of which server the client is connected.
5. All the servers are subscribed in the same internal topic then when a message comes from API this message will be sent to that internal topic, after this, all servers will be send the message for a topic existent in API body message.

# How to Start
1. Build the project with Maven.
2. Copy the artifact generated in Wildfly deployments folder.
3. Starts the Wildfly server.

# How to use
1. After the start, check if the server is up.
```
11:48:57,657 INFO  [org.wildfly.extension.undertow] (ServerService Thread Pool -- 65) WFLYUT0021: Registered web context: /ws-server
11:48:57,698 INFO  [org.jboss.as.server] (ServerService Thread Pool -- 34) WFLYSRV0010: Deployed "ws-server.war" (runtime-name : "ws-server.war")
11:48:57,984 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0060: Http management interface listening on http://127.0.0.1:9990/management
11:48:57,987 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990
11:48:57,987 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0025: WildFly Full 10.1.0.Final (WildFly Core 2.2.0.Final) started in 29523ms - Started 509 of 759 services (404 services are lazy, passive or on-demand)
11:48:59,639 INFO  [io.undertow.servlet] (default task-2) Initializing Spring DispatcherServlet 'dispatcherServlet'
11:48:59,640 INFO  [org.springframework.web.servlet.DispatcherServlet] (default task-2) Initializing Servlet 'dispatcherServlet'
11:48:59,669 INFO  [org.springframework.web.servlet.DispatcherServlet] (default task-2) Completed initialization in 28 ms
11:48:59,930 INFO  [com.chavessummer.websocket.service.WebSocketStompSessionHandler] (default task-6) afterConnected
```
2. Open address http://localhost:8080/ws-server/ws-client
![client](https://github.com/edubritochaves/websocket-server-scalable-web/blob/master/repo/client.png)

3. Type a topic name for this client, click in "Connect" button and after click in "Subscribe".
4. Send a message for API Rest by Postman or anything tool.
```
curl --location --request POST 'http://localhost:8080/ws-server/api' \
--header 'Content-Type: application/json' \
--data-raw '{"topic":"/topic/hello", "data":"Teste WebSocket Scalable"}'
```
5. To test the scability you should have two or more servers in differents ports or in different machines.
6. It's possible to test without a NGINX, for this, connect each client in your own server. 
