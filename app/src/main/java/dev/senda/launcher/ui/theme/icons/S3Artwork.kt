package dev.senda.launcher.ui.theme.icons

import dev.senda.launcher.R

/** Explicit reconstruction catalog: no generic circular badge fallback is approved for SIII. */
fun s3AppArtwork(packageName: String): Int? = when (packageName) {
    "com.google.android.dialer", "com.sec.android.app.dialertab" -> R.drawable.aura_s3_original_phone
    "com.android.settings" -> R.drawable.aura_s3_original_settings
    "com.motorola.camera3", "com.sec.android.app.camera" -> R.drawable.aura_s3_original_camera
    "com.sec.android.app.music", "com.android.music" -> R.drawable.aura_s3_original_music
    "com.google.android.contacts" -> R.drawable.aura_s3_period_contacts
    "com.google.android.deskclock" -> R.drawable.aura_s3_period_clock
    "com.google.android.calculator" -> R.drawable.aura_s3_period_calculator
    "com.google.android.apps.messaging" -> R.drawable.aura_s3_period_message
    "com.google.android.apps.nbu.files" -> R.drawable.aura_s3_period_files
    "com.google.android.apps.maps" -> R.drawable.aura_s3_period_maps
    "com.motorola.audiorecorder" -> R.drawable.aura_s3_period_recorder
    "com.motorola.stylus" -> R.drawable.aura_s3_period_notes
    else -> s3AiArtwork(packageName)
}
fun s3FunctionArtwork(icon: FamilyIcon): Int? = when (icon) {
    FamilyIcon.PHONE -> R.drawable.aura_s3_original_phone
    FamilyIcon.CAMERA -> R.drawable.aura_s3_original_camera
    FamilyIcon.SETTINGS -> R.drawable.aura_s3_original_settings
    FamilyIcon.MUSIC -> R.drawable.aura_s3_original_music
    FamilyIcon.CONTACTS -> R.drawable.aura_s3_period_contacts
    FamilyIcon.CLOCK -> R.drawable.aura_s3_period_clock
    FamilyIcon.CALCULATOR -> R.drawable.aura_s3_period_calculator
    FamilyIcon.MESSAGE -> R.drawable.aura_s3_period_message
    FamilyIcon.FILES -> R.drawable.aura_s3_period_files
    FamilyIcon.MAPS -> R.drawable.aura_s3_period_maps
    FamilyIcon.RECORDER -> R.drawable.aura_s3_period_recorder
    FamilyIcon.NOTES -> R.drawable.aura_s3_period_notes
    FamilyIcon.BROWSER -> R.drawable.aura_s3_period_browser
    FamilyIcon.GALLERY -> R.drawable.aura_s3_period_gallery
    FamilyIcon.RADIO -> R.drawable.aura_s3_period_radio
    FamilyIcon.DOWNLOADS -> R.drawable.aura_s3_period_downloads
    FamilyIcon.VIDEO -> R.drawable.aura_s3_period_video
    else -> null
}
