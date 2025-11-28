# BlobUtil - JSON Query Utility

A Java utility to query JSON files by ID and time range, identifying duplicates and unique entries. Supports batch processing from CSV files with memory-optimized streaming for large datasets (40,000+ points).

## Features

- Queries all JSON files in a directory
- Filters by ID and time range (from/to)
- **CSV file support**: Process multiple point IDs from a CSV file
- **Two output modes**: Individual detailed analysis or CSV summary
- **Memory-optimized**: Streaming processing for large datasets
- Identifies duplicate entries
- Lists unique entries separately
- Provides detailed analysis report
- Optional file name tracking in summary mode

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building

```bash
mvn clean package
```

This will create a JAR file with all dependencies in the `target` directory:
- `blobutil-jar-with-dependencies.jar`

## Usage

```bash
java -jar target/blobutil-jar-with-dependencies.jar <dataDirectory> <id|csvFile> <timeFrom> <timeTo> [outputFile] [mode] [--no-files]
```

### Parameters

- `dataDirectory`: Path to directory containing JSON files (e.g., `C:\Users\devvm\Downloads\data`)
- `id|csvFile`: Single point ID (number) OR path to CSV file containing point IDs
- `timeFrom`: Start time in ISO-8601 format (e.g., `2025-11-19T14:00:00.000Z`)
- `timeTo`: End time in ISO-8601 format (e.g., `2025-11-19T15:30:00.000Z`)
- `outputFile`: (Optional) Path to output file. Auto-generated if not specified
- `mode`: (Optional) Output mode: `individual` (default) or `summary`
- `--no-files` or `-nf`: (Optional) Exclude file names from summary output (saves memory)

### Examples

**Single Point ID:**
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 11337808 "2025-11-19T14:00:00.000Z" "2025-11-19T15:30:00.000Z"
```

**CSV File - Individual Mode (Detailed Analysis):**
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" points.csv "2025-11-19T14:00:00.000Z" "2025-11-19T15:30:00.000Z" output.txt individual
```

**CSV File - Summary Mode (CSV Summary):**
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" points.csv "2025-11-19T14:00:00.000Z" "2025-11-19T15:30:00.000Z" summary.csv summary
```

**CSV File - Summary Mode without Files (Memory Optimized):**
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" points.csv "2025-11-19T14:00:00.000Z" "2025-11-19T15:30:00.000Z" summary.csv summary --no-files
```

For detailed usage instructions, see [USAGE.md](USAGE.md).

## Output

The utility will:
1. Process all JSON files in the specified directory
2. Filter objects matching the ID and time range
3. Identify duplicates (objects with identical field values)
4. Display results both to:
   - **Console** (standard output)
   - **Output file** (specified file or auto-generated)
5. Report includes:
   - Total objects found
   - Number of unique objects
   - Number of duplicate groups
   - Detailed list of unique objects
   - Detailed list of duplicate groups with counts

All output is written simultaneously to both the console and the output file, so you can see results in real-time while also having a saved record.

## Memory Management

The utility is optimized for large datasets (40,000+ points):
- **Summary mode**: Uses streaming to process data without loading all objects into memory
- **Summary mode with --no-files**: Even more memory-efficient, skips file name collection
- **Individual mode**: Processes points incrementally, clearing memory after each point
- **Progress indicators**: Shows progress every 1,000 points
- **Automatic GC**: Suggests garbage collection every 5,000 points

For very large datasets, **summary mode with --no-files is recommended** for maximum memory efficiency.

## JSON Structure

The utility expects JSON files with the following structure:

```json
{
    "_name": "",
    "_model": "ComplexData",
    "_timestamp": 1763564604591,
    "ExportedData": {
        "Header": {
            "SystemName": "IRIS",
            "StartDate": "2025-11-18T23:15:00.000Z",
            "EndDate": "2025-11-19T15:02:30.619Z"
        },
        "Objects": [
            {
                "Id": 11337808,
                "Fullname": "...",
                "Time": "2025-11-19T14:45:00.000Z",
                "Value": -9.9999999747524271E-07,
                "Reason": 3,
                "State": "",
                "Units": "",
                "Quality": "Good",
                "QualityNumeric": 192
            }
        ]
    }
}
```


