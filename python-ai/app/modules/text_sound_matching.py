from datetime import datetime
import re

def matching_sentence(subtitles: list[dict[str, str]], sentences: list[str]) -> list[dict[str, float]]:
    # Step 1: 병합된 전체 자막 텍스트 및 단어 리스트 생성
    subtitle_texts = [s['text'].strip().replace('\n', ' ') for s in subtitles]
    full_text = " ".join(subtitle_texts)
    full_words = full_text.split()

    # Step 2: 자막별 단어 범위와 시간 범위 기록
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

    # Step 3: 문장을 전체 텍스트 기준으로 word 단위 인덱스로 찾기
    aligned_results = []
    word_index = 0
    for sentence in sentences:
        sentence_words = sentence.strip().split()
        sw_len = len(sentence_words)

        # full_words 에서 문장 위치 탐색 (정확한 위치)
        for i in range(word_index, len(full_words) - sw_len + 1):
            if full_words[i:i + sw_len] == sentence_words:
                sent_start_idx = i
                sent_end_idx = i + sw_len
                word_index = sent_end_idx
                break
        else:
            continue  # 문장을 못 찾으면 스킵

        # Step 4: 문장의 단어 인덱스 범위를 타임스탬프로 매핑
        sentence_start_time = None
        sentence_end_time = None
        accumulated = 0
        for wr in word_ranges:
            # 문장 시작 시간 계산
            if wr["start_idx"] <= sent_start_idx < wr["end_idx"]:
                offset = sent_start_idx - wr["start_idx"]
                per_word = wr["duration"] / wr["word_count"] if wr["word_count"] else 0
                sentence_start_time = wr["start_time"] + offset * per_word

            # 문장 끝 시간 계산
            if wr["start_idx"] < sent_end_idx <= wr["end_idx"]:
                offset = sent_end_idx - wr["start_idx"]
                per_word = wr["duration"] / wr["word_count"] if wr["word_count"] else 0
                sentence_end_time = wr["start_time"] + offset * per_word
                break

        if sentence_start_time is None:
            sentence_start_time = word_ranges[0]["start_time"]
        if sentence_end_time is None:
            sentence_end_time = word_ranges[-1]["end_time"]

        aligned_results.append({
            "start": round(sentence_start_time, 3),
            "end": round(sentence_end_time, 3),
            "text": sentence
        })

    for i, item in enumerate(aligned_results, 1):
        print(f"{i:02d} | {item['start']} ~ {item['end']} | {item['text']}")

    return aligned_results

def clean_and_split(text: str) -> list[str]:
    # ' 는 유지하되, 나머지 구두점 제거
    text = re.sub(r"[^\w'\s]", '', text)
    return text.strip().split()

def align_sentences_by_exact_words(subtitles: list[dict[str, str]], sentences: list[str]) -> list[dict[str, float]]:
    # 전체 자막 텍스트를 단어 기준으로 정리
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

    results = []
    word_index = 0

    for sentence in sentences:
        words = clean_and_split(sentence)
        n = len(words)

        # 정확히 일치하는 위치 탐색
        found = False
        for i in range(word_index, len(subtitle_words) - n + 1):
            if subtitle_words[i:i+n] == words:
                start_time = word_time_map[i]
                end_time = word_time_map[i + n - 1] + (
                    word_time_map[i + n - 1] - word_time_map[i + n - 2] if n > 1 else 0.5)
                results.append({
                    "start": round(start_time, 3),
                    "end": round(end_time, 3),
                    "text": sentence
                })
                word_index = i + n  # 다음 검색 시작 위치 이동
                found = True
                break

        if not found:
            print(f" 문장 매칭 실패: {sentence}")

    for i, item in enumerate(results, 1):
        print(f"{i:02d} | {item['start']} ~ {item['end']} | {item['text']}")

    return results

def time_to_sec(t: str) -> float:
    # Convert timestamp 'HH:MM:SS.sss' to Second as Float
    try:
        dt = datetime.strptime(t, "%H:%M:%S.%f")
    except ValueError:
        dt = datetime.strptime(t, "%H:%M:%S")
    return dt.hour * 3600 + dt.minute * 60 + dt.second + dt.microsecond / 1e6