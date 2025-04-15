from fastapi import APIRouter, HTTPException
from app.modules.audio_downloader import download_audio
from app.modules.sound_to_text import translate_audio

router = APIRouter()

@router.post("/extract_audio")
def extract_audio(url: str):
    try:
        output_file = download_audio(url)
        return {"status": "success", "audio": output_file}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/translate")
def translate_text(path: str):
    try:
        text = translate_audio(path)
        return {"status": "success", "text": text}
    except Exception as e:
        return {"status": "error", "message": str(e)}