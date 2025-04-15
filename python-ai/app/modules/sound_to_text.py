import whisper
import openai

def translate_audio(file_path: str) -> str:
    model = whisper.load_model("base.en")
    result = model.transcribe(file_path)
    return result["text"]

openai.api_key = (
    "sk-proj-Zi6uGv30JvJoLsp1dreI4pt9ERRnVBOmKMATxOot_XCsMyRz-"
    "SqD0mTdhKVsmo7Ff6NGXEHCEmT3BlbkFJsJ9Swi_8R9b43m5dhHDOd7fV"
    "BqCB18JEyeY5KxGdpqPEyztKL7RzPl34CfUKyPVWgKQkTiC04A"
)

def translate_audio_openai(file_path: str) -> str:
    with open(file_path, "rb") as audio_file:
        client = openai.OpenAI(api_key=openai.api_key)

        transript = client.audio.transcriptions.create(
            model="whisper-1",
            file=audio_file,
            language="en",
            response_format="json"
        )

    predicted_text = transript.text
    return predicted_text