#!/bin/bash
rm -rf build
rm -rf src/main/resources/static/*
rm -rf src/main/resources/templates/index.html
cp -r ../circuit-breaker-ui/dist/circuit-breaker-ui/browser/* src/main/resources/static/
mv  src/main/resources/static/index.html src/main/resources/templates