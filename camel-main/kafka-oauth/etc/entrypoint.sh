#!/bin/bash
# ------------------------------------------------------------------------------
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# This script defines `sh` as the interpreter, which is available in all POSIX environments. However, it might get
# started with `bash` as the shell to support dotted.environment.variable.names which are not supported by POSIX, but
# are supported by `sh` in some Linux flavours.
# ------------------------------------------------------------------------------

# ------------------------------------------------------------------------------
# Fetches the SSL certificate from the given OAuth Provider and trust it in Java
# ------------------------------------------------------------------------------
if [ -n ${OAUTH_PROVIDER_HOST} ]; then

  providerHost=${OAUTH_PROVIDER_HOST}
  providerPort=${OAUTH_PROVIDER_SSL_PORT:-443}

  truststoreFile=${TRUSTSTORE_LOCATION:-/maven/tls/truststore.jks}
  truststorePass=${TRUSTSTORE_PASSWORD:-changeit}

  certFile="/tmp/${providerHost}.crt"
  mkdir -p $(dirname "${truststoreFile}")

  echo "Fetch the Keycloak cert from ${providerHost}:${providerPort}"
  echo -n | openssl s_client -connect ${providerHost}:${providerPort} -servername ${providerHost} | openssl x509 > ${certFile}
  # cat ${certFile}| openssl x509 -noout -text

  echo "Create truststore ${truststoreFile}"
  keytool -import -alias ${providerHost} -file ${certFile} -keystore ${truststoreFile} -storepass ${truststorePass} -noprompt

  echo "Setting JAVA_OPTS for the truststore..."
  export JAVA_OPTS="${JAVA_OPTS} -Djavax.net.ssl.trustStore=${truststoreFile} -Djavax.net.ssl.trustStorePassword=${truststorePass}"
fi

exec java ${JAVA_OPTS} -jar "$@"