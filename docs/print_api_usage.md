# Print API Usage Guide

## Endpoint
**URL**: `http://<KIOSK_IP>:8686/print`
**Method**: `POST`
**Headers**:
- `Content-Type`: `application/json`
- `X-Api-Key`: `Boons_Secure_Print_Kiosk_2025!`

## JSON Payload Structure

The server accepts a JSON object with an `order` field.

```json
{
  "order": {
    "orderNumber": "1001",
    "total": "45.00",
    "footerMessage": "Thank you!\nVisit us at boons.com",
    "items": [
      {
        "name": "Cheeseburger",
        "qty": "2",
        "price": "15.00"
      }
    ],
    "headerMessage": "My Custom Shop"
  }
}
```

## Field Origins

| Field | Source | Description |
|-------|--------|-------------|
| **Date** | **Server (Kiosk)** | The printed date/time is taken directly from the **Android System Time** at the moment of printing. It is *not* sent by the client. |
| **orderNumber** | Client | The unique order ID sent in the JSON payload. |
| **headerMessage** | Client | **(New)** Custom title at the top. Defaults to "Boons Kiosk Order". |
| **items** | Client | List of items. Each item has `name`, `qty`, and `price`. |
| **qty** | Client | **(New)** Quantity of the item (defaults to "1" if missing). |
| **total** | Client | The total amount string sent in the JSON payload. |
| **total** | Client | The total amount string sent in the JSON payload. |
| **footerMessage** | Client | **(New)** Custom text printed at the bottom of the receipt. If omitted, the default "Thank you for your ordering!" is used. |

## Example Request

```bash
curl -X POST http://192.168.1.50:8686/print \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: Boons_Secure_Print_Kiosk_2025!" \
  -d '{
    "order": {
        "orderNumber": "555",
        "total": "12.99",
        "footerMessage": "   Have a Great Day!\n     See you soon.",
        "items": [
            { "name": "Coffee", "price": "4.99" },
            { "name": "Bagel", "price": "8.00" }
        ]
    }
}'
```
