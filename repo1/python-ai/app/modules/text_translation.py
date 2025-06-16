import requests

#API Secret
client_id = "nmofi7v6h9"
client_secret = "4HUOrzygCVRHOq6K8wM8kIoZmkLeBIyutQ7UsQtw"

SEPARATOR = " ## "

def translate_sentences(sentences: list[str]) -> list[str]:
    grouped_text = group_sentences(sentences)
    translated_sentences = []

    for text in grouped_text:
        translated_text = papago_translate(text)
        parts = translated_text.split(SEPARATOR)
        cleaned_parts = [part.strip() for part in parts if part.strip()]
        translated_sentences.extend(cleaned_parts)

    return translated_sentences

def group_sentences(sentences:list[str], max_chars=4000):
    groups = []
    current = ""

    for sentence in sentences:
        appended = current + (SEPARATOR if current else "") + sentence

        if len(appended) > max_chars:
            if current:
                groups.append(current)
            current = sentence
        else:
            current = appended

    if current:
        groups.append(current)

    return groups

def papago_translate(text:str, source: str="en", target: str="ko") -> str:
    url = "https://papago.apigw.ntruss.com/nmt/v1/translation"
    headers = {
        'X-NCP-APIGW-API-KEY-ID': client_id,
        'X-NCP-APIGW-API-KEY': client_secret,
    }
    data = {
        "source": source,
        "target": target,
        "text": text,
        "honorific": "true"
    }

    response = requests.post(url, headers=headers, data=data)
    if response.status_code == 200:
        result = response.json()
        return result['message']['result']['translatedText']
    else:
        raise Exception(f"Error: {response.status_code} - {response.text}")