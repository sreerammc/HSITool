# Quick Command Reference

## Single Point ID Query - Summary Mode

For point ID **10069996** from **October 27, 2025** to **November 30, 2025**:

### Basic Summary Command (CSV Output)
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 10069996 "2025-10-27T00:00:00Z" "2025-11-30T23:59:59Z" summary_10069996.csv summary
```

### Summary with CPU Control (Recommended for Xeon CPU)
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 10069996 "2025-10-27T00:00:00Z" "2025-11-30T23:59:59Z" summary_10069996.csv summary --threads=4
```

### Summary without Files (Faster, Lower Memory)
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 10069996 "2025-10-27T00:00:00Z" "2025-11-30T23:59:59Z" summary_10069996.csv summary --threads=4 --no-files
```

### Summary with Verbose Output
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 10069996 "2025-10-27T00:00:00Z" "2025-11-30T23:59:59Z" summary_10069996.csv summary --threads=4 --verbose
```

## Individual Mode (Detailed Analysis)

If you need detailed analysis instead of summary:

### Basic Command (Individual Mode)
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 10069996 "2025-10-27T00:00:00Z" "2025-11-30T23:59:59Z"
```

### With Custom Output File
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 10069996 "2025-10-27T00:00:00Z" "2025-11-30T23:59:59Z" result_10069996.txt
```

## Date Format Notes

- **From date**: `2025-10-27T00:00:00Z` (October 27, 2025 at midnight UTC)
- **To date**: `2025-11-30T23:59:59Z` (November 30, 2025 at end of day UTC)
- **Note**: November only has 30 days, so I used November 30th. If you meant a different date, adjust accordingly.

## Alternative Date Ranges

If you meant **October 27 to October 31**:
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 10069996 "2025-10-27T00:00:00Z" "2025-10-31T23:59:59Z"
```

If you meant **October 27 to December 1**:
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 10069996 "2025-10-27T00:00:00Z" "2025-12-01T00:00:00Z"
```

