from pron_difficulty import PronDifficulty
import re

ALPHA = 0.7

def level_detection(sentence: str):
    evaluator = PronDifficulty()

    words = [word for word in re.findall(r"[a-zA-Z']+", sentence.lower()) if len(word) > 2]
    if not words:
        return 0.0

    scores = evaluator.evaluate_batch(words, "en")
    for word, score in zip(words, scores):
        print(f"{word}: {round(score, 2)}")

    avg_score = sum(scores) / len(scores)
    max_score = max(scores)

    base_score = ALPHA * avg_score + (1 - ALPHA) * max_score
    return round(min(base_score, 1.0), 2)