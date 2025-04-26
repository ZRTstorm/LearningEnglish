from fastapi import APIRouter
from app.service import contents_extract

router = APIRouter()

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
