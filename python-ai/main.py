from fastapi import FastAPI
from app.api import basicRoot, audio_api, contents_serving

app = FastAPI()

app.include_router(basicRoot.router)
app.include_router(audio_api.router, prefix="/audio", tags=["audio"])
app.include_router(contents_serving.router, prefix="/contents", tags=["contents"])