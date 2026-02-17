#!/bin/bash

# Kiosk Print - Test Script
# Adjust the IP address below if it changes
KIOSK_IP="192.168.1.164"
PORT="8686"

echo "Sending test print to Kiosk at $KIOSK_IP..."

curl -X POST "http://$KIOSK_IP:$PORT/print" \
  -H "Content-Type: application/json" \
  -d '{
    "order": {
        "orderNumber": "TEST-SCRIPT",
        "total": "12.99",
        "headerMessage": "    Kiosk Print Test",
        "footerMessage": "    Sent via local script",
        "items": [
            { "name": "Coffee", "qty": "1", "price": "4.99" },
            { "name": "Bagel", "qty": "1", "price": "8.00" }
        ]
    }
}'

echo -e "\n\nDone."
