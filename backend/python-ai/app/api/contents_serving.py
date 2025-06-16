from fastapi import APIRouter
from pydantic import BaseModel

from app.service import contents_extract
from app.modules import sentence_level

router = APIRouter()

class SentenceRequests(BaseModel):
    sentences: list[str]

@router.get("/audio_all_contents")
def audio_all_contents(url: str):
    try:
        response_content = contents_extract.contents_all_operation(url)
        return {"status": "success", "content": response_content}
    except Exception as e:
        return {"status": "error", "content": str(e)}

@router.get("/ocr_all_contents")
def ocr_all_contents(text: str, name: str):
    try:
        response_content = contents_extract.ocr_all_operation(text, name)
        return {"status": "success", "content": response_content}
    except Exception as e:
        return {"status": "error", "content": str(e)}

@router.post("/sentence_ranked_contents")
def sentence_ranked_contents(request: SentenceRequests):
    try:
        ranked = contents_extract.sentence_rank_operation(request.sentences)
        return {"status": "success", "content": {"rankSentences": ranked}}
    except Exception as e:
        return {"status": "error", "content": str(e)}

@router.post("/summarize_contents")
def summarize_contents(request: SentenceRequests):
    try:
        summarization = contents_extract.text_summarize_operation(request.sentences)
        return {"status": "success", "content": {"summaSentences": summarization}}
    except Exception as e:
        return {"status": "error", "content": str(e)}

@router.get("/speech_grade")
def speech_detection(text: str):
    try:
        speech_grade = sentence_level.level_detection(text)
        return {"status": "success", "grade": speech_grade}
    except Exception as e:
        return {"status": "error", "content": str(e)}
