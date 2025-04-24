from app.modules import audio_downloader
from app.modules import sound_to_text
from app.modules import text_modify
from app.modules import text_sound_matching
from app.schema.contents_response import BasicResponse

# 1. URL -> audio file extracting
# 2. Audio file -> STT
# 3. VTT -> Sentence extracing
# 4. Sentence + VTT -> Sentence , Timestamp matching
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

    return BasicResponse(file_path=path, text=text_sound_list)
