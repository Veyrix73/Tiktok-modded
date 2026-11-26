package com.veyrix.tiktokplus.prototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "local_prototype";
    private static final int REQUEST_BANNER = 417;

    private static final int INK = Color.rgb(40, 49, 59);
    private static final int MUTED = Color.rgb(111, 122, 132);
    private static final int SURFACE = Color.WHITE;
    private static final int PAGE = Color.rgb(246, 249, 249);
    private static final int TEAL = Color.rgb(18, 175, 166);
    private static final int CORAL = Color.rgb(255, 112, 112);
    private static final int GOLD = Color.rgb(255, 194, 74);

    private SharedPreferences prefs;
    private LinearLayout pageContent;
    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(PAGE);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18), dp(12), dp(18), dp(10));
        header.setBackgroundColor(Color.WHITE);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView mark = new TextView(this);
        mark.setText("T+");
        mark.setTextColor(Color.WHITE);
        mark.setTextSize(18);
        mark.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(round(TEAL, 14));
        header.addView(mark, new LinearLayout.LayoutParams(dp(44), dp(44)));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setPadding(dp(12), 0, 0, 0);

        TextView brand = new TextView(this);
        brand.setText("TikTok+ Test");
        brand.setTextSize(19);
        brand.setTextColor(INK);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("INDEPENDENT CONCEPT • LOCAL ONLY");
        subtitle.setTextSize(9);
        subtitle.setTextColor(MUTED);
        subtitle.setLetterSpacing(0.08f);

        titles.addView(brand);
        titles.addView(subtitle);
        header.addView(titles, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        pageContent = new LinearLayout(this);
        pageContent.setOrientation(LinearLayout.VERTICAL);
        pageContent.setPadding(dp(14), dp(14), dp(14), dp(18));
        scroll.addView(pageContent);
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1
        ));

        root.addView(buildBottomNavigation());
        setContentView(root);

        showHome();
    }

    private LinearLayout buildBottomNavigation() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(6), dp(7), dp(6), dp(7));
        nav.setBackgroundColor(Color.WHITE);

        addNavButton(nav, "Feed", this::showHome);
        addNavButton(nav, "Search", this::showSearch);
        addNavButton(nav, "Create", this::showCreate);
        addNavButton(nav, "Profile", this::showProfile);
        return nav;
    }

    private void addNavButton(LinearLayout nav, String label, Runnable action) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextAllCaps(false);
        button.setTextSize(12);
        button.setTextColor(TEAL);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(dp(4), dp(5), dp(4), dp(5));
        button.setOnClickListener(v -> action.run());
        nav.addView(button, new LinearLayout.LayoutParams(0, dp(46), 1));
    }

    private void showHome() {
        pageContent.removeAllViews();
        addSectionTitle("For You");
        addNote("Sample posts are stored locally on this device. Nothing is sent to a server.");

        for (Post post : getPosts()) {
            pageContent.addView(buildPostCard(post), margin(0, 0, 0, 14));
        }
    }

    private void showSearch() {
        pageContent.removeAllViews();
        addSectionTitle("Discover");

        EditText query = new EditText(this);
        query.setSingleLine(true);
        query.setHint("Search local sample posts");
        query.setTextSize(15);
        query.setPadding(dp(14), dp(9), dp(14), dp(9));
        query.setBackground(round(Color.WHITE, 14));
        pageContent.addView(query, margin(0, 0, 0, 10));

        Button search = primaryButton("Search");
        pageContent.addView(search, margin(0, 0, 0, 16));

        LinearLayout results = new LinearLayout(this);
        results.setOrientation(LinearLayout.VERTICAL);
        pageContent.addView(results);

        Runnable filter = () -> {
            String term = query.getText().toString().trim().toLowerCase(Locale.ROOT);
            results.removeAllViews();
            int count = 0;
            for (Post post : getPosts()) {
                String haystack = (post.creator + " " + post.caption + " " + post.mediaLabel)
                        .toLowerCase(Locale.ROOT);
                if (term.isEmpty() || haystack.contains(term)) {
                    results.addView(buildPostCard(post), margin(0, 0, 0, 14));
                    count++;
                }
            }
            if (count == 0) addNoteTo(results, "No matching local posts.");
        };
        search.setOnClickListener(v -> filter.run());
        filter.run();
    }

    private void showCreate() {
        pageContent.removeAllViews();
        addSectionTitle("Create a post");
        addNote("This creates a text-and-cover demo card in your local prototype feed.");

        EditText caption = new EditText(this);
        caption.setHint("Write a caption...");
        caption.setTextSize(16);
        caption.setMinLines(3);
        caption.setGravity(Gravity.TOP | Gravity.START);
        caption.setPadding(dp(14), dp(12), dp(14), dp(12));
        caption.setBackground(round(Color.WHITE, 14));
        pageContent.addView(caption, margin(0, 0, 0, 12));

        Button publish = primaryButton("Add to my local feed");
        pageContent.addView(publish, margin(0, 0, 0, 10));
        publish.setOnClickListener(v -> {
            String text = caption.getText().toString().trim();
            if (text.isEmpty()) {
                caption.setError("Write something first");
                return;
            }
            saveCustomPost(text);
            Toast.makeText(this, "Added to this device's demo feed", Toast.LENGTH_SHORT).show();
            showHome();
        });
    }

    private void showProfile() {
        pageContent.removeAllViews();
        addSectionTitle("Your profile");

        FrameLayout bannerFrame = new FrameLayout(this);
        bannerFrame.setBackground(round(Color.rgb(220, 245, 240), 18));
        ImageView banner = new ImageView(this);
        banner.setScaleType(ImageView.ScaleType.CENTER_CROP);
        banner.setBackground(round(Color.rgb(220, 245, 240), 18));
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(150)
        );
        bannerFrame.addView(banner, imageParams);

        String savedUri = prefs.getString("profile_banner_uri", "");
        if (!savedUri.isEmpty()) {
            try {
                banner.setImageURI(Uri.parse(savedUri));
            } catch (Exception ignored) {
                prefs.edit().remove("profile_banner_uri").apply();
            }
        } else {
            TextView placeholder = new TextView(this);
            placeholder.setText("YOUR PROFILE BANNER");
            placeholder.setTextSize(12);
            placeholder.setLetterSpacing(0.12f);
            placeholder.setTextColor(TEAL);
            placeholder.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            placeholder.setGravity(Gravity.CENTER);
            bannerFrame.addView(placeholder, imageParams);
        }
        pageContent.addView(bannerFrame, margin(0, 0, 0, 12));

        Button choose = primaryButton("Choose banner from gallery");
        pageContent.addView(choose, margin(0, 0, 0, 8));
        choose.setOnClickListener(v -> chooseBanner());

        Button clear = new Button(this);
        clear.setText("Remove banner");
        clear.setTextAllCaps(false);
        clear.setTextColor(CORAL);
        clear.setBackgroundColor(Color.TRANSPARENT);
        pageContent.addView(clear, margin(0, 0, 0, 10));
        clear.setOnClickListener(v -> {
            prefs.edit().remove("profile_banner_uri").apply();
            showProfile();
        });

        addNote("The banner is displayed and saved on this device only. It is not uploaded or shared.");
        addNote("Likes, comments and created posts in this prototype are also local demo data.");
    }

    private void chooseBanner() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivityForResult(intent, REQUEST_BANNER);
        } catch (Exception e) {
            Toast.makeText(this, "No image picker is available on this device.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_BANNER || resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }
        Uri uri = data.getData();
        try {
            getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException ignored) {
            // Some providers return a readable URI without persistable permission.
        }
        prefs.edit().putString("profile_banner_uri", uri.toString()).apply();
        showProfile();
    }

    private LinearLayout buildPostCard(Post post) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.setBackground(round(SURFACE, 18));
        card.setElevation(dp(2));

        TextView creator = new TextView(this);
        creator.setText(post.creator + "   •   LOCAL SAMPLE");
        creator.setTextColor(INK);
        creator.setTextSize(13);
        creator.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        card.addView(creator, margin(0, 0, 0, 10));

        FrameLayout media = new FrameLayout(this);
        media.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{post.color, lighten(post.color)}
        ));
        TextView mediaLabel = new TextView(this);
        mediaLabel.setText(post.mediaLabel);
        mediaLabel.setTextColor(Color.WHITE);
        mediaLabel.setTextSize(19);
        mediaLabel.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        mediaLabel.setGravity(Gravity.CENTER);
        mediaLabel.setPadding(dp(16), dp(16), dp(16), dp(16));
        media.addView(mediaLabel, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(200)
        ));
        card.addView(media, margin(0, 0, 0, 10));

        TextView caption = new TextView(this);
        caption.setText(post.caption);
        caption.setTextColor(INK);
        caption.setTextSize(15);
        card.addView(caption, margin(0, 0, 0, 8));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button like = actionButton((prefs.getBoolean("liked_" + post.id, false) ? "♥ " : "♡ ")
                + (post.baseLikes + (prefs.getBoolean("liked_" + post.id, false) ? 1 : 0)));
        like.setTextColor(prefs.getBoolean("liked_" + post.id, false) ? CORAL : INK);
        like.setOnClickListener(v -> {
            boolean liked = prefs.getBoolean("liked_" + post.id, false);
            prefs.edit().putBoolean("liked_" + post.id, !liked).apply();
            like.setText((!liked ? "♥ " : "♡ ")
                    + (post.baseLikes + (!liked ? 1 : 0)));
            like.setTextColor(!liked ? CORAL : INK);
        });

        Button comment = actionButton("Comment (" + prefs.getInt("comments_" + post.id, post.comments) + ")");
        comment.setOnClickListener(v -> showCommentDialog(post, comment));

        Button share = actionButton("Share");
        share.setOnClickListener(v -> {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_TEXT, post.caption + " — shared from an independent local prototype");
            startActivity(Intent.createChooser(send, "Share demo post"));
        });

        actions.addView(like, new LinearLayout.LayoutParams(0, dp(42), 1));
        actions.addView(comment, new LinearLayout.LayoutParams(0, dp(42), 1));
        actions.addView(share, new LinearLayout.LayoutParams(0, dp(42), 1));
        card.addView(actions);

        return card;
    }

    private void showCommentDialog(Post post, Button sourceButton) {
        EditText input = new EditText(this);
        input.setHint("Add a local demo comment");
        input.setTextSize(15);
        new AlertDialog.Builder(this)
                .setTitle("Comments")
                .setMessage("Comments are saved only as a local counter in this prototype.")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", (dialog, which) -> {
                    String comment = input.getText().toString().trim();
                    if (!comment.isEmpty()) {
                        int count = prefs.getInt("comments_" + post.id, post.comments) + 1;
                        prefs.edit().putInt("comments_" + post.id, count).apply();
                        sourceButton.setText("Comment (" + count + ")");
                        Toast.makeText(this, "Local demo comment added", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private List<Post> getPosts() {
        ArrayList<Post> posts = new ArrayList<>();
        posts.add(new Post("sample_1", "Mira", "A little pause between busy days.", "A moment of calm", TEAL, 128, 8));
        posts.add(new Post("sample_2", "Eko", "Found a new angle for today's tiny adventure.", "Weekend snippets", CORAL, 96, 12));
        posts.add(new Post("sample_3", "Poga", "The best plans sometimes start with no plan.", "Out and about", GOLD, 74, 5));

        String encoded = prefs.getString("custom_posts", "[]");
        try {
            JSONArray array = new JSONArray(encoded);
            ArrayList<Post> custom = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                custom.add(new Post(
                        object.optString("id", "custom_" + i),
                        "You",
                        object.optString("caption", ""),
                        "YOUR POST",
                        Color.rgb(31, 171, 157),
                        0,
                        0
                ));
            }
            Collections.reverse(custom);
            posts.addAll(0, custom);
        } catch (JSONException ignored) {
            prefs.edit().putString("custom_posts", "[]").apply();
        }
        return posts;
    }

    private void saveCustomPost(String caption) {
        JSONArray array;
        try {
            array = new JSONArray(prefs.getString("custom_posts", "[]"));
        } catch (JSONException e) {
            array = new JSONArray();
        }
        JSONObject object = new JSONObject();
        try {
            object.put("id", "custom_" + System.currentTimeMillis());
            object.put("caption", caption);
            array.put(object);
        } catch (JSONException ignored) {
            return;
        }
        prefs.edit().putString("custom_posts", array.toString()).apply();
    }

    private void addSectionTitle(String title) {
        TextView text = new TextView(this);
        text.setText(title);
        text.setTextColor(INK);
        text.setTextSize(23);
        text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        pageContent.addView(text, margin(2, 2, 0, 5));
    }

    private void addNote(String note) {
        addNoteTo(pageContent, note);
    }

    private void addNoteTo(LinearLayout parent, String note) {
        TextView text = new TextView(this);
        text.setText(note);
        text.setTextColor(MUTED);
        text.setTextSize(12);
        text.setPadding(dp(2), dp(3), dp(2), dp(8));
        parent.addView(text);
    }

    private Button primaryButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14);
        button.setBackground(round(TEAL, 13));
        return button;
    }

    private Button actionButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextAllCaps(false);
        button.setTextSize(11);
        button.setTextColor(INK);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(dp(2), 0, dp(2), 0);
        button.setBackgroundColor(Color.TRANSPARENT);
        return button;
    }

    private GradientDrawable round(int color, int radiusDp) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radiusDp));
        return shape;
    }

    private int lighten(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.rgb(
                Math.min(255, r + (255 - r) / 3),
                Math.min(255, g + (255 - g) / 3),
                Math.min(255, b + (255 - b) / 3)
        );
    }

    private LinearLayout.LayoutParams margin(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static class Post {
        final String id;
        final String creator;
        final String caption;
        final String mediaLabel;
        final int color;
        final int baseLikes;
        final int comments;

        Post(String id, String creator, String caption, String mediaLabel,
             int color, int baseLikes, int comments) {
            this.id = id;
            this.creator = creator;
            this.caption = caption;
            this.mediaLabel = mediaLabel;
            this.color = color;
            this.baseLikes = baseLikes;
            this.comments = comments;
        }
    }
}
