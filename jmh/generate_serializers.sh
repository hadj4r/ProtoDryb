#!/usr/bin/env bash

protoc -I src/main/proto --java_out=src/main/java src/main/proto/*.proto

flatc -I src/main/flatbuffers --java -o src/main/java/ src/main/flatbuffers/*.fbs