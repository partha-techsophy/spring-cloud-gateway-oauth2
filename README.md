# Springcloud gateway with OAuth2 integration

In this sample project we will learn 

* How to secure services using OAuth2
* How to configure gateway
* How to make service-to-service call

## Project details
We will create 3 projects as listed bellow
* API Gateway - Route all incoming traffics
* Service-1 - A `hello` service and an `internal-call` service which will call service-2 (Servive-to-Service call)
* Service-2 - A `hello` service

We will use keycloak as OAuth2 provider.

## Pre requisite
* Docker
* Minikube - To run local kuberenetes
* kubectl - Kubernetes CLI
* Skaffold - To deploy and test in kbernetes
* Java SDK 11.x
* IDE (Intellij/ Eclipse)

## Installation
* Install docker [https://docs.docker.com/engine/install/]
* Install minikube [https://minikube.sigs.k8s.io/docs/start/]
* Install kubectl [https://kubernetes.io/docs/tasks/tools/]
* install skaffold [https://skaffold.dev/docs/install/]

## Run minikube
To start minikube execute the following command
```
$ minikube start
```

Enable addon Ingress
```
$ minikube addons enable ingress
```

Enable load balancer, keep this programm running in a separate window
```
$ minikube tunnel
``` 

## Prepare Keycloak
Deploy keycloak in kubernetes with default settings. It will use embaded database. To start keycloak in minikube run the following command

```
$ kubectl create -f keycloak.yaml
```

Login to keycloak to configure a realm, client and user. Before we can login we have to know the IP address of the keycloak service. Run the following commnand to know the external IP address asssigned to keycloak service
```
$ kubectl get svc
```

Log in to keycloak admin console and configure keycloak as described in this [article](https://medium.com/@bcarunmail/securing-rest-api-using-keycloak-and-spring-oauth2-6ddf3a1efcc2). Login with default user `admin` and password `admin`

- Create a demo realm, name it `demo-realm`. 
- Click Add realm -> `Name = demo-realm` -> Click create.
- While in demo-realm click `Clients`
- * Click `Create` -> `Client ID = springcloud` -> Click Save
- Scroll down and update field `Valid Redirect URIs = *`
- To add user click on Users. Click Add User -> `Username = user1` -> Click Save. 
- To set user password click Credentials -> Enter password and uncheck Temporary -> Click Save. 

## Gateway project
Configure the gateway project. Open application.yaml file and make the following changes;

* Replace the IP address of `token-uri`, `authorization-uri`, `userinfo-uri` with your keucloak serviec external address

After replacing the IP address run the following command to build and deploy the gateway service in keubernetes.

```
$ skaffold run
```
To undeploy the application from kubernetes run the following command
```
$ skaffold delete
```

To check if deployment is complete run the following command in a terminal. 

```
$ kubectl get po -w
```

Output of the above command will show pod deployment in progress as shown below.
```
NAME                            READY   STATUS    RESTARTS   AGE
cloud-gateway-cdcdf45df-m5gs5   0/1     Pending   0          1s
```

To view application log run the following command using the name from above list
```
kubectl logs -f <NAME>
```

List the external IP address `kubectl get svc`

Open a browser and type the IP address `<GATWAY_IP_ADDRESS:PORT>/login`. This will redirect to Keycloak login page. Login with the user created before `user1`. On login sucess we can check the token generated.

Getway serive has 2 endpoints

* View the token - `<GATWAY_IP_ADDRESS:PORT>/token`
* View the OAuth session id - `<GATWAY_IP_ADDRESS:PORT>/`

<br>

> Note: You may get the following error; 
Message: Forbidden!Configured service account doesn’t have access. Service account may have been revoked. endpoints is forbidden: User "system:serviceaccount:demo:default" cannot list endpoints in the namespace "demo": no RBAC policy matched.`

To reolve the above error run the following command and deploy the application again.

``` 
$ kubectl create clusterrolebinding admin --clusterrole=cluster-admin --serviceaccount=default:default
```

## Service-1
Configure the service project before deploying. 

* Update `application.yaml` file and replace `issuer-uri` with keycloak IP address.

Deploy the applicaion same as the Gateway application

To test the `hello` service in browswer type `<GATEWAY_IP:PORT>/service-1/hello`. This will return a greeting message.

## Service-2
Configure the service project before deploying. 

* Update `application.yaml` file and replace `issuer-uri` with keycloak IP address.

Deploy the applicaion same as the Gateway application

To test the `hello` service in browswer type `<GATEWAY_IP:PORT>/service-2/hello`. This will return a greeting message.

## Test Service-To-Service call
In browser type type `<GATEWAY_IP:PORT>/service-1/internal-call`. This will return a greeting message from service-2.

## Cleanup 
To cleanup runn the following command in sequence
```
$ service-2/skaffold delete

$ service-1/skaffold delete

$ cloud-gateway/skaffold delete

$ kubectl delete -f keycloak.yaml

$ Ctrl+c to terminate minikube tunnel

$ mininube stop
```
