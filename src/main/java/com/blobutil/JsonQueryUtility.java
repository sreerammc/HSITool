package com.blobutil;

import com.blobutil.model.ComplexData;
import com.blobutil.model.DataObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JsonQueryUtility {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private static PrintWriter fileWriter = null;

    // Wrapper class to track filename with each object
    private static class ObjectWithFile {
        private final DataObject object;
        private final String filename;

        public ObjectWithFile(DataObject object, String filename) {
            this.object = object;
            this.filename = filename;
        }

        public DataObject getObject() {
            return object;
        }

        public String getFilename() {
            return filename;
        }
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            printUsage();
            System.exit(1);
        }

        String dataDirectory = args[0];
        String idOrCsvFile = args[1];
        String timeFromStr = args[2];
        String timeToStr = args[3];
        String outputFile = args.length > 4 ? args[4] : null;
        String mode = args.length > 5 ? args[5].toLowerCase() : "individual"; // "individual" or "summary"
        boolean includeFiles = true; // Default to including files
        
        // Check for --no-files flag in any position after mode
        for (int i = 6; i < args.length; i++) {
            if ("--no-files".equalsIgnoreCase(args[i]) || "-nf".equalsIgnoreCase(args[i])) {
                includeFiles = false;
                break;
            }
        }

        try {
            Instant timeFrom = Instant.parse(timeFromStr);
            Instant timeTo = Instant.parse(timeToStr);

            List<Long> pointIds;
            boolean isCsvInput = idOrCsvFile.toLowerCase().endsWith(".csv");

            if (isCsvInput) {
                // Read point IDs from CSV file
                pointIds = readPointIdsFromCsv(idOrCsvFile);
                if (pointIds.isEmpty()) {
                    System.err.println("Error: No valid point IDs found in CSV file: " + idOrCsvFile);
                    System.exit(1);
                }
            } else {
                // Single ID provided
                try {
                    pointIds = Collections.singletonList(Long.parseLong(idOrCsvFile));
                } catch (NumberFormatException e) {
                    System.err.println("Error: Invalid ID format. Provide either a number or a CSV file path.");
                    System.exit(1);
                    return;
                }
            }

            // Generate output filename if not provided
            if (outputFile == null || outputFile.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                        .withZone(ZoneId.systemDefault());
                String timestamp = formatter.format(Instant.now());
                if (mode.equals("summary")) {
                    outputFile = String.format("query_summary_%s.csv", timestamp);
                } else {
                    outputFile = String.format("query_result_%s_%s.txt", 
                        pointIds.size() == 1 ? pointIds.get(0) : "multi", timestamp);
                }
            }

            // Initialize file writer
            fileWriter = new PrintWriter(new FileWriter(outputFile, false));
            System.out.println("Output will be written to: " + outputFile + "\n");

            if (mode.equals("summary")) {
                // Summary mode: generate CSV output for all points
                generateSummaryReport(dataDirectory, pointIds, timeFrom, timeTo, includeFiles);
            } else {
                // Individual mode: detailed analysis for each point
                // Process incrementally to manage memory
                int processedCount = 0;
                for (Long pointId : pointIds) {
                    printLine("========================================");
                    printLine("Processing Point ID: " + pointId);
                    printLine("========================================");
                    printLine("");

                    List<ObjectWithFile> matchingObjects = queryJsonFiles(dataDirectory, pointId, timeFrom, timeTo);
                    analyzeResults(matchingObjects, pointId, timeFrom, timeTo);
                    printLine("");
                    
                    // Clear references to help GC
                    matchingObjects.clear();
                    processedCount++;
                    
                    // Progress indicator for large datasets
                    if (processedCount % 1000 == 0) {
                        System.out.println("Processed " + processedCount + " of " + pointIds.size() + " points...");
                    }
                    
                    // Suggest GC after processing batches
                    if (processedCount % 5000 == 0) {
                        System.gc();
                    }
                }
            }

            printLine("\nResults saved to: " + outputFile);

        } catch (DateTimeParseException e) {
            System.err.println("Error parsing time: " + e.getMessage());
            System.err.println("Time format should be ISO-8601 (e.g., 2025-11-19T14:00:00.000Z)");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: JsonQueryUtility <dataDirectory> <id|csvFile> <timeFrom> <timeTo> [outputFile] [mode] [--no-files]");
        System.out.println("");
        System.out.println("Arguments:");
        System.out.println("  dataDirectory  - Directory containing JSON files to query");
        System.out.println("  id|csvFile     - Single point ID (number) or path to CSV file containing point IDs");
        System.out.println("  timeFrom       - Start time in ISO-8601 format (e.g., 2025-11-19T14:00:00.000Z)");
        System.out.println("  timeTo         - End time in ISO-8601 format");
        System.out.println("  outputFile     - (Optional) Output file path. Auto-generated if not specified.");
        System.out.println("  mode           - (Optional) Output mode: 'individual' (default) or 'summary'");
        System.out.println("  --no-files     - (Optional) Exclude file names from summary output. Use -nf as shorthand.");
        System.out.println("");
        System.out.println("Examples:");
        System.out.println("  Single ID, individual mode:");
        System.out.println("    JsonQueryUtility \"C:\\data\" 11337808 \"2025-11-19T14:00:00.000Z\" \"2025-11-19T15:30:00.000Z\"");
        System.out.println("");
        System.out.println("  CSV file, individual mode (detailed analysis per point):");
        System.out.println("    JsonQueryUtility \"C:\\data\" points.csv \"2025-11-19T14:00:00.000Z\" \"2025-11-19T15:30:00.000Z\" output.txt individual");
        System.out.println("");
        System.out.println("  CSV file, summary mode (CSV summary with files):");
        System.out.println("    JsonQueryUtility \"C:\\data\" points.csv \"2025-11-19T14:00:00.000Z\" \"2025-11-19T15:30:00.000Z\" summary.csv summary");
        System.out.println("");
        System.out.println("  CSV file, summary mode (CSV summary without files):");
        System.out.println("    JsonQueryUtility \"C:\\data\" points.csv \"2025-11-19T14:00:00.000Z\" \"2025-11-19T15:30:00.000Z\" summary.csv summary --no-files");
        System.out.println("");
        System.out.println("CSV Format:");
        System.out.println("  CSV file should contain point IDs, one per line. First line can be a header (will be skipped).");
        System.out.println("  Example CSV content:");
        System.out.println("    PointID");
        System.out.println("    11280362");
        System.out.println("    11337808");
    }

    private static void printLine(String line) {
        System.out.println(line);
        if (fileWriter != null) {
            fileWriter.println(line);
            fileWriter.flush();
        }
    }

    private static void printError(String line) {
        System.err.println(line);
        if (fileWriter != null) {
            fileWriter.println("ERROR: " + line);
            fileWriter.flush();
        }
    }

    /**
     * Reads point IDs from a CSV file.
     * Supports CSV files with or without headers.
     * Each line should contain a point ID (numeric value).
     */
    private static List<Long> readPointIdsFromCsv(String csvFilePath) throws IOException {
        List<Long> pointIds = new ArrayList<>();
        File csvFile = new File(csvFilePath);
        
        if (!csvFile.exists()) {
            throw new IOException("CSV file does not exist: " + csvFilePath);
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                // Try to parse the line - could be header or data
                String[] parts = line.split(",");
                String firstColumn = parts[0].trim();
                
                // Skip header if it's not numeric
                if (isFirstLine) {
                    try {
                        Long.parseLong(firstColumn);
                        // It's numeric, so it's data, not header
                        pointIds.add(Long.parseLong(firstColumn));
                    } catch (NumberFormatException e) {
                        // Likely a header, skip it
                    }
                    isFirstLine = false;
                } else {
                    // Try to parse as point ID
                    try {
                        pointIds.add(Long.parseLong(firstColumn));
                    } catch (NumberFormatException e) {
                        // Skip non-numeric lines
                        System.err.println("Warning: Skipping non-numeric line in CSV: " + line);
                    }
                }
            }
        }
        
        return pointIds;
    }

    /**
     * Generates a summary CSV report for multiple point IDs.
     * Memory-optimized: Uses streaming to avoid loading all objects into memory.
     * Output format: TimeRange, Point ID, Total Count, Unique Count, [Files] (optional)
     */
    private static void generateSummaryReport(String dataDirectory, List<Long> pointIds, 
                                             Instant timeFrom, Instant timeTo, boolean includeFiles) throws IOException {
        // Print header information
        printLine("Querying JSON files...");
        printLine("Directory: " + dataDirectory);
        printLine("Point IDs: " + pointIds.size() + " point(s)");
        printLine("Time From: " + timeFrom);
        printLine("Time To: " + timeTo);
        printLine("Mode: Summary");
        printLine("Include Files: " + includeFiles);
        printLine("========================================");
        printLine("");

        // CSV Header
        String timeRange = timeFrom + " to " + timeTo;
        if (includeFiles) {
            printLine("TimeRange,Point ID,Total Count,Unique Count,Files");
        } else {
            printLine("TimeRange,Point ID,Total Count,Unique Count");
        }

        // Process each point ID with memory-efficient streaming
        int processedCount = 0;
        for (Long pointId : pointIds) {
            // Use memory-efficient query that only collects counts and optionally file names
            PointSummary summary = queryJsonFilesSummary(dataDirectory, pointId, timeFrom, timeTo, includeFiles);
            
            // Output CSV row immediately and flush
            if (includeFiles) {
                String filesArray = formatFilesArray(summary.getFiles());
                printLine(String.format("%s,%d,%d,%d,\"%s\"", timeRange, pointId, 
                        summary.getTotalCount(), summary.getUniqueCount(), filesArray));
            } else {
                printLine(String.format("%s,%d,%d,%d", timeRange, pointId, 
                        summary.getTotalCount(), summary.getUniqueCount()));
            }
            
            // Progress indicator for large datasets
            processedCount++;
            if (processedCount % 1000 == 0) {
                System.out.println("Processed " + processedCount + " of " + pointIds.size() + " points...");
            }
            
            // Suggest GC after processing batches (helps with memory management)
            if (processedCount % 5000 == 0) {
                System.gc();
            }
        }
    }
    
    /**
     * Memory-efficient summary class that only stores counts and file names, not all objects.
     */
    private static class PointSummary {
        private final int totalCount;
        private final int uniqueCount;
        private final List<String> files;
        
        public PointSummary(int totalCount, int uniqueCount, List<String> files) {
            this.totalCount = totalCount;
            this.uniqueCount = uniqueCount;
            this.files = files;
        }
        
        public int getTotalCount() { return totalCount; }
        public int getUniqueCount() { return uniqueCount; }
        public List<String> getFiles() { return files; }
    }

    /**
     * Formats a list of file names as an array string.
     * Format: [file1,file2,file3]
     */
    private static String formatFilesArray(List<String> files) {
        if (files.isEmpty()) {
            return "[]";
        }
        return "[" + String.join(",", files) + "]";
    }

    /**
     * Memory-efficient query that only collects summary statistics without storing all objects.
     * Used for summary mode to handle large datasets (e.g., 40,000 points).
     */
    private static PointSummary queryJsonFilesSummary(String dataDirectory, Long targetId, 
                                                      Instant timeFrom, Instant timeTo, boolean includeFiles) throws IOException {
        File dir = new File(dataDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Directory does not exist: " + dataDirectory);
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null || files.length == 0) {
            throw new IOException("No JSON files found in directory: " + dataDirectory);
        }

        // Use sets to track unique objects and optionally files without storing all data
        Set<DataObject> uniqueObjects = new HashSet<>();
        Set<String> uniqueFiles = includeFiles ? new HashSet<>() : null;
        AtomicInteger totalCount = new AtomicInteger(0);

        for (File file : files) {
            try {
                ComplexData complexData = objectMapper.readValue(file, ComplexData.class);
                
                if (complexData.getExportedData() != null && 
                    complexData.getExportedData().getObjects() != null) {
                    
                    // Stream and filter without collecting all objects
                    complexData.getExportedData().getObjects().stream()
                            .filter(obj -> obj.getId() != null && obj.getId().equals(targetId))
                            .filter(obj -> obj.getTime() != null && 
                                         !obj.getTime().isBefore(timeFrom) && 
                                         !obj.getTime().isAfter(timeTo))
                            .forEach(obj -> {
                                totalCount.incrementAndGet();
                                uniqueObjects.add(obj); // Set automatically handles uniqueness
                                if (includeFiles && uniqueFiles != null) {
                                    uniqueFiles.add(file.getName());
                                }
                            });
                }
            } catch (IOException e) {
                String errorMsg = "Error reading file " + file.getName() + ": " + e.getMessage();
                printError(errorMsg);
            }
        }

        // Sort file names for consistent output (only if files are included)
        List<String> sortedFiles = new ArrayList<>();
        if (includeFiles && uniqueFiles != null) {
            sortedFiles = new ArrayList<>(uniqueFiles);
            Collections.sort(sortedFiles);
        }

        return new PointSummary(totalCount.get(), uniqueObjects.size(), sortedFiles);
    }

    /**
     * Original query method that returns all matching objects.
     * Used for individual mode where detailed analysis is needed.
     */
    private static List<ObjectWithFile> queryJsonFiles(String dataDirectory, Long targetId, 
                                                       Instant timeFrom, Instant timeTo) throws IOException {
        File dir = new File(dataDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Directory does not exist: " + dataDirectory);
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null || files.length == 0) {
            throw new IOException("No JSON files found in directory: " + dataDirectory);
        }

        List<ObjectWithFile> matchingObjects = new ArrayList<>();

        for (File file : files) {
            try {
                ComplexData complexData = objectMapper.readValue(file, ComplexData.class);
                
                if (complexData.getExportedData() != null && 
                    complexData.getExportedData().getObjects() != null) {
                    
                    List<DataObject> filtered = complexData.getExportedData().getObjects().stream()
                            .filter(obj -> obj.getId() != null && obj.getId().equals(targetId))
                            .filter(obj -> obj.getTime() != null && 
                                         !obj.getTime().isBefore(timeFrom) && 
                                         !obj.getTime().isAfter(timeTo))
                            .collect(Collectors.toList());
                    
                    // Wrap each object with its filename
                    for (DataObject obj : filtered) {
                        matchingObjects.add(new ObjectWithFile(obj, file.getName()));
                    }
                }
            } catch (IOException e) {
                String errorMsg = "Error reading file " + file.getName() + ": " + e.getMessage();
                printError(errorMsg);
            }
        }

        return matchingObjects;
    }

    private static void analyzeResults(List<ObjectWithFile> objectsWithFiles, Long targetId, 
                                       Instant timeFrom, Instant timeTo) {
        if (objectsWithFiles.isEmpty()) {
            printLine("No matching objects found.");
            return;
        }

        // Group by all fields to identify duplicates
        Map<DataObject, List<ObjectWithFile>> grouped = objectsWithFiles.stream()
                .collect(Collectors.groupingBy(ObjectWithFile::getObject));

        List<ObjectWithFile> uniqueObjects = new ArrayList<>();
        List<List<ObjectWithFile>> duplicateGroups = new ArrayList<>();

        for (Map.Entry<DataObject, List<ObjectWithFile>> entry : grouped.entrySet()) {
            if (entry.getValue().size() == 1) {
                uniqueObjects.add(entry.getValue().get(0));
            } else {
                duplicateGroups.add(entry.getValue());
            }
        }

        // Calculate total duplicate objects
        int totalDuplicateObjects = duplicateGroups.stream()
                .mapToInt(List::size)
                .sum();

        // Print summary at the top
        printLine("========================================");
        printLine("SUMMARY");
        printLine("========================================");
        printLine("ID: " + targetId);
        printLine("Time From: " + timeFrom);
        printLine("Time To: " + timeTo);
        printLine("Total objects found: " + objectsWithFiles.size());
        printLine("Unique objects: " + uniqueObjects.size());
        printLine("Duplicate objects: " + totalDuplicateObjects + " (in " + duplicateGroups.size() + " duplicate group(s))");
        printLine("========================================");
        printLine("");

        // Print all matching objects with filenames
        printLine("========================================");
        printLine("ALL MATCHING OBJECTS");
        printLine("========================================");
        printLine("Total objects found: " + objectsWithFiles.size());
        printLine("");
        
        for (int i = 0; i < objectsWithFiles.size(); i++) {
            ObjectWithFile objWithFile = objectsWithFiles.get(i);
            printLine((i + 1) + ". " + formatObjectWithFile(objWithFile));
        }
        printLine("");

        // Print duplicate records
        if (!duplicateGroups.isEmpty()) {
            printLine("DUPLICATE RECORDS (" + duplicateGroups.size() + " group(s)):");
            printLine("----------------------------------------");
            for (int i = 0; i < duplicateGroups.size(); i++) {
                List<ObjectWithFile> group = duplicateGroups.get(i);
                printLine("Duplicate Group " + (i + 1) + " (appears " + group.size() + " times):");
                DataObject sample = group.get(0).getObject();
                printLine("  " + formatObject(sample));
                printLine("  Found in files:");
                for (int j = 0; j < group.size(); j++) {
                    printLine("    - " + group.get(j).getFilename());
                }
                printLine("");
            }
        } else {
            printLine("No duplicate records found.");
            printLine("");
        }
    }

    private static String formatObject(DataObject obj) {
        return String.format(
            "Id=%d, Fullname='%s', Time=%s, Value=%s, Reason=%d, Quality='%s'",
            obj.getId(),
            obj.getFullname(),
            obj.getTime(),
            obj.getValue(),
            obj.getReason(),
            obj.getQuality()
        );
    }

    private static String formatObjectWithFile(ObjectWithFile objWithFile) {
        return String.format(
            "%s [File: %s]",
            formatObject(objWithFile.getObject()),
            objWithFile.getFilename()
        );
    }
}


