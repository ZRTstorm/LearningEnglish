from fastapi import APIRouter, HTTPException
from app.modules import audio_downloader
from app.service import text_operating
from app.service import contents_extract

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
        text_operating.text_manipulate(url)
        return {"status": "success"}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/translate")
def translate_text(path: str):
    try:
        text = text_operating.text_processing_music(path)
        return {"status": "success", "text": text}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/contents_basic")
def contents_basic(url: str):
    try:
        contents = contents_extract.mani_contents(url)
        return {"status": "success", "contents": contents}
    except Exception as e:
        return {"status": "error", "message": str(e)}