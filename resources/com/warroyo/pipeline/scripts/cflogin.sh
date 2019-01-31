#!/bin/bash
set -e

cf login --skip-ssl-validation -a $API_URL -u $USERNAME -p $PASSWORD  -s $SPACE -o $ORG