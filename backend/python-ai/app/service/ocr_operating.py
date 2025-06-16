from app.modules import text_to_speech
from app.modules import text_modify
from app.modules import grade_classification
from app.schema.contents_response import TTSResponse

def ocr_text_executing(text: str, file_name: str):
    # full_text -> sentence list
    sentences = text_modify.sentence_classification_spacy(text)

    print("문장 리스트:", sentences)

    # tts operation
    # US , GB , AU voice List
    tts_list = text_to_speech.tts_google(sentences, file_name)

    print("생성된 TTS 리스트:", tts_list)

    # Text Grade evaluation
    text_grade, _ = grade_classification.readability_evaluation(tts_list[0].text)
    print("text_grade:", text_grade)

    result = TTSResponse(
        grade = text_grade,
        contents = tts_list
    )
    return result

