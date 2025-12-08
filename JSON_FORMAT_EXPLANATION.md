# JSON Format Error Explanation

## The Error

```
Cannot deserialize value of type `com.blobutil.model.ComplexData` from Array value (token `JsonToken.START_ARRAY`)
```

## Root Cause

The utility was originally designed to read JSON files in this format:

### Expected Format (ComplexData)
```json
{
  "_name": "",
  "_model": "ComplexData",
  "_timestamp": 1763564604591,
  "ExportedData": {
    "Header": {
      "SystemName": "IRIS",
      "StartDate": "2025-10-30T13:00:00.000Z",
      "EndDate": "2025-10-30T14:15:00.000Z"
    },
    "Objects": [
      {
        "Id": 11306749,
        "Fullname": "...",
        "Time": "2025-10-30T13:00:00.000Z",
        "Value": 0,
        "Reason": 3,
        "Quality": "Good"
      }
    ]
  }
}
```

### Actual Format in Some Files (Direct Array)
```json
[
  {
    "Id": 11306749,
    "Fullname": "...",
    "Time": "2025-10-30T13:00:00.000Z",
    "Value": 0,
    "Reason": 3,
    "Quality": "Good"
  },
  {
    "Id": 11306749,
    "Fullname": "...",
    "Time": "2025-10-30T13:05:00.000Z",
    "Value": 0,
    "Reason": 3,
    "Quality": "Good"
  }
]
```

## Why It Failed

1. **Code expectation**: The code tried to parse every JSON file as a `ComplexData` object
2. **Reality**: Some files are just arrays of `DataObject` directly
3. **Jackson error**: Jackson couldn't convert an array `[...]` into a `ComplexData` object `{...}`
4. **Result**: Deserialization error

## The Fix

The utility now:
1. **First tries** to parse as `ComplexData` (standard format)
2. **If that fails**, tries to parse as a direct array of `DataObject`
3. **Handles both formats** automatically

## Files Affected

The error occurred in files like:
- `IRIS_Data_30102025153023183.JSON`
- `IRIS_Data_30102025174810830.JSON`
- `IRIS_Data_30102025153126196.JSON`
- `IRIS_Data_30102025174831208.JSON`

These files contain direct arrays instead of the wrapped `ComplexData` structure.

## Solution Status

✅ **Fixed**: The code now supports both formats automatically. After rebuilding, these errors should no longer occur.

