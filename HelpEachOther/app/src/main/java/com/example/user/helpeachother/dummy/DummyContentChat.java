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
public class DummyContentChat {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public static final int RIGHT = 0,LEFT = 1;

    private static final int COUNT = 25;

    static {
        // Add some sample items.

    }

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void clearItem() {
        ITEMS.clear();
    }

    public static DummyItem createDummyItem(int position,String uno,String text,int type,String time){
        return new DummyItem(String.valueOf(position), uno,text,type,time);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String uno; //uno+Details前16char
        public final String text; //TaskDetails
        public final int type;
        public final String time;

        public DummyItem(String id, String uno, String text,int type,String time) {
            this.id = id;
            this.uno = uno;
            this.text = text;
            this.type = type;
            this.time = time;
        }

        @Override
        public String toString() {
            return text;
        }
        public int getType() { return type; }
    }
}
