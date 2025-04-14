import os.path

import yt_dlp

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