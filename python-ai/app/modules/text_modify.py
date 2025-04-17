import spacy
import re
from deepmultilingualpunctuation import PunctuationModel

# Sentence Classification by spaCy
def sentence_classification_spacy(full_text: str):
    # spacy nlp sentence classification model
    nlp = spacy.load("en_core_web_sm")

    doc = nlp(full_text)

    return [sent.text.strip() for sent in doc.sents]

# Restore fullstop Punctuation
def punctuation_restore(full_text: str):
    model = PunctuationModel()
    result = model.restore_punctuation(full_text)

    return result

# Sentence Splitter
def sentence_spliter(full_text: str):
    base_sentences = re.split(r'(?<=[.!?])\s+', full_text.strip())

    final_sentences = []
    min_len = 5

    for base in base_sentences:
        parts = [p.strip() for p in base.split(",") if p.strip()]

        current = ""
        for i, part in enumerate(parts):
            if len(part) < min_len:
                if current:
                    current += ", " + part
                else:
                    current = part
            else:
                if current:
                    current += ", " + part
                    final_sentences.append(current.strip())
                    current = ""
                else:
                    final_sentences.append(part.strip())
        if current:
            final_sentences.append(current.strip())

    return final_sentences


# Remove Duplicate sentences in VTT Format
def duplicate_sentences(subtitles: list[dict[str, str]]) -> str:
    result = []
    prev_text = ""

    for subtitle in subtitles:
        text = subtitle["text"].strip().replace("\n", " ")

        new_part = common_suffix(prev_text, text)

        if new_part:
            result.append(new_part)
            prev_text += " " + new_part

    return " ".join(result)

def paste_sentences(subtitles: list[dict[str, str]]) -> str:
    result = []

    for subtitle in subtitles:
        text = subtitle["text"].strip().replace("\n", " ")
        result.append(text)

    return " ".join(result)

def common_suffix(prev:str, curr:str) -> str:
    max_len = min(len(prev), len(curr))

    for i in range(max_len, 0, -1):
        if prev.endswith(curr[:i]):
            return curr[i:].strip()

    return curr.strip()

