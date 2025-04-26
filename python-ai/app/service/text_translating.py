from app.modules import text_translation

def translate_test():
    sentences = [
        "Hello, how are you?",
        "I hope you are having a good day.",
        "This is a sample sentence for translation testing.",
        "Another long sentence that could contribute to exceeding the 5000 characters if repeated enough times."
    ]

    translated = text_translation.translate_sentences(sentences)

    for idx, sentence in enumerate(translated):
        print(f"{idx + 1}: {sentence}")
