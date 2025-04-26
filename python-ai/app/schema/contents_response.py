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