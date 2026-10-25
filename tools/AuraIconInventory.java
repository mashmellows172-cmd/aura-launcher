import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Read-only shell helper: no APK installation, permissions, preferences or app data access. */
public final class AuraIconInventory {
    private static void image(ZipOutputStream zip, String name, Drawable drawable) throws Exception {
        if (drawable == null) return;
        Bitmap bitmap = Bitmap.createBitmap(192, 192, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, 192, 192);
        drawable.draw(new Canvas(bitmap));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        zip.putNextEntry(new ZipEntry(name)); zip.write(bytes.toByteArray()); zip.closeEntry();
        bitmap.recycle();
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !args[0].startsWith("/data/local/tmp/aura-icon-"))
            throw new IllegalArgumentException("Expected a private temporary export path");
        if (Looper.myLooper() == null) Looper.prepare();
        Class<?> thread = Class.forName("android.app.ActivityThread");
        Object activity = thread.getMethod("systemMain").invoke(null);
        Context system = (Context) thread.getMethod("getSystemContext").invoke(activity);
        Context context = system.createPackageContext("com.android.shell", 0);
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
        JSONArray entries = new JSONArray();
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(args[0]))) {
            for (ResolveInfo info : apps) {
                String pkg = info.activityInfo.packageName;
                JSONObject item = new JSONObject().put("package", pkg)
                    .put("activity", info.activityInfo.name).put("label", info.loadLabel(pm).toString());
                try {
                    Drawable icon = info.loadIcon(pm);
                    image(zip, "icons/" + pkg + ".png", icon);
                    if (icon instanceof AdaptiveIconDrawable) {
                        AdaptiveIconDrawable adaptive = (AdaptiveIconDrawable) icon;
                        image(zip, "foreground/" + pkg + ".png", adaptive.getForeground());
                        if (android.os.Build.VERSION.SDK_INT >= 33 && adaptive.getMonochrome() != null)
                            image(zip, "monochrome/" + pkg + ".png", adaptive.getMonochrome());
                    }
                    item.put("iconStatus", "captured");
                } catch (Exception e) { item.put("iconStatus", "failed").put("error", e.getClass().getSimpleName()); }
                entries.put(item);
            }
            zip.putNextEntry(new ZipEntry("labels.json"));
            zip.write(new JSONObject().put("source", "Android PackageManager MAIN/LAUNCHER public icons")
                .put("entries", entries).toString(2).getBytes("UTF-8")); zip.closeEntry();
        }
        System.out.println("Captured " + entries.length() + " launcher entries");
    }
}
