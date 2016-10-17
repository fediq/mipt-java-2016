#!/bin/bash

set -e

if [ -z "${TRAVIS_PULL_REQUEST}" ] || [ "${TRAVIS_PULL_REQUEST}" == "false" ]; then
    echo "Building push"
    mvn test -B
else
    echo "Building pull request ${TRAVIS_PULL_REQUEST}"
    PROJECTS=$(git diff origin/master..HEAD --name-only | grep 'homework-g' | sed -e 's/^\([^\/]*\)\/.*$/\1/' | sort | uniq | awk 'BEGIN{ORS=","}; {print $1}')
    if [ -n "$PROJECTS" ]; then
        echo "Modified modules: ${PROJECTS}"
    	mvn test -B -amd -am -pl ${PROJECTS}
    else
        echo "Cannot detect modified modules; running full test"
        mvn test -B
    fi
fi