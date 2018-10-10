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

#
# Shell script to compile hsdis on this env
#
# Note: Before you run this script, you need to install `build-essential`:
#  apt-get =>
#    $ apt-get install build-essential
#  yum =>
#    $ yum groupinstall "Development Tools"

set -e -o pipefail

if [ -z "${JAVA_HOME}" ]; then
  echo "env JAVA_HOME not defined" 1>&2
  exit 1
fi

# Checks an OS name in the current platform
_OS_NAME="$(uname -s)"
case "${_OS_NAME}" in
  Linux*)  TARGET_OS="linux";;
  Darwin*) TARGET_OS="macosx";;
  *)       echo "Unsupported OS: ${_OS_NAME}" 1>&2; exit 1
esac

TARGET_ARCH="amd64"

# JDK version used to compile hsdis (e.g., jdk8u, jdk9, ...)
JDK_VERSION="jdk9"
BINUTILS_VERSION="2.28"

# Checks if mercurial installed
if ! type "hg" > /dev/null 2>&1; then
  echo "You need to install mercurial first" 1>&2
  exit 1
fi

# Determines the current working directory
_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Function to run a given command
run_cmd() {
  local command="$1"
  local working_dir="$2"

  # Preserves the calling directory
  _CALLING_DIR="$(pwd)"

  # Runs the given command and check if it works well
  cd ${working_dir} && ${command}
  if [ $? = 127 ]; then
    echo "Cannot run '${command}', so check if the command works"
    exit 1
  fi

  # Resets the current working directory
  cd "${_CALLING_DIR}"
}

# Checks out hotspot from the repository
_HOTSPOT_DIR=${_DIR}/hotspot
_HSDIS_DIR=${_HOTSPOT_DIR}/src/share/tools/hsdis
if [ ! -x "${_HOTSPOT_DIR}" ]; then
  run_cmd "hg clone http://hg.openjdk.java.net/${JDK_VERSION}/${JDK_VERSION}/hotspot hotspot" "${_DIR}"
fi

# Fetches binutils with the given version
_BINUTILS=binutils-${BINUTILS_VERSION}
_BINUTILS_URL="https://ftp.gnu.org/gnu/binutils"
if [ ! -x "${_HSDIS_DIR}/${_BINUTILS}" ]; then
  run_cmd "wget ${_BINUTILS_URL}/${_BINUTILS}.tar.gz" "${_HSDIS_DIR}"
  run_cmd "tar xvf ${_BINUTILS}.tar.gz" "${_HSDIS_DIR}"
fi

# Does compile hsdis
echo "Copying the compiled hsdis(arch=${TARGET_ARCH}) into ${JAVA_HOME}/jre/lib/server/"
cd ${_HSDIS_DIR} &&                                   \
  make BINUTILS=./${_BINUTILS} ARCH=${TARGET_ARCH} && \
  sudo cp build/${TARGET_OS}-${TARGET_ARCH}/hsdis-${TARGET_ARCH}.* ${JAVA_HOME}/jre/lib/server/

