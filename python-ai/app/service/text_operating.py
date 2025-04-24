from app.modules import audio_downloader
from app.modules import sound_to_text
from app.modules import text_modify
from app.modules import text_sound_matching
from app.schema.contents_response import BasicResponse

# Extracting exist subtitle
def exist_subtitle(url: str):
    # extract subtitles for vtt format
    subtitles = audio_downloader.download_subtitles(url)

    # delete duplicate sentences
    full_text = text_modify.duplicate_sentences(subtitles)

    # sentence classification
    sentences = text_modify.sentence_classification_spacy(full_text)

    # (sentence , timestamp) Matching
    return text_sound_matching.matching_sentence(subtitles, sentences)

# Basic audio file processing
def text_processing_basic(path: str):
    # STT OpenAI Whisper
    transcript = sound_to_text.translate_audio_openai(path)

    # Duplicate Sentence Cleaning
    full_text = text_modify.paste_sentences(transcript)

    # Sentence Classification
    sentence_list = text_modify.sentence_classification_spacy(full_text)

    # Sentence - Sound TimeStamp Matching
    matching_list = text_sound_matching.matching_sentence(transcript, sentence_list)

    return BasicResponse(file_path=path, text=matching_list)

# Music audio file processing
def text_processing_music(path: str):
    # STT OpenAI Whisper
    transcript = sound_to_text.translate_audio_openai(path)

    # Duplicate Sentence Cleaning
    full_text = text_modify.paste_sentences(transcript)

    # Restore fullstop Punctuation
    restore_text = text_modify.punctuation_restore(full_text)

    # Sentence Splitting by [.!?,]
    sentence_list = text_modify.sentence_spliter(restore_text)

    # Sentence - Sound TimeStamp Matching
    matching_list = text_sound_matching.matching_sentence_words(transcript, sentence_list)

    return BasicResponse(file_path=path, text=matching_list)
