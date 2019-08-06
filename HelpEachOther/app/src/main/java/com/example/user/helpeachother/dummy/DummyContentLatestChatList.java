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
public class DummyContentLatestChatList {

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

    public static DummyItem createDummyItem(int position,String uano,String ubno,String text,String realBno,String time) {
        //if(ubno.length()+ubname.length()>24  )
            return new DummyItem(String.valueOf(position), uano,ubno,text,realBno,time);
        //else
            //return new DummyItem(String.valueOf(position), ubno, ubname,realBno);
    }

    private static String makeDetails(int position,String ubno,String ubname,String time) {
        StringBuilder builder = new StringBuilder();
        //builder.append(" Released by: "+x);
        builder.append(ubno).append(ubname);
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
        public final String uano; //uno+Details前16char
        public final String ubno; //TaskDetails
        public final String content;
        public  String realbno;
        public final String time;

        public DummyItem(String id, String uano, String ubno,String text,String realBno,String time) {
            this.id = id;
            this.uano = uano;
            this.ubno = ubno;
            this.content = text;
            this.realbno = realBno;
            this.time = time;
        }

        @Override
        public String toString() {
            return content;
        }

    }
}
