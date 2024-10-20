#!/bin/bash


docker container rm sign-http
docker container stop sign-http

docker run --restart always -d --name sign-http --network host \
  -v $(pwd)/my-httpd.conf:/usr/local/apache2/conf/httpd.conf \
  -v $(pwd):/usr/local/apache2/htdocs/ \
  httpd:2.4
