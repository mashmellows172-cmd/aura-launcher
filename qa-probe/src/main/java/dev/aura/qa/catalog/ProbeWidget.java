package dev.aura.qa.catalog;

public class ProbeWidget extends android.appwidget.AppWidgetProvider {
    @Override public void onUpdate(android.content.Context context, android.appwidget.AppWidgetManager manager, int[] ids) {
        for (int id : ids) manager.updateAppWidget(id, new android.widget.RemoteViews(context.getPackageName(), R.layout.probe_widget));
    }
}
