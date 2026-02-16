#!/bin/bash

# Output file
OUTPUT_FILE="printer_logs.txt"

# Clear previous logs buffer
echo "Clearing old logs..."
adb logcat -c

echo "Listening for logs... (Press Ctrl+C to stop)"
echo "Logs will be saved to $OUTPUT_FILE"

# Capture logs specifically for our tags and general Android runtime errors
# -s filters for specific tags
# We include known tags and "*:E" for any other errors
adb logcat -v time -s PrinterServer PrinterService POSPrinter AndroidRuntime System.err *:E > "$OUTPUT_FILE"
