package com.example.user.helpeachother.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        /*for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }*/
    }

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void clearItem() {
        ITEMS.clear();
    }

    public static DummyItem createDummyItem(int position,String dId,String uno,String taskcontent,String latitude,String longitude,String time,String deadline) {

            return new DummyItem(String.valueOf(position), dId,uno,taskcontent, makeDetails(position,uno,taskcontent),latitude,longitude,time,deadline);
    }

    private static String makeDetails(int position,String x,String taskcontent) {
        StringBuilder builder = new StringBuilder();
        //builder.append(" Released by: "+x);
        //builder.append("More details about the task:");
        builder.append("任 务 要 求 详 情：");
        builder.append("\n\n").append(taskcontent);
        /*builder.append("Details about Item: ").append(position).append(" Released by: "+x);
        builder.append("\nMore details information about task.");
        builder.append("\n").append(content);*/
        /*for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }*/
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String uno;
        public final String content; //uno+Details前16char
        public final String details; //TaskDetails
        public final String latitude;
        public final String longitude;
        public final String dId;
        public final String time;
        public final String deadline;

        public DummyItem(String id,String dId,String uno ,String content, String details,String latitude,String longitude,String time,String deadline) {
            this.id = id;
            this.uno = uno;
            this.content = content;
            this.details = details;
            this.latitude = latitude;
            this.longitude = longitude;
            this.dId = dId;
            this.time = time;
            this.deadline = deadline;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
