package com.posprinter.printdemo

import android.util.Log
import fi.iki.elonen.NanoHTTPD
import net.posprinter.POSPrinter
import net.posprinter.POSConst
import org.json.JSONObject
import java.io.IOException

class PrinterServer(port: Int) : NanoHTTPD(port) {

    private val API_KEY = "Boons_Secure_Print_Kiosk_2025!"
    private val MAX_PAYLOAD_SIZE = 2 * 1024 * 1024 // 2MB
    private val printerLock = Any()

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val method = session.method
        val headers = session.headers

        Log.d("PrinterServer", "Request: $method $uri")

        // CORS Preflight
        if (Method.OPTIONS == method) {
            val response = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, null)
            addCorsHeaders(response)
            return response
        }

        // 1. Payload Size Check (Security)
        val contentLength = headers["content-length"]?.toLongOrNull() ?: 0
        if (contentLength > MAX_PAYLOAD_SIZE) {
            Log.e("PrinterServer", "Payload too large: $contentLength")
            return errorResponse("Payload too large (Max 2MB)", Response.Status.BAD_REQUEST) // 400 Bad Request
        }

        // 2. API Key Authentication (Security) for sensitive endpoints
        if (uri == "/print") {
            val clientKey = headers["x-api-key"] // NanoHTTPD headers are lowercase
            if (clientKey != API_KEY) {
                 Log.w("PrinterServer", "Unauthorized Access Attempt")
                 return errorResponse("Unauthorized: Invalid API Key", Response.Status.UNAUTHORIZED) // 401 Unauthorized
            }
        }

        if ("/health" == uri && Method.GET == method) {
            val response = newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"alive\"}")
            addCorsHeaders(response)
            return response
        }

        if ("/logs" == uri && Method.GET == method) {
            return handleLogs(session)
        }

        if ("/print" == uri && Method.POST == method) {
            synchronized(printerLock) { // 3. Thread Safety
                return handlePrint(session)
            }
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
    }

    private val MAX_LOG_SIZE = 10 * 1024 * 1024 // 10MB limit

    private fun handleLogs(session: IHTTPSession): Response {
        val params = session.parms
        val filter = params["filter"]

        try {
            // Execute logcat command to get logs
            // Added POSConnect to tags
            val process = Runtime.getRuntime().exec("logcat -d -v time -s PrinterServer PrinterService POSPrinter POSConnect AndroidRuntime System.err")
            val reader = java.io.BufferedReader(java.io.InputStreamReader(process.inputStream))
            val logBuilder = StringBuilder()
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                if (filter.isNullOrEmpty() || line!!.contains(filter, ignoreCase = true)) {
                    logBuilder.append(line).append("\n")
                    
                    // Security: Prevent log response from exceeding 10MB
                    if (logBuilder.length > MAX_LOG_SIZE) {
                        logBuilder.append("\n[LOGS TRUNCATED - EXCEEDED 10MB LIMIT]\n")
                        break
                    }
                }
            }
            
            val response = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, logBuilder.toString())
            addCorsHeaders(response)
            return response
        } catch (e: Exception) {
            Log.e("PrinterServer", "Failed to fetch logs", e)
            return errorResponse("Failed to fetch logs: ${e.message}")
        }
    }

    private fun handlePrint(session: IHTTPSession): Response {
        val map = HashMap<String, String>()
        try {
            session.parseBody(map)
        } catch (e: Exception) {
            Log.e("PrinterServer", "Body Parse Error", e) // Log full stack trace
            return errorResponse("Failed to parse body: ${e.message}")
        }

        val jsonString = map["postData"]
        Log.d("PrinterServer", "Payload: $jsonString") // Log the raw payload
        if (jsonString == null) {
             Log.e("PrinterServer", "Error: No 'postData' found in request")
             return errorResponse("No body found. Make sure to send raw JSON.")
        }

        try {
            val json = JSONObject(jsonString)
            
            // Handle Raw Content (Legacy Support)
            if (json.has("content")) {
                val content = json.getString("content")
                if (content.startsWith("MOCK:")) {
                    Log.d("PrinterServer", "Mock Print Success: $content")
                    val response = newFixedLengthResponse(Response.Status.OK, "application/json", "{\"success\": true, \"mock\": true}")
                    addCorsHeaders(response)
                    return response
                }
                
                val curConnect = App.get().curConnect
                val isConnected = curConnect?.isConnect ?: false
                Log.d("PrinterServer", "Checking Printer Connection: $isConnected")
                
                if (!isConnected) {
                    Log.e("PrinterServer", "Error: Printer not connected")
                    return errorResponse("Printer not connected")
                }
                val printer = POSPrinter(curConnect)
                printer.initializePrinter()
                    .printString(content)
                    .feedLine()
                    .cutHalfAndFeed(1)
                    
                Log.i("PrinterServer", "Print Job Sent Successfully")
                val response = newFixedLengthResponse(Response.Status.OK, "application/json", "{\"success\": true}")
                addCorsHeaders(response)
                return response
            }

            // Handle Structured Order
            if (json.has("order")) {
                val order = json.getJSONObject("order")
                val orderNum = order.optString("orderNumber", "000")
                val items = order.optJSONArray("items")
                val total = order.optString("total", "0.00")
                Log.d("PrinterServer", "Processing Order #$orderNum")
                
                // MOCK CHECK for Structured Data
                if (orderNum.startsWith("MOCK")) {
                     Log.d("PrinterServer", "Mock Order Print Success")
                     val response = newFixedLengthResponse(Response.Status.OK, "application/json", "{\"success\": true, \"mock\": true}")
                     addCorsHeaders(response)
                     return response
                }

                val curConnect = App.get().curConnect
                val isConnected = curConnect?.isConnect ?: false
                Log.d("PrinterServer", "Checking Printer Connection for Order: $isConnected")

                if (!isConnected) {
                    Log.e("PrinterServer", "Error: Printer not connected")
                    return errorResponse("Printer not connected")
                }

                val printer = POSPrinter(curConnect)
                printer.initializePrinter()
                    .printString("--------------------------------\n")
                    .printString("      Boons Kiosk Order\n")
                    .printString("--------------------------------\n")
                    .printString("Order #: $orderNum\n")
                    .printString("Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date())}\n")
                    .printString("\n")
                    .printString(String.format("%-20s %10s\n", "Item", "Price"))
                    .printString("--------------------------------\n")

                if (items != null) {
                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val name = item.optString("name", "Item")
                        val price = item.optString("price", "0.00")
                        
                        // Simple truncation/padding for alignment (assuming monospace font)
                        var displayName = name
                        if (name.length > 20) {
                            displayName = name.substring(0, 17) + "..."
                        }
                        printer.printString(String.format("%-20s %10s\n", displayName, price))
                    }
                }

                printer.printString("--------------------------------\n")
                printer.printString(String.format("%-20s %10s\n", "TOTAL", total))
                printer.printString("--------------------------------\n")
                printer.printString("\n")
                printer.printString("      Thank you for your\n")
                printer.printString("        ordering!\n")
                printer.printString("\n\n")
                .feedLine()
                .cutHalfAndFeed(1)

                Log.i("PrinterServer", "Order #$orderNum Printed Successfully")
                val response = newFixedLengthResponse(Response.Status.OK, "application/json", "{\"success\": true}")
                addCorsHeaders(response)
                return response
            }

            Log.e("PrinterServer", "Invalid JSON Structure: $jsonString")
            return errorResponse("Invalid JSON: Missing 'content' or 'order' field")

        } catch (e: Exception) {
            Log.e("PrinterServer", "Print Execution Failed", e)
            return errorResponse("Print failed: ${e.message}")
        }
    }

    private fun errorResponse(msg: String, status: Response.Status = Response.Status.INTERNAL_ERROR): Response {
        val json = JSONObject()
        json.put("error", msg)
        val response = newFixedLengthResponse(status, "application/json", json.toString())
        addCorsHeaders(response)
        return response
    }

    private fun addCorsHeaders(response: Response) {
        response.addHeader("Access-Control-Allow-Origin", "*")
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, X-Api-Key")
        response.addHeader("Access-Control-Max-Age", "86400")
    }
}
