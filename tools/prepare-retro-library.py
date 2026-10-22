"""Build an optional Aura library from a user-owned asset directory.

Requires Pillow. No downloads, audio, APK code, manufacturer images or fonts are
bundled with Aura's open source repository. Output belongs to the user's local
collection. The filenames below are selection rules, not authenticity claims.
"""
import argparse
import hashlib
import io
import json
from pathlib import Path
import zipfile
from PIL import Image, ImageOps

SOURCES = {
    'SENSE': ('HTC_One_M7', 'Colección HTC', 'HTC2.png',
              'Fondos de tu colección. Los iconos de esta carpeta mezclan AOSP/CyanogenMod; Aura conserva sus ilustraciones para Sense.'),
    'TOUCHWIZ': ('Samsung_Galaxy_S3', 'Colección Samsung', 'hd_01.jpg',
                 'Seis iconos de aplicaciones seleccionados. Se excluyen botones internos. Los fondos de la colección mezclan distintas generaciones.'),
    'OPTIMUS': ('LG_G2', 'Colección LG', 'LG9.png',
                'Fondos de tu colección. Los archivos de iconos son controles internos y no equivalen a iconos completos de LG; Aura conserva sus ilustraciones.'),
    'XPERIA': ('Sony_Xperia_Z1', 'Colección Xperia', 'Sony3.png',
               'Iconos y fondos seleccionados de tu colección; incluye diseños posteriores al Z1. No se certifica un firmware ni año exactos.'),
}

SAMSUNG = {
    'PHONE': 'SecPhone_ic_launcher_phone.png',
    'CAMERA': 'SamsungCamera_tw30_icon_camera.png',
    'VIDEO': 'SamsungCamera_tw30_icon_camcorder.png',
    'MUSIC': 'MusicPlayer_homescreen_icon_musicplayer.png',
    'SETTINGS': 'SecPhone_ic_launcher_settings.png',
    'STORE': 'SecLauncher2_ic_launcher_market_holo.png',
}

SONY = {
    'PHONE': 'com_android_contacts_activities_dialtactsactivity.png',
    'CAMERA': 'com_android_camera.png', 'BROWSER': 'com_android_browser.png',
    'MUSIC': 'com_android_mediacenter.png', 'VIDEO': 'com_android_videoplayer.png',
    'MESSAGE': 'com_android_messenger.png', 'CONTACTS': 'com_android_contacts.png',
    'CLOCK': 'com_semc_clock.png', 'CALENDAR': 'com_sonyericsson_calendar.png',
    'SETTINGS': 'com_android_settings.png', 'FILES': 'com_android_providers_downloads_ui.png',
    'GALLERY': 'com_android_gallery3d.png', 'CALCULATOR': 'com_android_calculator2.png',
    'STORE': 'com_android_vending.png', 'EMAIL': 'com_google_android_gm.png',
    'MAPS': 'com_google_android_apps_maps.png', 'NOTES': 'com_google_android_keep.png',
    'WEATHER': 'com_semc_weather.png', 'RADIO': 'com_android_fmradio.png',
    'RECORDER': 'com_android_soundrecorder.png',
}

# Exact package matches preserve third-party identity; category artwork fills only
# known equivalents. Source spelling errors are never guessed into app mappings.
SONY_PACKAGES = {
    'com.android.chrome': 'com_android_chrome.png',
    'com.google.android.apps.books': 'com_google_android_apps_books.png',
    'com.google.android.apps.chromecast.app': 'com_google_android_apps_chromecast_app.png',
    'com.google.android.apps.docs': 'com_google_android_apps_docs.png',
    'com.google.android.apps.maps': 'com_google_android_apps_maps.png',
    'com.google.android.apps.photos': 'com_google_android_apps_photos.png',
    'com.google.android.apps.tachyon': 'com_google_android_apps_tachyon.png',
    'com.google.android.apps.translate': 'com_google_android_apps_translate.png',
    'com.google.android.calendar': 'com_google_android_calendar.png',
    'com.google.android.gm': 'com_google_android_gm.png',
    'com.google.android.googlequicksearchbox': 'com_google_android_googlequicksearchbox.png',
    'com.google.android.keep': 'com_google_android_keep.png',
    'com.google.android.music': 'com_google_android_music.png',
    'com.google.android.play.games': 'com_google_android_play_games.png',
    'com.google.android.videos': 'com_google_android_videos.png',
    'com.google.android.youtube': 'com_google_android_youtube.png',
    'com.facebook.katana': 'com_facebook_katana.png',
    'com.instagram.android': 'com_instagram_android.png',
    'com.linkedin.android': 'com_linkedin_android.png',
    'com.microsoft.office.outlook': 'com_microsoft_office_outlook.png',
    'com.Slack': 'com_slack.png', 'com.whatsapp': 'com_whatsapp.png',
    'hr.dtakac.prognoza': 'com_semc_weather.png',
    'com.motorola.fmplayer': 'com_android_fmradio.png',
    'com.google.android.apps.recorder': 'com_android_soundrecorder.png',
}


