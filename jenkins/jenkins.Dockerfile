FROM jenkins/jenkins:lts

USER root

RUN apt-get update && \
    apt-get install -y \
      apt-transport-https \
      ca-certificates \
      curl \
      gnupg2 \
      lsb-release && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] \
      https://download.docker.com/linux/debian $(lsb_release -cs) stable" \
      > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce docker-ce-cli containerd.io
RUN git config --global --add safe.directory /usr/local/proyectos/PupaSv/.git
RUN git config --global --add safe.directory /usr/local/proyectos/PupaSvFE/.git
RUN git config --global --add safe.directory /usr/local/proyectos/PupaSvETE/.git
