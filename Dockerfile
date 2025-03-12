FROM ubuntu:latest
LABEL authors="gtk"

ENTRYPOINT ["top", "-b"]