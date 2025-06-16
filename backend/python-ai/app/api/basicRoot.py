from fastapi import APIRouter
import torch

router = APIRouter()

@router.get("/")
def read_root():
    return {"message" : "Welcome to English BackPart"}

@router.get("/items/{item_id}")
def read_item(item_id: int, q: str = None):
    return {"item_id": item_id, "q": q}

@router.get("/checkings")
def read_checkings():
    print(torch.cuda.is_available())
    print(torch.cuda.get_device_name(0))