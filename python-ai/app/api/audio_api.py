from fastapi import APIRouter, HTTPException
from app.modules import audio_downloader
from app.modules.sound_to_text import translate_audio
from app.service.text_accuracy import text_accuracy

router = APIRouter()

@router.post("/extract_audio")
def extract_audio(url: str):
    try:
        output_file = audio_downloader.downlaod_audio_mp3(url)
        return {"status": "success", "audio": output_file}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/extract_subtitles")
def extract_subtitles(url: str):
    try:
        audio_downloader.download_subtitles(url)
        return {"status": "success"}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/translate")
def translate_text(path: str):
    try:
        text = text_accuracy(path)
        return {"status": "success", "text": text}
    except Exception as e:
        return {"status": "error", "message": str(e)}
