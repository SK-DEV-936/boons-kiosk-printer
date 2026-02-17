# Print API Usage Guide

## Endpoint
**URL**: `http://<KIOSK_IP>:8686/print`
**Method**: `POST`
**Headers**:
- `Content-Type`: `application/json`

> [!NOTE]
> The `X-Api-Key` requirement has been removed for ease of use within the local Kiosk network.

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

## Runtime Implementation (No Hardcoding)

To ensure the app always finds the printer server, fetch the device IP at runtime instead of hardcoding it.

### React Native Example
Use `react-native-network-info` to get the tablet's LAN IP.

```javascript
import { NetworkInfo } from "react-native-network-info";

async function printOrder(orderData) {
  const ip = await NetworkInfo.getIPV4Address();
  const url = `http://${ip}:8686/print`;

  await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ order: orderData })
  });
}
```

### Native Android (Kotlin) Example
```kotlin
fun getPrintUrl(context: Context): String {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val ip = cm.getLinkProperties(cm.activeNetwork)
        ?.linkAddresses?.firstOrNull { it.address is Inet4Address }
        ?.address?.hostAddress
    return "http://$ip:8686/print"
}
```

## Field Origins

| Field | Source | Description |
|-------|--------|-------------|
| **Date** | **Server (Kiosk)** | Printed date/time is taken from Android system time. |
| **orderNumber** | Client | Unique order ID. |
| **headerMessage** | Client | Custom title at top. Defaults to "Kiosk Print Order". |
| **items** | Client | List of items (name, qty, price). |
| **total** | Client | Total amount string. |
| **footerMessage** | Client | Custom text at bottom. |

## Example curl Request

```bash
curl -X POST http://192.168.1.164:8686/print \
  -H "Content-Type: application/json" \
  -d '{
    "order": {
        "orderNumber": "REAL-123",
        "total": "12.99",
        "items": [
            { "name": "Coffee", "price": "4.99" },
            { "name": "Bagel", "price": "8.00" }
        ]
    }
}'
```
