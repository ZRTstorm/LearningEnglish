from datetime import datetime
import re
from app.schema.contents_response import TextTime

# (Sentence , Timestamp) Matching
def matching_sentence(subtitles: list[dict[str, str]], sentences: list[str]) -> list[TextTime]:
    # All Subtitle Texts & Words List
    subtitle_texts = [s['text'].strip().replace('\n', ' ') for s in subtitles]
    full_text = " ".join(subtitle_texts)
    full_words = full_text.split()

    # Words Range & TimeStamp Range
    word_ranges = []
    word_cursor = 0

    for s in subtitles:
        words = s['text'].strip().replace('\n', ' ').split()
        word_count = len(words)
        start_sec = time_to_sec(s['start'])
        end_sec = time_to_sec(s['end'])

        word_ranges.append({
            "start_idx": word_cursor,
            "end_idx": word_cursor + word_count,
            "start_time": start_sec,
            "end_time": end_sec,
            "duration": end_sec - start_sec,
            "word_count": word_count
        })
        word_cursor += word_count

    # Searching per Words index
    aligned_results: list[TextTime] = []
    word_index = 0

    for sentence in sentences:
        sentence_words = sentence.strip().split()
        sw_len = len(sentence_words)

        for i in range(word_index, len(full_words) - sw_len + 1):
            if full_words[i:i + sw_len] == sentence_words:
                sent_start_idx = i
                sent_end_idx = i + sw_len
                word_index = sent_end_idx
                break
        else:
            continue

        # Sentence - TimeStamp Mapping
        sentence_start_time = None
        sentence_end_time = None
        accumulated = 0

        for wr in word_ranges:
            # Sentence Start Time
            if wr["start_idx"] <= sent_start_idx < wr["end_idx"]:
                offset = sent_start_idx - wr["start_idx"]
                per_word = wr["duration"] / wr["word_count"] if wr["word_count"] else 0
                sentence_start_time = wr["start_time"] + offset * per_word

            # Sentence End Time
            if wr["start_idx"] < sent_end_idx <= wr["end_idx"]:
                offset = sent_end_idx - wr["start_idx"]
                per_word = wr["duration"] / wr["word_count"] if wr["word_count"] else 0
                sentence_end_time = wr["start_time"] + offset * per_word
                break

        if sentence_start_time is None:
            sentence_start_time = word_ranges[0]["start_time"]
        if sentence_end_time is None:
            sentence_end_time = word_ranges[-1]["end_time"]

        aligned_results.append(TextTime(
            start = round(sentence_start_time, 3),
            end = round(sentence_end_time, 3),
            text = sentence
        ))

    return aligned_results

# (Sentence , Timestamp) Matching by words
def matching_sentence_words(subtitles: list[dict[str, str]], sentences: list[str]) -> list[TextTime]:
    subtitle_words = []
    word_time_map = []

    for s in subtitles:
        start_sec = time_to_sec(s['start'])
        end_sec = time_to_sec(s['end'])
        words = clean_and_split(s['text'])
        duration = end_sec - start_sec

        if not words:
            continue
        per_word_time = duration / len(words)

        for i, word in enumerate(words):
            timestamp = start_sec + i * per_word_time
            subtitle_words.append(word)
            word_time_map.append(timestamp)

    word_index = 0
    aligned_results: list[TextTime] = []

    for sentence in sentences:
        words = clean_and_split(sentence)
        n = len(words)

        found = False
        for i in range(word_index, len(subtitle_words) - n + 1):
            if subtitle_words[i:i+n] == words:
                start_time = word_time_map[i]
                end_time = word_time_map[i + n - 1] + (
                    word_time_map[i + n - 1] - word_time_map[i + n - 2] if n > 1 else 0.5)

                aligned_results.append(TextTime(
                    start = round(start_time, 3),
                    end = round(end_time, 3),
                    text = sentence
                ))
                word_index = i + n
                found = True
                break

        if not found:
            print(f" Failure to Sentence matching : {sentence}")

    return aligned_results

# Timestamp to Second
def time_to_sec(t: str) -> float:
    # Convert timestamp 'HH:MM:SS.sss' to Second as Float
    try:
        dt = datetime.strptime(t, "%H:%M:%S.%f")
    except ValueError:
        dt = datetime.strptime(t, "%H:%M:%S")
    return dt.hour * 3600 + dt.minute * 60 + dt.second + dt.microsecond / 1e6

# Text split to words
def clean_and_split(text: str) -> list[str]:
    text = re.sub(r"[^\w'\s]", '', text)
    return text.strip().split()