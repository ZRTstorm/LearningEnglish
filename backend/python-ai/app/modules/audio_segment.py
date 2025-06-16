from pydub import AudioSegment
import silero_vad
import tempfile
import os
from app.schema.contents_response import TextTime


# sentence Speech Segment Extracting
def correct_sentence_segments(sentences: list[TextTime], voice_segments: list[tuple[float, float]]):
    for sentence in sentences:
        s_start, s_end = sentence.start, sentence.end

        overlaps = [
            (max(v_start, s_start), min(v_end, s_end))
            for v_start, v_end in voice_segments
            if v_end > s_start and v_start < s_end
        ]

        if not overlaps:
            continue

        sentence.start = round(min(start for start, _ in overlaps), 2)
        sentence.end = round(min(max(end for _, end in overlaps), s_end), 2)

# Person Audio Segmentation by silero_vad
def vad_segment_silero(path: str, min_duration=0.4, merge_threshold=0.3):
    audio = AudioSegment.from_mp3(path)
    audio = audio.set_channels(1).set_frame_rate(16000)

    with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as tmp_wav:
        wav_path = tmp_wav.name
        audio.export(wav_path, format="wav")

    try:
        model = silero_vad.load_silero_vad()

        wav = silero_vad.read_audio(wav_path, sampling_rate=16000)
        speech_timestamps = silero_vad.get_speech_timestamps(wav, model, sampling_rate=16000, return_seconds=True)

        segments = []
        for ts in speech_timestamps:
            start, end = ts['start'], ts['end']
            if end - start >= min_duration:
                segments.append((round(start, 2), round(end, 2)))

        merged = []
        for seg in segments:
            if not merged:
                merged.append(seg)
            else:
                prev_start, prev_end = merged[-1]
                curr_start, curr_end = seg

                if curr_start - prev_end < merge_threshold:
                    merged[-1] = (prev_start, curr_end)
                else:
                    merged.append(seg)

        print("Sound Segment : ", merged)
        return merged
    finally:
        os.remove(wav_path)
