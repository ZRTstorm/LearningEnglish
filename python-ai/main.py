from fastapi import FastAPI
from app.api import basicRoot, audio_api

app = FastAPI()

app.include_router(basicRoot.router)
app.include_router(audio_api.router, prefix="/audio", tags=["audio"])