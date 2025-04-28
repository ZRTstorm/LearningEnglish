from app.modules import audio_downloader
from app.modules import sound_to_text
from app.modules import text_modify
from app.modules import text_sound_matching
from app.modules import audio_segment
from app.modules import grade_classification
from app.modules import voice_grade
from app.modules import text_translation
from app.modules import sentence_rank
from app.service import ocr_operating
from app.schema.contents_response import BasicResponse, AllContentsResponse, OcrContentsResponse


# 1. URL -> audio file extracting
# 2. Audio file -> STT
# 3. VTT -> Sentence extracing
# 4. Sentence + VTT -> Sentence , Timestamp matching
# 5. Audio -> Speech Voice Segment Extracting
# 6. Sentence , Timestamp + Speech Voice Segment matching
# 7. Sentence List -> Text grade Evaluation
# 8. Audio file -> Sound grade Evaluation
# 9. Sentence List -> (En, Ko) Translation
def contents_all_operation(url: str):
    # Audio Download
    path = audio_downloader.downlaod_audio_mp3(url)

    # STT
    vtt_text = sound_to_text.translate_audio_openai(path)

    # sentence extracting
    full_text = text_modify.paste_sentences(vtt_text)
    sentence_list = text_modify.sentence_classification_spacy(full_text)

    # sentence , timestamp matching
    text_sound_list = text_sound_matching.matching_sentence(vtt_text, sentence_list)

    # Audio voice Segmentation
    voice_list = audio_segment.vad_segment_silero(path)

    # Voice , Sentence matching
    audio_segment.correct_sentence_segments(text_sound_list, voice_list)

    # Text grade Evaluation
    text_score, details = grade_classification.readability_evaluation(text_sound_list)

    # Sound grade Evaluation
    sound_score = voice_grade.sound_scoring(path, text_sound_list)

    # Translation En -> Ko
    off_trans = [item.text for item in text_sound_list]
    translated = text_translation.translate_sentences(off_trans)

    response = AllContentsResponse(file_path=path,
                                   text_grade=text_score,
                                   sound_grade=sound_score,
                                   text=text_sound_list,
                                   translated=translated)

    return response

def ocr_all_operation(text: str, name: str):
    # Text_grade , (file_path , TextTime) list
    exec_result = ocr_operating.ocr_text_executing(text, name)

    # Text_Translation
    contents_list = exec_result.contents
    sentence_list = contents_list[0].text
    sentences = [item.text for item in sentence_list]
    translated = text_translation.translate_sentences(sentences)

    response = OcrContentsResponse(text_grade=exec_result.text_grade,
                                   file_text=exec_result.contents,
                                   translated=translated)
    return response

def sentence_rank_operation(sentences: list[str]):
    results = sentence_rank.text_rank_tf(sentences)

    return results

def text_summarize_operation(sentences: list[str]):
    summarize = sentence_rank.text_summarize(sentences)

    return summarize
