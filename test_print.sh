#!/bin/bash

# Kiosk Print - Test Script
# Adjust the IP address below if it changes
KIOSK_IP="192.168.1.164"
PORT="8686"

echo "Sending test print to Kiosk at $KIOSK_IP..."

curl -X POST "http://$KIOSK_IP:$PORT/print" \
  -H "Content-Type: application/json" \
  -d '{
    "receipt": {
        "order_type": "DINE IN",
        "order_number": "E64",
        "total_items": "4",
        "items": [
            { 
                "qty": "2", 
                "name": "Double Cheeseburger", 
                "price": "$18.00",
                "options": ["Extra Cheese", "Well Done", "No Onion"]
            },
            { "qty": "1", "name": "Large Fries", "price": "$4.50" },
            { 
                "qty": "1", 
                "name": "Strawberry Shake", 
                "price": "$5.99",
                "options": ["Whipped Cream"]
            }
        ],
        "order_placed_at": "2026-02-17 15:22",
        "payment_status": "PAID"
    }
}'

echo -e "\n\nDone."