def image_bytes(path, icon):
    with Image.open(path) as original:
        if original.width > 8192 or original.height > 8192:
            raise ValueError(f'Image too large: {path.name}')
        image = ImageOps.exif_transpose(original).convert('RGBA' if icon else 'RGB')
        image.thumbnail((256, 256) if icon else (1920, 1920), Image.Resampling.LANCZOS)
        output = io.BytesIO()
        if icon:
            image.save(output, 'PNG', optimize=True)
        else:
            image.save(output, 'WEBP', quality=88, method=6)
        return output.getvalue()


def build(assets, destination):
    manifest = {'format': 'aura-retro-library', 'version': 1, 'themes': {}}
    report = {}
    destination.parent.mkdir(parents=True, exist_ok=True)
    # Output is explicitly supplied by the user; source files are read only.
    with zipfile.ZipFile(destination, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
        for theme, (folder, label, first_wall, note) in SOURCES.items():
            source = assets / folder
            walls = sorted(p for p in (source / 'wallpapers').iterdir() if p.suffix.lower() in ('.png', '.jpg', '.jpeg', '.webp'))
            walls.sort(key=lambda p: p.name != first_wall)
            if not walls or walls[0].name != first_wall:
                raise ValueError(f'Missing representative wallpaper: {folder}/{first_wall}')
            item = {'label': label, 'note': note, 'icons': {}, 'packages': {}, 'wallpapers': []}
            audit = {'icons': [], 'wallpapers': []}
            for index, wall in enumerate(walls):
                name = f'{theme}/wallpapers/wall_{index:02d}.webp'
                data = image_bytes(wall, False)
                archive.writestr(name, data)
                item['wallpapers'].append({'label': wall.stem, 'path': name})
                audit['wallpapers'].append({'source': wall.name, 'path': name, 'sha256': hashlib.sha256(wall.read_bytes()).hexdigest(), 'bytes': len(data)})
            categories = SAMSUNG if theme == 'TOUCHWIZ' else SONY if theme == 'XPERIA' else {}
            packages = SONY_PACKAGES if theme == 'XPERIA' else {}
            filenames = sorted(set(categories.values()) | set(packages.values()))
            names = {}
            for index, filename in enumerate(filenames):
                path = source / 'icons' / filename
                name = f'{theme}/icons/icon_{index:02d}.png'
                data = image_bytes(path, True)
                archive.writestr(name, data)
                names[filename] = name
                audit['icons'].append({'source': filename, 'path': name, 'sha256': hashlib.sha256(path.read_bytes()).hexdigest(), 'bytes': len(data)})
            item['icons'] = {key: names[filename] for key, filename in categories.items()}
            item['packages'] = {key: names[filename] for key, filename in packages.items()}
            manifest['themes'][theme] = item
            report[theme] = audit
        archive.writestr('manifest.json', json.dumps(manifest, ensure_ascii=False, indent=2))
    destination.with_suffix('.report.json').write_text(json.dumps(report, indent=2), encoding='utf-8')
    print(json.dumps({'file': str(destination), 'bytes': destination.stat().st_size, 'themes': {theme: {key: len(value) for key, value in audit.items()} for theme, audit in report.items()}}, ensure_ascii=True))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--assets', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args()
    build(args.assets.resolve(strict=True), args.output.resolve())
