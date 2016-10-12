#!/bin/sh

set -e

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
    echo "Building push"
    mvn test -B
else
    echo "Building pull"
    PROJECTS=$(git diff origin/master..HEAD --name-only | grep '/' | sed -e 's/^\([^\/]*\)\/.*$/\1/' | sort | uniq | awk 'BEGIN{ORS=","}; {print $1}')
    mvn test -B -amd -am -pl ${PROJECTS}
fi
