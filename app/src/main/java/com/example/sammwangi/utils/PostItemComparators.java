package com.example.sammwangi.utils;

import com.example.sammwangi.DAOs.PostItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class PostItemComparators {
    // Custom comparator for sorting by datePosted
    public static final Comparator<PostItem> DATE_POSTED_COMPARATOR = new Comparator<PostItem>() {
        @Override
        public int compare(PostItem postItem1, PostItem postItem2) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date1 = sdf.parse(postItem1.getDatePosted());
                Date date2 = sdf.parse(postItem2.getDatePosted());
                return date2.compareTo(date1); // Sorting in descending order (newest to oldest)
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    };

    // Custom comparator for sorting by postAmount
    public static final Comparator<PostItem> POST_AMOUNT_COMPARATOR = new Comparator<PostItem>() {
        @Override
        public int compare(PostItem postItem1, PostItem postItem2) {
            // Assuming postAmount is a numeric value represented as a string
            Double amount1 = Double.valueOf(postItem1.getPostAmount());
            Double amount2 = Double.valueOf(postItem2.getPostAmount());
            return amount2.compareTo(amount1); // Sorting in descending order (highest to lowest amount)
        }
    };

    // Custom comparator for sorting by fullName
    public static final Comparator<PostItem> FULL_NAME_COMPARATOR = new Comparator<PostItem>() {
        @Override
        public int compare(PostItem postItem1, PostItem postItem2) {
//            return postItem1.getFullName().compareToIgnoreCase(postItem2.getFullName());
            return 0;
        }
    };
}

