#!/usr/bin/env bash

#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e -o pipefail

# Determines the current working directory
_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Determines the Numba version to install
_NUMBA_VERSION=`grep "<numba.version>" "${_DIR}/../../pom.xml" | head -n1 | awk -F '[<>]' '{print $3}'`

# Downloads any application tarball given a URL, the expected tarball name,
# and, optionally, a checkable binary path to determine if the binary has
# already been installed
## Arg1 - URL
## Arg2 - Tarball Name
## Arg3 - Checkable Binary
download_app() {
  local remote_tarball="$1/$2"
  local local_tarball="${_DIR}/$2"
  local binary="${_DIR}/$3"

  # setup `curl` and `wget` silent options if we're running on Jenkins
  local curl_opts="-L"
  local wget_opts=""
  if [ -n "$AMPLAB_JENKINS" ]; then
    curl_opts="-s ${curl_opts}"
    wget_opts="--quiet ${wget_opts}"
  else
    curl_opts="--progress-bar ${curl_opts}"
    wget_opts="--progress=bar:force ${wget_opts}"
  fi

  if [ -z "$3" -o ! -f "$binary" ]; then
    # check if we already have the tarball
    # check if we have curl installed
    # download application
    [ ! -f "${local_tarball}" ] && [ $(command -v curl) ] && \
      echo "exec: curl ${curl_opts} ${remote_tarball}" 1>&2 && \
      curl ${curl_opts} "${remote_tarball}" > "${local_tarball}"
    # if the file still doesn't exist, lets try `wget` and cross our fingers
    [ ! -f "${local_tarball}" ] && [ $(command -v wget) ] && \
      echo "exec: wget ${wget_opts} ${remote_tarball}" 1>&2 && \
      wget ${wget_opts} -O "${local_tarball}" "${remote_tarball}"
    # if both were unsuccessful, exit
    [ ! -f "${local_tarball}" ] && \
      echo -n "ERROR: Cannot download $2 with cURL or wget; " && \
      echo "please download manually and try again." && \
      exit 2
    cd "${_DIR}" && tar -xvf "$2"
    rm -rf "$local_tarball"
  fi
}

# Fetches the Numba source code with the version
# https://github.com/numba/numba/archive/0.41.0.tar.gz
download_app \
  "https://github.com/numba/numba/archive" \
  "${_NUMBA_VERSION}.tar.gz" \
  "numba-${_NUMBA_VERSION}/bin/numba"

# Makes a symblic link to the source code
ln -s numba-${_NUMBA_VERSION} numba

# Then, builds a native library for the current platform
# $ sudo pip install numpy scipy
CC=clang ${_DIR}/waf configure
${_DIR}/waf -v

