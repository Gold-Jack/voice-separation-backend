from asteroid.models import BaseModel, DPRNNTasNet
import soundfile as sf
import sys

# 'from_pretrained' automatically uses the right model class (asteroid.models.DPRNNTasNet).
model = DPRNNTasNet.from_pretrained("E:\\IDEA-Workspace\\voice-separation-backend\\asteroid\\"
                                    "DPRNNTasNet-ks2_Libri1Mix_enhsingle_16k\\pytorch_model.bin")

# # You can pass a NumPy array:
# mixture, _ = sf.read("female-female-mixture.wav", dtype="float32", always_2d=True)
# # Soundfile returns the mixture as shape (time, channels), and Asteroid expects (batch, channels, time)
# mixture = mixture.transpose()
# mixture = mixture.reshape(1, mixture.shape[0], mixture.shape[1])
# out_wavs = model.separate(mixture)

# Or simply a file name:
model.separate(sys.argv[1], resample=True)
