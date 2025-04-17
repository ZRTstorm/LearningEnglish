from app.modules import audio_downloader
from app.schema.contents_response import BasicResponse
from app.service import text_operating

def mani_contents(url: str):
    # download Contents Audio file
    path = audio_downloader.downlaod_audio_mp3(url)

    # Text Extracting Process
    text_sound_list = text_operating.text_processing_basic(path)

    return BasicResponse(file_path=path, text=text_sound_list)
