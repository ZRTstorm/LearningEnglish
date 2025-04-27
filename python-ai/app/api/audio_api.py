from fastapi import APIRouter
from app.modules import audio_downloader
from app.modules import audio_segment
from app.service import ocr_operating
from app.service import text_operating
from app.service import text_translating

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

@router.get("/sound_segmentation")
def sound_segmentation(path: str):
    try:
        segment_stamp = audio_segment.vad_segment_silero(path)
        return {"status": "success", "stamp": segment_stamp}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/text_grade")
def text_grade_classification(path: str):
    try:
        score, details = text_operating.text_grading(path)
        scores = [score for _, score in details]
        return {"status": "success", "overall_score": score, "scores": scores}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/sound_grade")
def sound_grade(path: str):
    try:
        grade = text_operating.sound_grading(path)
        return {"status": "success", "sound_score": grade}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/text_to_speech")
def tts_api(text: str, file_name: str):
    try:
        result = ocr_operating.ocr_text_executing(text, file_name)
        return {"status": "success", "item": result}
    except Exception as e:
        return {"status": "error", "message": str(e)}

@router.get("/text_translation")
def translate_text(path: str):
    try:
        translated = text_translating.translate_operate(path)
        return {"status": "success", "translated": translated}
    except Exception as e:
        return {"status": "error", "message": str(e)}
