# Print API Usage Guide

## Endpoint
**URL**: `http://<KIOSK_IP>:8686/print`
**Method**: `POST`
**Headers**:
- `Content-Type`: `application/json`

> [!NOTE]
> The `X-Api-Key` requirement has been removed for ease of use within the local Kiosk network.

## JSON Payload Structure

The server supports two formats. The **Receipt Format** is recommended for new integrations as it provides structured formatting and logo support.

### 1. Receipt Format### Recommended: Custom Receipt Payload (`/print`)
Use the `receipt` root object for a professionally formatted, branded receipt.

```json
{
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
    "order_placed_at": "2026-02-17 16:55",
    "payment_status": "PAID"
  }
}
```

#### Receipt Fields Detail
| Field | Type | Description |
| :--- | :--- | :--- |
| `order_type` | String | Printed at the top in large 3x font (e.g., "DINE IN", "TAKE AWAY"). |
| `order_number` | String | Printed in large 2x font. |
| `total_items` | String | Summary of item count. |
| `items` | Array | List of objects containing `qty`, `name`, `price`, and optional `options`. |
| `options` | Array | (Optional) List of strings (suboptions) printed indented with bullets under an item. |
| `order_placed_at`| String | (Optional) Timestamp printed at the bottom. |
| `payment_status` | String | (Optional) Printed as a bold footer (e.g., "PAID", "UNPAID"). |

#### Design Notes
- **Logo**: The "Boons" brand logo is printed at the top (1.5x size).
- **Items Row**: Item names are printed in **Bold** to match the timestamp styling.
- **Separators**: Dashed lines are added between metadata sections and around the items table for clear structure.
- **Indentation**: Suboptions are automatically indented with a `*` bullet for better readability.

---

### Legacy Payload Format (Backward Compatibility)
The server still supports the legacy `order` structure for existing integrations.

```json
{
  "order": {
    "type": "DINE IN",
    "number": "A123",
    "items": "3",
    "table": [
      { "qty": "1", "name": "Burger", "price": "$10" }
    ]
  }
}
```

## Runtime Implementation (No Hardcoding)

To ensure the app always finds the printer server, fetch the device IP at runtime instead of hardcoding it.

### React Native Example
```javascript
import { NetworkInfo } from "react-native-network-info";

async function printReceipt(receiptData) {
  const ip = await NetworkInfo.getIPV4Address();
  const url = `http://${ip}:8686/print`;

  await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ receipt: receiptData })
  });
}
```

## Example curl Request (Receipt Format)

```bash
curl -X POST http://192.168.1.164:8686/print \
  -H "Content-Type: application/json" \
  -d '{
    "receipt": {
        "order_type": "DINE IN",
        "order_number": "E64",
        "total_items": "3",
        "items": [
            { "qty": "1", "name": "Classic Burger", "price": "12.50" },
            { "qty": "2", "name": "French Fries (L)", "price": "9.00" }
        ],
        "order_placed_at": "2026-02-17 15:30",
        "payment_status": "PAID"
    }
}'
```
