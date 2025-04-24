from pydub import AudioSegment
import webrtcvad
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

# Person Audio Segmentation by webrtcvad
def vad_segment_timestamp(path: str):
    audio_bytes, sample_rate, audio_length = mp3_to_pcm(path)
    vad = webrtcvad.Vad(2)

    segments = []
    frames = list(frame_generator(audio_bytes, sample_rate))
    duration_per_frame = 30 / 1000.0

    start_time = None

    time = 0.0
    for i, frame in enumerate(frames):
        is_speech = vad.is_speech(frame, sample_rate)
        time = i * duration_per_frame

        if is_speech:
            if start_time is None:
                start_time = time
        else:
            if start_time is not None:
                segments.append((start_time, time))
                start_time = None

    if start_time is not None:
        segments.append((start_time, time))

    rounded_segments = [(round(start_time, 2), round(time, 2)) for start_time, time in segments]

    print("Sound Segment : ", rounded_segments)
    return rounded_segments

# mp3 to pcm low byte
def mp3_to_pcm(path: str, sample_rate=16000):
    audio = AudioSegment.from_mp3(path)
    audio = audio.set_channels(1).set_frame_rate(sample_rate)
    raw_audio = audio.raw_data

    return raw_audio, sample_rate, len(audio) / 1000

# frame generator
def frame_generator(audio_bytes, sample_rate, frame_length=30):
    frame_size = int(sample_rate * frame_length / 1000) * 2
    offset = 0

    while offset + frame_size < len(audio_bytes):
        yield audio_bytes[offset:offset + frame_size]
        offset += frame_size
