# JsonQueryUtility - Extended Usage Guide

## Compilation

```bash
mvn clean package
```

This will create `target/blobutil-jar-with-dependencies.jar` which contains all dependencies.

## Usage

The utility now supports two modes:
- **Individual mode**: Detailed analysis for each point (default)
- **Summary mode**: CSV summary with timerange, point ID, total count, and unique count

## Command Syntax

```bash
java -jar target/blobutil-jar-with-dependencies.jar <dataDirectory> <id|csvFile> <timeFrom> <timeTo> [outputFile] [mode] [--no-files]
```

### Arguments:
- `dataDirectory` - Directory containing JSON files to query
- `id|csvFile` - Single point ID (number) OR path to CSV file containing point IDs
- `timeFrom` - Start time in ISO-8601 format (e.g., `2025-11-26T00:00:00Z`)
- `timeTo` - End time in ISO-8601 format
- `outputFile` - (Optional) Output file path. Auto-generated if not specified
- `mode` - (Optional) `individual` (default) or `summary`
- `--no-files` or `-nf` - (Optional) Exclude file names from summary output. Saves memory and processing time for large datasets.

## Examples

### 1. Single Point ID (Original Functionality)
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" 11280362 "2025-11-26T00:00:00Z" "2025-11-27T00:00:00Z"
```

### 2. Multiple Points from CSV - Individual Mode (Detailed Analysis)
First, create a CSV file `points.csv`:
```csv
PointID
11280362
11337808
10240564
```

Then run:
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" points.csv "2025-11-26T00:00:00Z" "2025-11-27T00:00:00Z" output.txt individual
```

### 3. Multiple Points from CSV - Summary Mode (CSV Summary with Files)
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" points.csv "2025-11-26T00:00:00Z" "2025-11-27T00:00:00Z" summary.csv summary
```

The summary output will be a CSV file with format:
```csv
TimeRange,Point ID,Total Count,Unique Count,Files
2025-11-26T00:00:00Z to 2025-11-27T00:00:00Z,11280362,255,255,"[IRIS_Data_26112025190036064.JSON,IRIS_Data_26112025200026447.JSON]"
2025-11-26T00:00:00Z to 2025-11-27T00:00:00Z,11337808,150,150,"[IRIS_Data_26112025190036064.JSON]"
...
```

The **Files** column contains an array of unique file names (in brackets) where the point was found.

### 4. Multiple Points from CSV - Summary Mode (CSV Summary without Files)
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" points.csv "2025-11-26T00:00:00Z" "2025-11-27T00:00:00Z" summary.csv summary --no-files
```

Or using the shorthand:
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\Users\devvm\Downloads\data" points.csv "2025-11-26T00:00:00Z" "2025-11-27T00:00:00Z" summary.csv summary -nf
```

The summary output will be a CSV file without the Files column:
```csv
TimeRange,Point ID,Total Count,Unique Count
2025-11-26T00:00:00Z to 2025-11-27T00:00:00Z,11280362,255,255
2025-11-26T00:00:00Z to 2025-11-27T00:00:00Z,11337808,150,150
...
```

**Note**: Using `--no-files` saves memory and processing time, especially useful for very large datasets (40,000+ points).

## CSV File Format

The CSV file can have:
- A header row (will be automatically skipped if non-numeric)
- One point ID per line in the first column
- Empty lines are ignored

Example `points.csv`:
```csv
PointID
11280362
11337808
11138561
10240564
```

Or without header:
```csv
11280362
11337808
11138561
10240564
```

## Output Files

- **Individual mode**: Text files with detailed analysis (`.txt` extension)
- **Summary mode**: CSV files with summary data (`.csv` extension)
- If output file is not specified, files are auto-generated with timestamps

## Memory Management

The utility has been optimized for large datasets (e.g., 40,000+ points):

- **Summary mode**: Uses streaming to process data without loading all objects into memory. Only counts and optionally file names are stored.
- **Summary mode with --no-files**: Even more memory-efficient as it skips collecting file names entirely.
- **Individual mode**: Processes points incrementally, clearing memory after each point.
- **Progress indicators**: Shows progress every 1,000 points processed.
- **Automatic garbage collection**: Suggests GC after every 5,000 points to help manage memory.

For very large datasets, **summary mode with --no-files is recommended** for maximum memory efficiency.


