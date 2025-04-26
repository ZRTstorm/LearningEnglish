from pydantic import BaseModel

class TextTime(BaseModel):
    start: float
    end: float
    text: str

class BasicResponse(BaseModel):
    file_path: str
    text: list[TextTime]

class TTSResponse(BaseModel):
    grade: float
    contents: list[BasicResponse]

class AllContentsResponse(BaseModel):
    file_path: str
    text_grade: float
    sound_grade: float
    text: list[TextTime]
    translated: list[str]

class OcrContentsResponse(BaseModel):
    text_grade: float
    file_text: list[BasicResponse]
    translated: list[str]