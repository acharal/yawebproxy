# Yet Another Web Proxy

## Introduction

Web proxy is a program that performs http requests to http servers on
behalf of clients. The main advantage of a web proxy is that it can
cache the content it receives from servers in order to increase availability,
reduce total work on web servers, and reduce the total bandwidth required.

## Proxy Architecture

The architecture of this proxy scheme is as follows. There is a "forward proxy" that listens for requests from other http clients (e.g. web browsers) and forwards all the requests to the "validate proxy". The validate proxy is responsible for contacting the original servers and reply to the forward proxy. Both the forward and the validate proxy caches their content.

The validate proxy is responsible for checking if both caches are consistent. If the validate proxy's cache has a more recent copy of an object than the forward proxy, then it sends the cached object. If both caches have the same copy of the object then the validate proxy replies that the object hasn't changed. Otherwise the validate proxy contacts the original servers, tunnelling the request to the forward proxy. Both the forward proxy and the validate proxy update their caches storing the new object.

## Communication between Proxies

The forward and the validate proxy use http protocol to communicate using the headers "Last-modified" and "If-Modified-Since" and the response "304 Not Modified" when needed.

## Build

Source is packaged in a maven project. To build the project and package it to a single jar file you must type

    mvn package

To execute the forward and the validate proxy you must execute

    java -cp target/yawebproxy-1.0-SNAPSHOT.jar gr.uoa.di.acharal.yawebproxy.ForwardProxy {port-to-listen} {ip-of-validproxy} {port-of-validproxy}
    java -cp target/yawebproxy-1.0-SNAPSHOT.jar gr.uoa.di.acharal.yawebproxy.ValidateProxy {port-to-listen}
