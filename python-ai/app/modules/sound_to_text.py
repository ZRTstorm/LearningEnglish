import whisper
import openai
from app.modules.audio_downloader import subtitle_list_vtt

# Local Whisper STT Model -> base.en ~ medium.en
def translate_audio(file_path: str) -> str:
    model = whisper.load_model("base.en")
    result = model.transcribe(file_path)
    return result["text"]

# OpenAI API Key
openai.api_key = (
    "sk-proj-Zi6uGv30JvJoLsp1dreI4pt9ERRnVBOmKMATxOot_XCsMyRz-"
    "SqD0mTdhKVsmo7Ff6NGXEHCEmT3BlbkFJsJ9Swi_8R9b43m5dhHDOd7fV"
    "BqCB18JEyeY5KxGdpqPEyztKL7RzPl34CfUKyPVWgKQkTiC04A"
)

# OpenAI Whipser API -> Whisper-1
def translate_audio_openai(file_path: str) -> list[dict[str, str]]:
    with open(file_path, "rb") as audio_file:
        client = openai.OpenAI(api_key=openai.api_key)

        transript = client.audio.transcriptions.create(
            model="whisper-1",
            file=audio_file,
            language="en",
            response_format="vtt"
        )

    subtitles = subtitle_list_vtt(transript)
    remove_hallucination(subtitles)

    for subtitle in subtitles:
        print(f"{subtitle['start']} -- {subtitle['end']}: {subtitle['text']}")

    return subtitles

# Hallucination ending PostProcessing
def remove_hallucination(subtitles: list[dict[str, str]]) -> list[dict[str, str]]:
    hallucination = [
        "thank you", "for watching", "like and subscribe",
        "see you next", "sponsored by", "check out", "link in the description",
        "transcript", "description", "i hope you", "i'll see you"
    ]

    if not subtitles:
        return subtitles

    last_subtitle = subtitles[-1]["text"].strip().lower()

    if any(key in last_subtitle for key in hallucination):
        print("hallucination detected: ", subtitles[-1]["text"])
        subtitles.pop()

    return subtitles