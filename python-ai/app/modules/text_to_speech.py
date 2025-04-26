import os
from google.cloud import texttospeech
from google.cloud import texttospeech_v1beta1 as texttospeech
from pydub import AudioSegment
from app.schema.contents_response import TextTime, BasicResponse

# Service key Config
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "tts-service-account.json"

def tts_google(sentences:list[str], output_prefix:str) -> list[BasicResponse]:
    languages = {
        "EN": "en-US",
        "GB": "en-GB",
        "AU": "en-AU"
    }
    # Make sentenceList to SSML
    ssml = make_ssml(sentences)

    client = texttospeech.TextToSpeechClient()

    result: list[BasicResponse] = []
    for suffix, lang_code in languages.items():
        synthesis_input = texttospeech.SynthesisInput(ssml=ssml)

        voice = texttospeech.VoiceSelectionParams(
            language_code = lang_code,
            ssml_gender = texttospeech.SsmlVoiceGender.NEUTRAL
        )

        audio_config = texttospeech.AudioConfig(audio_encoding=texttospeech.AudioEncoding.LINEAR16)

        request = texttospeech.SynthesizeSpeechRequest(
            input=synthesis_input,
            voice=voice,
            audio_config=audio_config,
            enable_time_pointing=[texttospeech.SynthesizeSpeechRequest.TimepointType.SSML_MARK]
        )

        response = client.synthesize_speech(request=request)

        wav_file_name = f"{output_prefix}-{suffix}.wav"
        mp3_file_name = f"{output_prefix}-{suffix}.mp3"

        with open(wav_file_name, "wb") as output_file:
            output_file.write(response.audio_content)

        audio = AudioSegment.from_wav(wav_file_name)
        duration_sec = len(audio) / 1000.0
        audio.export(mp3_file_name, format="mp3", bitrate="320k")
        os.remove(wav_file_name)

        timestamps: list[TextTime] = []
        timepoints = response.timepoints

        for i, tp in enumerate(timepoints):
            start = tp.time_seconds
            end = timepoints[i+1].time_seconds if i+1 < len(timepoints) else duration_sec

            timestamps.append(TextTime(
                start = round(start, 2),
                end = round(end, 2),
                text = sentences[i],
            ))

        basic_response = BasicResponse(
            file_path = mp3_file_name,
            text = timestamps,
        )
        result.append(basic_response)

    return result

def make_ssml(sentences: list[str]):
    ssml = "<speak>\n"

    for i, sentence in enumerate(sentences):
        ssml += f'<mark name="s{i}"/>{sentence} '
    ssml += "\n</speak>"

    return ssml