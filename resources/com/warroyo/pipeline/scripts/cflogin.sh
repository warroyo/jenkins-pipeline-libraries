#!/bin/bash
set -e

cf login -a $API_URL -u $USERNAME -p $PASSWORD  -s $SPACE -o $ORG