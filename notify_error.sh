#!/bin/bash

ERROR_MESSAGE=$1

DATA=$(cat <<EOF
{
  "content": "<@&$DISCORD_ROLE_ID>",
  "embeds": [
    {
      "title": "🚨 Deploy Error",
      "description": "$ERROR_MESSAGE",
      "color": 16711680
    }
  ]
}
EOF
)

curl -X POST -H 'Content-type: application/json' \
     -d "$DATA" \
     ${DISCORD_WEBHOOK_URL}
