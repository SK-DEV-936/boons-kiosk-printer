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
        "order_type": "Dine-In",
        "order_number": "E35",
        "total_items": "7",
        "subtotal": "44.00",
        "tip": "5.00",
        "tax": "6.00",
        "total": "55.00",
        "items": [
            { 
                "qty": "1", 
                "name": "Classic Burger", 
                "price": "12.00",
                "options": [
                    {
                        "name": "Add-ons",
                        "sub_options": ["Extra Cheese", "Bacon"]
                    },
                    "No Onions"
                ]
            },
            { 
                "qty": "1", 
                "name": "Custom Pizza", 
                "price": "16.50",
                "options": [
                    {
                        "name": "Toppings",
                        "sub_options": ["Pepperoni", "Mushrooms"]
                    },
                    {
                        "name": "Crust",
                        "sub_options": ["Thin Crust"]
                    }
                ]
            },
            { "qty": "1", "name": "French Fries", "price": "4.50" },
            { "qty": "1", "name": "Onion Rings", "price": "5.50" },
            { "qty": "2", "name": "Soft Drinks", "price": "6.00" },
            { "qty": "1", "name": "Ice Cream", "price": "4.00" },
            { "qty": "1", "name": "Brownie", "price": "6.50" }
        ],
        "order_placed_at": "Feb 18, 3:10 PM",
        "payment_status": "PAID"
    }
}'

echo -e "\n\nDone."
