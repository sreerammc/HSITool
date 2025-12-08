# CPU Usage Optimization Guide

## For Intel Xeon Platinum 8168 (24-28 cores @ 2.70GHz)

Your CPU has many cores, which can cause very high CPU usage when using all cores. Here are optimized configurations:

### Recommended Settings

#### Option 1: Balanced Performance (Recommended)
```bash
--threads=4
```
- Uses 4 threads
- ~15-20% CPU usage
- Excellent performance
- Best balance for most workloads

#### Option 2: Moderate Performance
```bash
--threads=8
```
- Uses 8 threads
- ~30-40% CPU usage
- Very good performance
- Good for time-sensitive tasks

#### Option 3: Low CPU Usage
```bash
--threads=2
```
or
```bash
--no-parallel
```
- Uses 1-2 threads
- ~5-10% CPU usage
- Slower but CPU-friendly
- Good for background processing

### Why Not Use All Cores?

With 24-28 cores, using all of them:
- Can cause 80-100% CPU usage
- May slow down other applications
- Diminishing returns (4-8 threads often sufficient)
- Higher power consumption and heat

### Performance Comparison

For typical workloads with many JSON files:

| Threads | CPU Usage | Speed | Use Case |
|---------|-----------|-------|----------|
| 1 (sequential) | ~5% | 1x | Background, low priority |
| 2 | ~10% | ~1.8x | Light processing |
| 4 | ~20% | ~3.5x | **Recommended** |
| 8 | ~40% | ~6x | High performance |
| 16 | ~70% | ~10x | Maximum speed |
| All cores (24+) | ~95% | ~12x | Only if needed |

### Example Commands

**Best for your Xeon CPU:**
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\data" points.csv "2025-11-26T00:00:00Z" "2025-11-27T00:00:00Z" summary.csv summary --threads=4 --no-files
```

**If you need faster processing:**
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\data" points.csv "2025-11-26T00:00:00Z" "2025-11-27T00:00:00Z" summary.csv summary --threads=8
```

**If CPU usage is still too high:**
```bash
java -jar target/blobutil-jar-with-dependencies.jar "C:\data" points.csv "2025-11-26T00:00:00Z" "2025-11-27T00:00:00Z" summary.csv summary --threads=2
```

### Monitoring CPU Usage

The utility will show thread count at startup:
```
Parallel processing: ENABLED (4 threads)
CPU cores available: 24
```

You can monitor CPU usage in Task Manager (Windows) or `top` (Linux) to find your optimal thread count.


