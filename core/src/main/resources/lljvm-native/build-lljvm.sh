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

# Installs any application tarball given a URL, the expected tarball name,
# and, optionally, a checkable binary path to determine if the binary has
# already been installed
## Arg1 - URL
## Arg2 - Tarball Name
## Arg3 - Checkable Binary
install_app() {
  local remote_archive="$1/$2"
  local local_archive="${_DIR}/$2"
  local binary="${_DIR}/$3"

  if [ -z "$3" -o ! -f "$binary" ]; then
    download_app "${remote_archive}" "${local_archive}"

    case "$local_archive" in
      *\.tar.xz)
        cd "${_DIR}" && tar xvf "$2"
        ;;
    esac
    rm -rf "$local_archive"
  fi
}

# Downloads any application given a URL
## Arg1 - Remote URL
## Arg2 - Local file name
download_app() {
  local remote_url="$1"
  local local_name="$2"

  # setup `curl` and `wget` options
  local curl_opts="--progress-bar -L"
  local wget_opts="--progress=bar:force"

  # checks if we already have the given application
  # checks if we have curl installed
  # downloads application
  [ ! -f "${local_name}" ] && [ $(command -v curl) ] && \
    echo "exec: curl ${curl_opts} ${remote_url}" 1>&2 && \
    curl ${curl_opts} "${remote_url}" > "${local_name}"
  # if the file still doesn't exist, lets try `wget` and cross our fingers
  [ ! -f "${local_name}" ] && [ $(command -v wget) ] && \
    echo "exec: wget ${wget_opts} ${remote_url}" 1>&2 && \
    wget ${wget_opts} -O "${local_name}" "${remote_url}"
  # if both were unsuccessful, exit
  [ ! -f "${local_name}" ] && \
    echo -n "ERROR: Cannot download $2 with cURL or wget; " && \
    echo "please install manually and try again." && \
    exit 2

  echo ""
}

# Determines the LLVM version from the root pom.xml file and
# installs LLVM under the current folder if needed.
install_llvm() {
  # TODO: Currently, this script supports AWS Ubuntu Server
  # 16.04 LTS on AWS only.
  #
  # You must say lines below before you run this script:
  # $ sudo apt-get update
  # $ sudo apt-get install build-essential python zlib1g-dev libtinfo-dev
  local platform="linux-gnu-ubuntu-16.04"
  local llvm_version=`grep "<llvm.version>" "${_DIR}/../../../../../pom.xml" | head -n1 | awk -F '[<>]' '{print $3}'`

  install_app \
    "http://releases.llvm.org/${llvm_version}" \
    "clang+llvm-${llvm_version}-x86_64-${platform}.tar.xz" \
    "clang+llvm-${llvm_version}-x86_64-${platform}/bin/llvm-config"

  LLVM_DIR=${_DIR}/"clang+llvm-${llvm_version}-x86_64-${platform}"
}

# Installs LLVM first
install_llvm

# Then, builds a native library for the current platform
PATH=${PATH}:${LLVM_DIR}/bin LLVM_CONFIG=llvm-config CXX=${LLVM_DIR}/bin/clang++ ${_DIR}/waf configure
${_DIR}/waf -v

