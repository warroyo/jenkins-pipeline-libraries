#!/bin/bash
set -e

cf map-route ${APP_NAME}-green $ROUTE --hostname ${APP_NAME}
cf scale ${APP_NAME}-green -i $SCALE