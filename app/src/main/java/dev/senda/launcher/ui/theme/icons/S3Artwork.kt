package dev.senda.launcher.ui.theme.icons

import dev.senda.launcher.R

/** Explicit reconstruction catalog: no generic circular badge fallback is approved for SIII. */
fun s3AppArtwork(packageName: String): Int? = when (packageName) {
    "com.google.android.dialer", "com.sec.android.app.dialertab" -> R.drawable.aura_s3_original_phone
    "com.android.settings" -> R.drawable.aura_s3_original_settings
    "com.motorola.camera3", "com.sec.android.app.camera" -> R.drawable.aura_s3_original_camera
    "com.sec.android.app.music", "com.android.music" -> R.drawable.aura_s3_original_music
    "com.openai.chatgpt" -> R.drawable.aura_s3_ai_chatgpt
    "com.anthropic.claude" -> R.drawable.aura_s3_ai_claude
    "com.google.android.apps.bard" -> R.drawable.aura_s3_ai_gemini
    else -> null
}
fun s3FunctionArtwork(icon: FamilyIcon): Int? = when (icon) {
    FamilyIcon.PHONE -> R.drawable.aura_s3_original_phone
    FamilyIcon.CAMERA -> R.drawable.aura_s3_original_camera
    FamilyIcon.SETTINGS -> R.drawable.aura_s3_original_settings
    FamilyIcon.MUSIC -> R.drawable.aura_s3_original_music
    else -> null
}
