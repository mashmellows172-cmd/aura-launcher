package dev.aura.qa.catalog;

public class ProbeActivity extends android.app.Activity {
    @Override public void onCreate(android.os.Bundle state) {
        super.onCreate(state);
        android.widget.TextView label = new android.widget.TextView(this);
        label.setText("Aura QA Probe: apertura correcta");
        label.setTextSize(24);
        label.setGravity(android.view.Gravity.CENTER);
        setContentView(label);
    }
}
