import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import numpy as np

from app.schema.contents_response import TextTime

# Model Loading
model_name = "agentlans/deberta-v3-base-readability-v2"
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForSequenceClassification.from_pretrained(model_name)
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
model = model.to(device)

# Grade Evaluation
def readability_evaluation(sentence_list: list[TextTime]):
    chunks = chunk_sentences(sentence_list)
    scores, weights = [], []

    for chunk in chunks:
        inputs = tokenizer(chunk, return_tensors="pt", truncation=True, padding=True, max_length=512).to(device)
        with torch.no_grad():
            logits = model(**inputs).logits.squeeze().cpu()
        score = logits.item()

        scores.append(score)
        weights.append(len(tokenizer.tokenize(chunk)))

    overall_score = np.average(scores, weights=weights)
    return round(overall_score, 2), list(zip(chunks, scores))

# Chunk Generating
def chunk_sentences(sentences: list[TextTime], max_tokens: int = 400):
    chunks, current_chunk = [], []
    total_tokens = 0

    for sentence in sentences:
        tokenized = tokenizer.tokenize(sentence.text)
        token_count = len(tokenized)

        if total_tokens + token_count > max_tokens:
            chunks.append(" ".join(current_chunk))
            current_chunk = [sentence.text]
            total_tokens = token_count
        else:
            current_chunk.append(sentence.text)
            total_tokens += token_count

    if current_chunk:
        chunks.append(" ".join(current_chunk))

    return chunks
