from app.schema.contents_response import TextTime
import librosa
import numpy as np

def sound_scoring(path:str, sentences:list[TextTime]):
    # WPM
    wpm = calculate_wpm(sentences)

    # wpm scoring
    if wpm < 110: wpm = 1.0
    elif wpm < 130: wpm = 0.8
    elif wpm < 160: wpm = 0.6
    elif wpm < 190: wpm = 0.4
    else: wpm = 0.2

    # Pause percentage
    ratio = pause_ratio(sentences)

    # pause ratio scoring
    if ratio < 0.05: ratio = 0.3
    elif ratio < 0.1: ratio = 0.6
    elif ratio <= 0.2: ratio = 1.0
    elif ratio <= 0.3: ratio = 0.6
    else: ratio = 0.3

    # Pitch
    pitch = estimate_pitch(path)
    if pitch >= 0.95: pitch = 1.0
    elif pitch >= 0.9: pitch = 0.8
    elif pitch >= 0.8: pitch = 0.6
    elif pitch >= 0.6: pitch = 0.4
    else: pitch = 0.2

    final_score = round(0.6 * wpm + 0.2 * ratio + 0.2 * pitch, 2)
    print(final_score)

    return final_score

# WPM < 110 : Very Easy
# WPM 110 ~ 130 : Easy
# WPM 130 ~ 160 : Medium
# WPM 160 ~ 190 : Hard
# WPM > 190 : Very Hard
def calculate_wpm(sentences: list[TextTime]) -> float:
    if not sentences:
        return 0.0

    total_words = sum(len(seg.text.strip().split()) for seg in sentences)

    total_time = sum(max(0.0, seg.end - seg.start) for seg in sentences)

    if total_time <= 0:
        return 0.0

    wpm = total_words / (total_time / 60)

    print(round(wpm, 2))
    return round(wpm, 2)

# Pause Ratio -> Speaking Density
def pause_ratio(sentences: list[TextTime]) -> float:
    if len(sentences) < 2:
        return 1.0

    pause_gaps = []
    for i in range(len(sentences) - 1):
        gap = sentences[i+1].start - sentences[i].end
        if gap > 0.2:
            pause_gaps.append(gap)

    total_pause_time = sum(pause_gaps)
    total_speaking_time = sum(seg.end - seg.start for seg in sentences)

    if total_speaking_time <= 0:
        return 1.0

    ratio = total_pause_time / (total_pause_time + total_speaking_time)
    return round(ratio, 2)

def estimate_pitch(path: str, sr=16000):
    y, sr = librosa.load(path, sr=sr)

    f0, _, _ = librosa.pyin(
        y,
        fmin=librosa.note_to_hz('C2'),
        fmax=librosa.note_to_hz('C7')
    )

    f0_clean = f0[~np.isnan(f0)]
    if len(f0_clean) == 0:
        return 0.0

    pitch_std = np.std(f0_clean)
    pitch_range = np.max(f0_clean) - np.min(f0_clean)

    # pitch Scoring
    max_std = 40.0
    max_range = 500.0

    norm_std = min(pitch_std / max_std, 1.0)
    norm_range = min(pitch_range / max_range, 1.0)

    score = round(0.7 * norm_std + 0.3 * norm_range, 2)
    print(score)

    return score