from app.modules import text_translation
from app.modules import sound_to_text
from app.modules import text_modify

def translate_test():
    sentences = [
        "Hello, how are you?",
        "I hope you are having a good day.",
        "This is a sample sentence for translation testing.",
        "Another long sentence that could contribute to exceeding the 5000 characters if repeated enough times."
    ]

    translated = text_translation.translate_sentences(sentences)

    for idx, sentence in enumerate(translated):
        print(f"{idx + 1}: {sentence}")

def translate_operate(path: str):
    transcript = sound_to_text.translate_audio_openai(path)

    full_text = text_modify.paste_sentences(transcript)

    sentences = text_modify.sentence_classification_spacy(full_text)

    translated = text_translation.translate_sentences(sentences)

    for idx, sentence in enumerate(translated):
        print(f"{idx + 1}: {sentence}")

    return translated