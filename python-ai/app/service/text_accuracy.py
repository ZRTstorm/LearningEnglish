import jiwer
from app.modules.sound_to_text import translate_audio_openai_text

# Daylight Original Text
daylight_text = (
    "Telling myself I won't go there "
    "Oh, but I know that I won't care "
    "Tryna wash away all the blood I've spilt "
    "This lust is a burden that we both share "
    "Two sinners can't atone from a lone prayer "
    "Souls tied, intertwined by our pride and guilt "
    "There's darkness in the distance "
    "From the way that I've been livin' "
    "But I know I can't resist it "
    "Oh, I love it and I hate it at the same time "
    "You and I drink the poison from the same vine "
    "Oh, I love it and I hate it at the same time "
    "Hidin' all of our sins from the daylight "
    "From the daylight, runnin' from the daylight "
    "From the daylight, runnin' from the daylight "
    "Oh, I love it and I hate it at the same time "
    "Tellin' myself it's the last time "
    "Can you spare any mercy that you might find "
    "If I'm down on my knees again "
    "Deep down, way down, Lord, I try "
    "Try to follow your light, but it's night time "
    "Please, don't leave me in the end "
    "There's darkness in the distance "
    "I'm beggin' for forgiveness ooh "
    "But I know I might resist it, oh "
    "Oh, I love it and I hate it at the same time "
    "You and I drink the poison from the same vine "
    "Oh, I love it and I hate it at the same time "
    "Hidin' all of our sins from the daylight "
    "From the daylight, runnin' from the daylight "
    "From the daylight, runnin' from the daylight "
    "Oh, I love it and I hate it at the same time "
    "Oh, I love it and I hate it at the same time "
    "You and I drink the poison from the same vine "
    "Oh, I love it and I hate it at the same time "
    "Hidin' all of our sins from the daylight "
    "From the daylight, runnin' from the daylight "
    "From the daylight, runnin' from the daylight "
    "Oh, I love it and I hate it at the same time "
)

# Take Me To Church Original Text
church_text = (
    "My lover's got humor "
    "She's the giggle at a funeral "
    "Knows everybody's disapproval "
    "I should've worshiped her sooner "
    "If the Heavens ever did speak "
    "She's the last true mouthpiece "
    "Every Sunday's getting more bleak "
    "A fresh poison each week "
    "We were born sick, you heard them say it "
    "My church offers no absolutes "
    "She tells me, Worship in the bedroom "
    "The only Heaven I'll be sent to "
    "Is when I'm alone with you "
    "I was born sick, but I love it "
    "Command me to be well "
    "A , Amen, Amen, Amen "
    "Take me to church "
    "I'll worship like a dog at the shrine of your lies "
    "I'll tell you my sins and you can sharpen your knife "
    "Offer me that deathless death "
    "Good God, let me give you my life "
    "Take me to church "
    "I'll worship like a dog at the shrine of your lies "
    "I'll tell you my sins and you can sharpen your knife "
    "Offer me that deathless death "
    "Good God, let me give you my life "
    "If I'm a pagan of the good times "
    "My lover's the sunlight "
    "To keep the Goddess on my side "
    "She demands a sacrifice Drain the whole sea "
    "Get something shiny "
    "Something meaty for the main course "
    "That's a fine looking high horse "
    "What you got in the stable "
    "We've a lot of starving faithful "
    "That looks tasty That looks plenty This is hungry work "
    "Take me to church "
    "I'll worship like a dog at the shrine of your lies "
    "I'll tell you my sins so you can sharpen your knife "
    "Offer me that deathless death "
    "Good God, let me give you my life "
    "Take me to church "
    "I'll worship like a dog at the shrine of your lies "
    "I'll tell you my sins so you can sharpen your knife "
    "Offer me that deathless death "
    "Good God, let me give you my life "
    "No masters or kings when the ritual begins "
    "There is no sweeter innocence than our gentle sin "
    "In the madness and soil of that sad earthly scene "
    "Only then I am human Only then I am clean Oh, oh, Amen, Amen, Amen "
    "Take me to church "
    "I'll worship like a dog at the shrine of your lies "
    "I'll tell you my sins and you can sharpen your knife "
    "Offer me that deathless death "
    "Good God, let me give you my life "
    "Take me to church "
    "I'll worship like a dog at the shrine of your lies "
    "I'll tell you my sins and you can sharpen your knife "
    "Offer me that deathless death "
    "Good God, let me give you my life "
)

transform = jiwer.Compose([
    jiwer.RemovePunctuation(),
    jiwer.ToLowerCase(),
    jiwer.RemoveWhiteSpace(replace_by_space=True),
    jiwer.RemoveEmptyStrings()
])

# Compare text accuracy by WER rate
# 0.00 ~ 0.10 Error rate
def text_accuracy(path: str) -> float:
    text = translate_audio_openai_text(path)

    original_text = transform(daylight_text)
    replaced_text = transform(text)

    accuracy = jiwer.wer(original_text, replaced_text)
    print("WER: ", accuracy)

    return accuracy