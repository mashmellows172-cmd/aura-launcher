package dev.senda.launcher.ui.theme.icons

import dev.senda.launcher.data.LauncherTheme

/** Explicit system-function aliases only. A package containing "music" is not proof of its identity. */
private val systemIcons: Map<String, FamilyIcon> = buildMap {
    fun aliases(icon: FamilyIcon, vararg packages: String) = packages.forEach { put(it, icon) }
    aliases(FamilyIcon.PHONE, "com.android.dialer", "com.google.android.dialer", "com.htc.htcdialer", "com.samsung.android.dialer", "com.sonyericsson.android.socialphonebook.dialer")
    aliases(FamilyIcon.MESSAGE, "com.android.mms", "com.android.messaging", "com.google.android.apps.messaging", "com.samsung.android.messaging", "com.sonyericsson.conversations")
    aliases(FamilyIcon.CAMERA, "com.android.camera", "com.android.camera2", "com.motorola.camera", "com.motorola.camera3", "com.sec.android.app.camera", "com.htc.camera", "com.lge.camera", "com.sonyericsson.android.camera")
    aliases(FamilyIcon.BROWSER, "com.android.browser", "com.sec.android.app.sbrowser", "com.htc.sense.browser")
    aliases(FamilyIcon.MUSIC, "com.android.music", "com.htc.music", "com.lge.music", "com.sonyericsson.music", "com.sec.android.app.music")
    aliases(FamilyIcon.CALENDAR, "com.android.calendar", "com.google.android.calendar", "com.samsung.android.calendar", "com.htc.calendar")
    aliases(FamilyIcon.SETTINGS, "com.android.settings")
    aliases(FamilyIcon.FILES, "com.android.documentsui", "com.google.android.documentsui", "com.sec.android.app.myfiles", "com.motorola.filemanager")
    aliases(FamilyIcon.CONTACTS, "com.android.contacts", "com.google.android.contacts", "com.samsung.android.contacts", "com.sonyericsson.android.socialphonebook")
    aliases(FamilyIcon.CLOCK, "com.android.deskclock", "com.google.android.deskclock", "com.sec.android.app.clockpackage", "com.htc.android.worldclock", "com.sonyericsson.organizer")
    aliases(FamilyIcon.GALLERY, "com.android.gallery", "com.android.gallery3d", "com.htc.album", "com.sec.android.gallery3d", "com.sonyericsson.album", "com.lge.gallery")
    aliases(FamilyIcon.CALCULATOR, "com.android.calculator2", "com.google.android.calculator", "com.sec.android.app.popupcalculator", "com.htc.calculator", "com.sonyericsson.calculator")
    aliases(FamilyIcon.EMAIL, "com.android.email", "com.htc.android.mail", "com.lge.email")
    aliases(FamilyIcon.VIDEO, "com.android.video", "com.sonyericsson.video", "com.htc.video", "com.sec.android.app.videoplayer")
    aliases(FamilyIcon.MAPS, "com.android.maps")
    aliases(FamilyIcon.NOTES, "com.htc.notes", "com.lge.qmemoplus", "com.sonymobile.notes")
    aliases(FamilyIcon.RECORDER, "com.android.soundrecorder", "com.motorola.audiorecorder", "com.sec.android.app.voicenote", "com.htc.soundrecorder")
    aliases(FamilyIcon.RADIO, "com.motorola.fmplayer", "com.android.fmradio", "com.htc.fm", "com.sonyericsson.fmradio")
    aliases(FamilyIcon.WEATHER, "com.motorola.weather", "com.htc.weather", "com.lge.sizechangable.weather")
    aliases(FamilyIcon.DOWNLOADS, "com.android.providers.downloads.ui")
}

fun familyIconFor(packageName: String): FamilyIcon? = systemIcons[packageName]

val iconFamilyThemes = listOf(LauncherTheme.NOTHING, LauncherTheme.SENSE, LauncherTheme.TOUCHWIZ,
    LauncherTheme.OPTIMUS, LauncherTheme.XPERIA, LauncherTheme.RAZR)
