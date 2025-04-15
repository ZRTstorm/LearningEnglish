import os.path

import yt_dlp
import requests
from io import StringIO
import webvtt

def download_audio(url: str) -> str:
    save_dir = "downloads"
    output_path = os.path.join(save_dir, "%(title)s.%(ext)s")

    ydl_opts = {
        "format": "bestaudio/best",
        'extractaudio' : True,
        'audioformat' : 'wav',
        'outtmpl': output_path,
        'postprocessors': [{
            'key': 'FFmpegExtractAudio',
            'preferredcodec': 'wav',
            'preferredquality': '256',
        }],
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=True)
        file_path = ydl.prepare_filename(info).replace(".webm", ".wav")

    return file_path

def downlaod_audio_mp3(url: str) -> str:
    save_dir = "downloads"
    output_path = os.path.join(save_dir, "%(title)s.%(ext)s")

    ydl_opts = {
        "format": "bestaudio/best",
        "postprocessors": [{
            'key': 'FFmpegExtractAudio',
            'preferredcodec': 'mp3',
            'preferredquality': '320',
        }],
        "outtmpl": output_path,
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=True)
        file_path = ydl.prepare_filename(info).replace(".webm", ".mp3")

    return file_path

def subtitle_list_vtt(vtt_text: str):
    buffer = StringIO(vtt_text)
    subtitles = []

    for caption in webvtt.read_buffer(buffer).captions:
        subtitles.append({
            "start": caption.start,
            "end": caption.end,
            "text": caption.text.strip()
        })
    return subtitles

def download_subtitles(url: str):

    ydl_opts = {
        'skip_download': True,
        'writesubtitles': True,
        'writeautomaticsub': True,
        'subtitleslangs': ['en'],
        "subtitlesformat": "vtt",
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=False)
        subs = info.get("subtitles", {})
        auto_subs = info.get("automatic_captions", {})

        subtitle_url = None

        for fmt in subs.get("en", []):
            if fmt["ext"] == "vtt":
                subtitle_url = fmt["url"]
                print("Subtitle is in YouTube")
                break

        if not subtitle_url:
            for fmt in auto_subs.get("en", []):
                if fmt["ext"] == "vtt":
                    subtitle_url = fmt["url"]
                    print("Automatic subtitle is in YouTube")
                    break

        if not subtitle_url:
            print("No subtitles found")
            return

        response = requests.get(subtitle_url)
        vtt_text = response.text

        if not vtt_text.strip().startswith("WEBVTT"):
            print("Subtitle is not Correct vtt format")
            return

        subtitles = subtitle_list_vtt(vtt_text)

        for subtitle in subtitles:
            print(f"{subtitle['start']} -- {subtitle['end']}: {subtitle['text']}")