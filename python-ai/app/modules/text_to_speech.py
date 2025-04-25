import os
from google.cloud import texttospeech

# Service key Config
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "tts-service-account.json"

def tts_google(text, output_path="output.mp3"):
    client = texttospeech.TextToSpeechClient()

    input_text = texttospeech.SynthesisInput(text=text)

    voice = texttospeech.VoiceSelectionParams(
        language_code="en-US",
        ssml_gender=texttospeech.SsmlVoiceGender.FEMALE
    )

    audio_config = texttospeech.AudioConfig(
        audio_encoding=texttospeech.AudioEncoding.MP3,
    )

    response = client.synthesize_speech(
        input=input_text,
        voice=voice,
        audio_config=audio_config
    )

    with open(output_path, "wb") as out:
        out.write(response.audio_content)
        print("Audio content written to file '%s'" % output_path)