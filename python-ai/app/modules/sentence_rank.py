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
    for score, sentence, idx in ranked:
        if len(sentence) > 30:
            count += 1
            results.append(TextRankResponse(index=idx, sentence=sentence))
            print(f"[{count}] (index: {idx}) {sentence}")

            if count >= top_n:
                break

    return results

def text_summa(text: str):
    summary_sentences = summarizer.summarize(text, ratio=0.2, split=True)

    for i, sentence in enumerate(summary_sentences, 1):
        print(f"[{i}] {sentence}")

def text_summarize(sentences: list[str]):
    joined_text = " ".join(sentence.strip() for sentence in sentences)

    summary_sentences = summarizer.summarize(joined_text, ratio=0.2)

    return summary_sentences