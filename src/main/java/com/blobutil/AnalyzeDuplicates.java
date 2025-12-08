package com.blobutil;

import java.util.*;

public class AnalyzeDuplicates {
    public static void main(String[] args) {
        // Array from user input
        long[] pointArray = {
            10620251, 10697797, 10703407, 10711437, 10712551, 10712571, 10712581, 10715125, 10717908, 10725160, 10725169, 10728493, 
            10728718, 10731289, 10731352, 10733687, 10733716, 10735398, 10737985, 10740259, 10740700, 10752338, 10759190, 10769506, 
            10807933, 10814757, 10814955, 10827606, 10849557, 10864455, 10865142, 10881665, 10881674, 10935943, 10966664, 10968383, 
            10983421, 10984339, 10985461, 11000846, 11000963, 11001188, 11001197, 11010124, 11010316, 11010420, 11019760, 11032246, 
            11040414, 11057076, 11064336, 11067682, 11072110, 11072222, 11072582, 11072798, 11080650, 11093699, 11125707, 11138561, 
            11139676, 11146658, 11149820, 11163275, 11165560, 11165763, 11167389, 11168792, 11169132, 11170257, 11255850, 11619431, 
            11621162, 11627113, 11643426, 11643438, 11643607, 11649886, 11676674, 11697075, 11697091, 11721105, 11734911, 11735160, 
            11743534, 11743539, 11748521, 11756514, 11758299, 11758447, 11763589, 11791828, 11791829, 11793299, 11798243, 11798395, 
            11802023, 11802024, 11816331, 11817655, 11817656, 11918789, 11918794, 11935639, 11974825, 11974826, 12013135, 12020238, 
            12020239, 12045398, 12074902, 12142259, 12142261, 12146010, 12163428, 12164589, 12164594, 12206773, 12207278, 12257174, 
            12276036, 10240149, 10250857, 10693383, 10703407, 10704622, 10712437, 10759190, 10864455, 10872383, 10967735, 10995909, 
            11060247, 11076007, 11087764, 11098942, 11125707, 11134670, 11138561, 11157387, 11157396, 11161830, 11170049, 11817656, 
            11821667, 11821668, 10240564, 10704614, 11087764, 11098654, 11125707, 11138561, 11140304, 11167353, 11280354, 11280357, 
            11280358, 11280362, 11280380, 11280390, 11280392, 11280393, 11280398, 11293455, 11293457, 11293459, 11293463, 11293478, 
            11783157, 11786535, 11790141, 11817656, 11819892, 11819893, 11979200, 12101228, 12257578, 12257829, 12272315, 12382790, 
            12395787, 12395919
        };

        // Count occurrences of each point ID
        Map<Long, Integer> countMap = new HashMap<>();
        for (long pointId : pointArray) {
            countMap.put(pointId, countMap.getOrDefault(pointId, 0) + 1);
        }

        // Find duplicates (IDs that appear more than once)
        Map<Long, Integer> duplicates = new TreeMap<>();
        for (Map.Entry<Long, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.put(entry.getKey(), entry.getValue());
            }
        }

        int totalCount = pointArray.length;
        int uniqueCount = countMap.size();
        int duplicateCount = totalCount - uniqueCount;
        int duplicateIdsCount = duplicates.size();

        System.out.println("========================================");
        System.out.println("DUPLICATE ANALYSIS");
        System.out.println("========================================");
        System.out.println("Total point IDs in array: " + totalCount);
        System.out.println("Unique point IDs: " + uniqueCount);
        System.out.println("Total duplicate occurrences: " + duplicateCount);
        System.out.println("Number of IDs that are duplicated: " + duplicateIdsCount);
        System.out.println("========================================");

        if (duplicates.isEmpty()) {
            System.out.println("\nNo duplicates found!");
        } else {
            System.out.println("\nDuplicate Point IDs (ID -> Count):");
            System.out.println("----------------------------------------");
            for (Map.Entry<Long, Integer> entry : duplicates.entrySet()) {
                System.out.println(String.format("  %d appears %d time(s)", entry.getKey(), entry.getValue()));
            }
            System.out.println("----------------------------------------");
            System.out.println("\nSummary:");
            System.out.println("  " + duplicateIdsCount + " unique IDs are duplicated");
            System.out.println("  " + duplicateCount + " total duplicate occurrences");
        }
    }
}


