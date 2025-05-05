from sklearn.feature_extraction.text import TfidfVectorizer
from summa import summarizer
import numpy as np

from app.schema.contents_response import TextRankResponse

def text_rank_tf(sentences: list[str]):

    vectorizer = TfidfVectorizer()
    x = vectorizer.fit_transform(sentences)

    importance_scores = np.linalg.norm(x.toarray(), axis=1)

    ranked = sorted(zip(importance_scores, sentences, range(len(sentences))), reverse=True)
    results: list[TextRankResponse] = []

    count = 0
    top_n = 10
    for i, (score, sentence, idx) in enumerate(ranked):
        if len(sentence) > 40:
            count += 1
            results.append(TextRankResponse(index=idx, sentence=sentence))
            print(f"[{count}] (index: {idx}) {sentence}")

            if count >= top_n or i == len(ranked) - 1:
                break

    return results

def text_summa(text: str):
    summary_sentences = summarizer.summarize(text, ratio=0.2, split=True)

    for i, sentence in enumerate(summary_sentences, 1):
        print(f"[{i}] {sentence}")

def text_summarize(sentences: list[str]):
    joined_text = " ".join(sentence.strip() for sentence in sentences)
    total_sentences = len(sentences)

    target_min = 10
    target_max = 30

    if total_sentences <= target_min:
        return sentences
    elif total_sentences <= target_max:
        ratio = 0.5
    elif total_sentences <= 50:
        ratio = 0.3
    elif total_sentences <= 100:
        ratio = 0.2
    else:
        ratio = 0.1

    summary_sentences = summarizer.summarize(joined_text, ratio=ratio, split=True)
    for i, sentence in enumerate(summary_sentences, 1):
        print(f"[{i}] {sentence}")

    return summary_sentences
