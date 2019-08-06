package com.example.user.helpeachother.dummy;

import android.graphics.Bitmap;

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
public class DummyContentFriendList {

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

    public static DummyItem createDummyItem(int position, String ubno, String ubname) {
        //if(ubno.length()+ubname.length()>24  )
            return new DummyItem(String.valueOf(position), ubno,ubname);
        //else
            //return new DummyItem(String.valueOf(position), ubno, ubname);
    }

    private static String makeDetails(int position,String ubno,String ubname) {
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
        public final String ubno; //uno+Details前16char
        public final String ubname; //TaskDetails
        public final String content;
        //public final Bitmap bitmap;

        public DummyItem(String id, String ubno, String ubname) {
            this.id = id;
            this.ubno = ubno;
            this.ubname = ubname;
            this.content = ubno+":"+ubname;
            //this.bitmap = bitmap;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
