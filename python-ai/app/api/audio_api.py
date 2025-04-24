from fastapi import APIRouter, HTTPException
from app.modules import audio_downloader
from app.service import text_operating
from app.service import contents_extract

router = APIRouter()

@router.get("/extract_audio")
def extract_audio(url: str):
    try:
        output_file = audio_downloader.downlaod_audio_mp3(url)
        return {"status": "success", "audio": output_file}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/extract_subtitles")
def extract_subtitles(url: str):
    try:
        subtitles = text_operating.exist_subtitle(url)
        return {"status": "success", "subtitles": subtitles}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/text_processing")
def text_processing(path: str):
    try:
        text = text_operating.text_processing_basic(path)
        return {"status": "success", "contents": text}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/contents_all")
def contents_all(url: str):
    try:
        contents = contents_extract.contents_all_operation(url)
        return {"status": "success", "contents": contents}
    except Exception as e:
        return {"status": "error", "message": str(e)}