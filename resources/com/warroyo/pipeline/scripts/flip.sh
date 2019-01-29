#!/bin/bash
set -e

cf map-route ${APP_NAME}-green $ROUTE --hostname ${APP_NAME}
cf delete $APP_NAME -f

cf delete-route $ROUTE --hostname ${APP_NAME}-green -f