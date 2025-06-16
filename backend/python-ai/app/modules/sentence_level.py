from pron_difficulty import PronDifficulty
import re

ALPHA = 0.7

def level_detection(sentence: str):
    evaluator = PronDifficulty()

    words = [word for word in re.findall(r"[a-zA-Z']+", sentence.lower()) if len(word) > 2]
    if not words:
        return 0.0

    scores = evaluator.evaluate_batch(words, "en")
    word_score_pairs = list(zip(words, scores))

    top_pairs = sorted(word_score_pairs, key=lambda x: x[1], reverse=True)[:10]
    top_scores = [s for _, s in top_pairs]

    avg_score = sum(top_scores) / len(top_scores)
    max_score = max(top_scores)

    base_score = ALPHA * avg_score + (1 - ALPHA) * max_score
    return round(min(base_score, 1.0), 2)