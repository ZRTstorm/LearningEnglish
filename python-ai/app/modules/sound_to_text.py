import whisper

def translate_audio(file_path: str) -> str:
    model = whisper.load_model("medium.en")
    result = model.transcribe(file_path)
    return result["text"]
