from fastapi import APIRouter, HTTPException
from app.modules.audio_downloader import download_audio

router = APIRouter()

@router.post("/extract_audio")
def extract_audio(url: str):
    try:
        output_file = download_audio(url)
        return {"status": "success", "audio": output_file}
    except Exception as e:
        return {"status": "error", "message": str(e)}